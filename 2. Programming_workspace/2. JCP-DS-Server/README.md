# JCP-DS-Server

##### JCP Danger Sound Server : JCP 프로젝트 청각장애인을 위한 위험소리 분류모델 서버

~~**GCE(임시) + Ubuntu18.04 + Django + Nginx 로 운영중입니다.**~~

**AWS + Ubuntu18.04 + Django + Nginx 로 운영중입니다.** 

- Server IP : 52.79.242.64
- Server URL : https://ec2-52-79-242-64.ap-northeast-2.compute.amazonaws.com
- JCP Repository : https://github.com/allcle/JCP



### Install - ubuntu
```
# For Ubuntu18.04
# 클론
$ git clone THIS_REPOSITORY_URL
$ cd CLONED_DIRECTORY

# 가상환경 생성
$ python3 -m venv env
$ source env/bin/activate

# 가상환경에 라이브러리 설치
# tensorflow, keras 는 다운그레이드 버전에서 실행 - views.py 주석 참고
#   - 최신버전 오류발생 : https://github.com/keras-team/keras/issues/13336

(env)$ pip install django
(env)$ pip install djangorestframework
(env)$ pip install librosa
(env)$ pip install tensorflow==1.13.1
(env)$ pip install keras==2.2.4
(env)$ pip install pillow
(env)$ pip install matplotlib

# 테스트 서버 실행 확인
$ python manage.py runserver 0.0.0.0:8000

# Nginx, uWSGI 연동방법
 - https://uwsgi-docs.readthedocs.io/en/latest/tutorials/Django_and_nginx.html
 - https://twpower.github.io/41-connect-nginx-uwsgi-django
 
# 실 서버 가동(Nginx, uWSGI 환경 설정 완료상태)
(env)$ uwsgi --socket :8001 --module jcp.wsgi

# Nginx + SSL 처리방법 - CA 없이 openssl 사용
- https://blog.iwanhae.ga/nginx-ssl-https/


```
### Installing Error
1. **OSError: sndfile library not fond**
> 간혹 클라우드 인스턴스에 기본 사운드 라이브러리가 설치가 안되어 있을 수 있습니다.
> 관련 라이브러리를 apt-get 설치해주시면 됩니다.
> 참고자료(StackOverflow) : https://stackoverflow.com/questions/55086834/cant-import-soundfile-python/55086878  
```
$ sudo apt-get install libsndfile1
```
2. **접속 시도 중 Invalid HTTP_HOST header: '18.216.246.234:8000'.
You may need to add '18.216.246.234' to ALLOWED_HOSTS. 에러로그 발생 시**
> settings.py 의 ALLOWED_HOSTS 값에 본 서버의 호스트 주소가 누락되었습니다.
> 설치할 서버의 IP 주소를 추가해주면 됩니다.
```
# To debug 허용된 호스트 - 안드로이드 가상환경에서는 localhost 를 10.0.2.2 로 접근한다.
# 임시 GCE 서버 IP : 35.223.183.56
# 한이음 AWS 서버 IP : 52.79.242.64
# 한이음 DNS name : ec2-52-79-242-64.ap-northeast-2.compute.amazonaws.com
ALLOWED_HOSTS = ['10.0.2.2', 'localhost', '35.233.183.56', '52.79.242.64', 
'ec2-52-79-242-64.ap-northeast-2.compute.amazonaws.com']
```
3. **uwsgi 설치 중 fatal error: Python.h: No such file or directory 오류 발생**
> uwsgi 설치 중 <Python.h> 모듈을 불러 올 수 없어서 그렇습니다. python3-dev 패키지를 설치하면 해결됩니다.
```
$ sudo apt-get install python3-dev
(env)$ pip install uwsgi 
```



## API Usage

|   Request Type    |            Request Value             |
| :---------------: | :----------------------------------: |
|     `Method`      |                `POST`                |
|       `URL`       | `https://ec2-52-79-242-64.ap-northeast-2.compute.amazonaws.com/uploads/` |
|    **HEADER**     |                                      |
|  `Content-Type`   |        `multipart/form-data`         |
|     **BODY**      |                                      |
|      `file`       |        `음성 파일 업로드.wav`        |
| **Response BODY** |          **Response Value**          |
|     `result`      |         `소리 인덱스(0 ~ 5)`         |
|      `sound`      |        `소리 분류( ** 소리)`         |



### Request Sample - cURL

```
$ curl -X POST \
  https://ec2-18-216-246-234.us-east-2.compute.amazonaws.com/uploads/ \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@dog_bark35.wav
  
{ "result": 2, "sound": "드릴 소리" }
```



### Request Sample - HTTP

```
POST /uploads/ HTTP/1.1
Content-Length: 300108
Host: https://ec2-52-79-242-64.ap-northeast-2.compute.amazonaws.com/
Content-Type: multipart/form-data;boundary=------FormBoundaryShouldDifferAtRuntime

------FormBoundaryShouldDifferAtRuntime
Content-Disposition: form-data; name="file"; filename="drilling3.wav"
Content-Type: audio/wav

[message-part-body; type: audio/wav, size: 299924 bytes]
------FormBoundaryShouldDifferAtRuntime--

HTTP/1.1 200 OK
date: Sun, 29 Sep 2019 20:23:59 GMT
allow: OPTIONS, POST
server: WSGIServer/0.2 CPython/3.6.8
x-frame-options: SAMEORIGIN
content-length: 36
vary: Accept, Cookie
content-type: application/json

{"result":2,"sound":"드릴 소리"}
```



###  Request sample - Restlet Client

### ![requestsample](./readme_request.png)