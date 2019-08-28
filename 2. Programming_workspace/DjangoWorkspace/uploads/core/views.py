from django.shortcuts import render, redirect
from django.conf import settings
from django.core.files.storage import FileSystemStorage

from uploads.core.models import Document
from uploads.core.forms import DocumentForm

def home(request):
    documents = Document.objects.all()
    return render(request, 'core/home.html', { 'documents': documents })

"""
여기가 file upload main code. myfile이라는 이름으로 저장되는듯
★★★ ToDoList
1) simple_upload.html에서 html input태그를 적절하게 만들어서 지금처럼 버튼클릭을 통한 업로드가 아닌 url을 통한 직접 전달 방식으로 변경. myfile에 저장
   흐음.. 전달방식은 Restful Api로 실험해보자.
2) views.py에서 myfile에 있는 wav파일을 열어서 librosa로 변환한 뒤 웹 상에 출력
3) librosa에 대해 keras로 만든 .h5 파일로 class score 출력.
4) class index를 요청한 어플리케이션에게 반환
5) 임의로 저장해둔 myfile이 충돌되면 안되므로 삭제시킨다.
"""
def simple_upload(request):
    if request.method == 'POST' and request.FILES['myfile']:
        myfile = request.FILES['myfile']
        fs = FileSystemStorage()
        filename = fs.save(myfile.name, myfile)
        uploaded_file_url = fs.url(filename)
        return render(request, 'core/simple_upload.html', {
            'uploaded_file_url': uploaded_file_url
        })
    return render(request, 'core/simple_upload.html')


def model_form_upload(request):
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            form.save()
            return redirect('home')
    else:
        form = DocumentForm()
    return render(request, 'core/model_form_upload.html', {
        'form': form
    })
