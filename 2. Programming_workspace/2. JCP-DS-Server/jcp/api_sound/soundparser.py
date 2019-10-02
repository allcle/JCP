import librosa
import librosa.display
import numpy as np
import matplotlib.pyplot as plt
from PIL import Image


class SoundParser:
    def parseWavToPILImage(wavFile, file_name):
        
        ## 넘어온 파일을 librosa로 로드
        y, sr = librosa.load(wavFile)
        print("##### librosa library load success! : " + file_name)

        ## librosa로 이미지 변환 후 저장
        fig = plt.figure()
        S = np.abs(librosa.stft(y))
        librosa.display.specshow(librosa.power_to_db(S**2, ref=np.max), sr = sr, y_axis = 'log', x_axis='time')
        plt.title(file_name)

        # plt 이미지 pil 형식 변환 및 가장자리 제거
        canvas = plt.get_current_fig_manager().canvas
        canvas.draw()
        pil_image = Image.frombytes('RGB', canvas.get_width_height(), canvas.tostring_rgb())

        plt.close()

        # Crop
        # 80,57, 577, 429
        area = (80, 57, 577, 429)
        pil_image = pil_image.crop(area)
        print("##### PIL Image converted")

        return pil_image