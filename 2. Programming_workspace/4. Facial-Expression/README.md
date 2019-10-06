# Raspberry pi 구현 과정

#### ============================================================
<one line to give the program's name and an idea of what it does.>
Copyright (C) <2019> <JCP>

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
#### ============================================================

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
3. parameter들=> 즉, model.save를 h5파일로 한다.

## keras on raspberry pi
1. keras를 raspberry pi에 다운로드 한다 (링크 : https://nextus.tistory.com/18?category=762131)
2. aspberry pi에서 h5를 load 후, 실행

## connection between raspberry pi and application
1. socket : ref)
- http://blog.naver.com/cosmosjs/220714273636
- http://www.masterqna.com/android/88918/%EB%9D%BC%EC%A6%88%EB%B2%A0%EB%A6%AC%ED%8C%8C%EC%9D%B4-%ED%8C%8C%EC%9D%B4%EC%8D%AC%EA%B3%BC-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EC%8A%A4%ED%8A%9C%EB%94%94%EC%98%A4-%EC%9E%90%EB%B0%94-%EC%86%8C%EC%BC%93%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4
==============================================================
2. using server

## streaming reference
1. http://www.3demp.com/community/boardDetails.php?cbID=234
rt
