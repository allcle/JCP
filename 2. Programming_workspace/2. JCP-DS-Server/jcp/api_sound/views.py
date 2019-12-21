# Django_Rest_Framework
from rest_framework.parsers import FileUploadParser, MultiPartParser, FormParser
from rest_framework.response import Response
from rest_framework.decorators import api_view, parser_classes
from django.http import HttpResponse

import os
from django.conf import settings
from django.core.files.storage import default_storage
from django.core.files.base import ContentFile

# Converting library
from jcp.api_sound.soundparser import SoundParser

# Processing library
from jcp.api_sound.preprocessor import PreProcessor

# Modeling library
from jcp.api_sound.modelpredict import ModelPredict

# Time library for performance profiling
import time

"""

    아래부터는 이미지 전처리 및 CNN 사용 방식에 수정이 요망되는 부분.

    이미지 전처리 작업

    Warning!
        
    현재 tensorflow 1.14 에서 predict 오류 발생

    AttributeError: '_thread._local' object has no attribute 'value'
        
    Stackoverflow issue : https://github.com/keras-team/keras/issues/13336
    8일 전 이슈, predict 함수 사용 시 내부에서 문제가 생기는 듯 함.
    텐서플로우[1.13.1], 케라스[2.2.4] 버전으로 다운그레이드 하여 테스트.

"""
"""
# -----------------------------------------------
# 사전에 모델 로드 해놓는다.
"""
# 버전 확인
import tensorflow as tf
import keras
from keras.models import load_model
import glob

print("##### Tenserflow version : " + tf.__version__)
print("##### Keras version : " + keras.__version__)
models = list()
filelist = glob.glob('./classifier/*.h5')
for file in filelist:
    model = load_model(file)
    model._make_predict_function()
    models.append(model)
    print("##### Load keras model : " + file)

# 전역 설정
image_width = 331
image_height = 221
channels = 3
strSound = ["경적 소리", "개 짓는 소리", "드릴 소리", "총 소리", "사이렌 소리", "아무 소리 아님"]

"""
# -----------------------------------------------
"""
"""

    request 방법
    
    @REQUEST URL         : http://우리서버:8000/uploads/
    @REQUEST METHOD      : POST
    @CONTENT-TYPE        : audio/wav
    @CONTENT-DISPOSITION : attachment; filename=파일이름.wav
    @REQUEST BODY        : 파일경로 업로드.

"""

@api_view(['POST'])
# @parser_classes((FileUploadParser,))
@parser_classes((MultiPartParser, FormParser,))
def classifySound(request):

    ## 클라이언트에서 요청 바디의 파일을 받아온다
    f = request.data['file']
    file_name = f.name
    print("##### Requested sound data : " + file_name + " at ")
    print(time.time())
    print(f)
    print(type(f))

    # path = default_storage.save('recorded.wav', ContentFile(f.read()))
    # tmp_file = os.path.join(settings.MEDIA_ROOT, path)
    # print("file saved!")

    ## 음성 파일을 파싱하여 PIL 포맷 이미지로 받음
    pil_image = SoundParser.parseWavToPILImage(f, file_name)

    ## PIL 포맷 이미지를 np.ndarray 형태로 전처리
    x_test = PreProcessor.processImageToXTrain(pil_image, image_width, image_height, channels)

    ## 전처리 데이터를 앙상블 방식으로 예상값 도출
    result = ModelPredict().ensemble(models, x_test)

    return Response({"result": result, "sound": strSound[result]})

@api_view(['GET'])
def helloworld(request):
    return HttpResponse("<h1>Helloworld! this server is running!</h1>")