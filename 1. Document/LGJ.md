# Raspberry pi 구현 과정

## raspberry pi 다운 및 환경설정
1. rasberian을 다운 받는다 (링크 : https://www.raspberrypi.org/downloads/)
2. 다운받은 이미지 파일을 컴퓨터를 통해 SD카드에 설치한다 (링크 : https://geeksvoyage.com/raspberry%20pi/installing-os-for-pi/)
3. SD카드를 raspberry pi 에 넣은 후, 전원을 켜서 기본적 세팅을 한다. 이 때 개인적으로 모든 언어를 영어로 하는 것을 추천한다.

## raspberry pi에 웹캠 연결
1. 웹캠을 연결한다
2. lsusb라는 명령어를 통해 연결되었는지 확인한다.
3. sudo raspi-config를 통해 활성화를 해준다 (링크 : https://webnautes.tistory.com/909)
4. cheese라는 프로그램으로 웹캘 활성화를 확인
(다만 VNC의 경우 따로 설정을 해주어야 한다.)

## face detection
1. 기본적으로 얼굴을 detecting하는 소스를 받아야한다. (링크 : )
2. 이것은 read만 가능함으로 sudo chmod 777을 활용하여 권한을 바꾸어준다.
3. detect되었을 때, 그 부분만 가져오는 코드를 짠다.

## cry detection
1. 먼저 crawling한 데이터를 준비한다
2. keras를 통해 pc에서 mobile net을 구현한다 (on device 형태로 구현할 예정이므로 연산량이 적어야한다. 이 때 mobile net이 제일 좋다.)
3. 모든 파라미터를 h5파일로 저장한 후, raspberry pi에서 load 후, 실행

## connection between raspberry pi and application
1. 
