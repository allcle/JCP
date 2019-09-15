### 안드로이드 어플리케이션 프로젝트

___

README.md 는 기호에 맞게 수정하셔도 됩니다.



#### History 

---

##### 19.07.21.근화 FirstCommit - 변경사항 및 이슈

* 로딩화면 -> 메인액티비티 및 뷰페이저 연결
* 각 Fragment 생성(번역, 아이울음소리, 위기감지)
* 앱의 이름이.. HereHear 맞나요..? 수정요망
* Drawable 폴더 미작업 - 멀티해상도구현방식 스터디 필요
* build.gradle 파일 확인 - compileSdkVersion 29 설정(근화)환경에서는 29 이하 버전에서 링킹오류 발생.
* res/values 의 하위 xml 파일 주석 참고.
* 기타 Activity, Fragment 주석 참고.
* 디자인 가이드 필요 - 사이즈, 이미지파일, 회의 때 사용한 스토리보드
* Github 협업 유의사항 설명가이드 필요.



##### 19.08.19.근화 190819SttFragment작업 - 변경사항 및 이슈

* STT Fragment 작업 및 테스트 성공



##### 19.09.10.근화 190910DangerFragment작업 - 변경사항 및 이슈

- 상단바 HereHear 이미지 부착

- DangerFragment RecyclerView 를 통한 기초 작업 완료.

- CryFragment - 재은님이 해당 코드 참고해서 작업.

- 각 Play 버튼에 setOnclickListener 달아놓음 -> http 통신작업 필요.

- 작업하면서 따로 커스터마이징 해야할 소스들

  - src/main/java/com.jcp.herehear/Class/DangerData : 각 Danger 리스트의 정보를 담는 클래스
  - DangerFragment 내부 클래스인 RecyclerAdapter 클래스의 listData 및 초기화 부분(생성자)
  - 기타 onCreateView 쪽 인수님이 작업한거는 일단 주석 처리함.




##### 19.09.15.근화 190915 18:45 DangerFragment 및 CryFragment Merge - 변경사항 및 이슈

- 재은님 코드 Merge (CryFragment 작업)
- DangerFragment 아이템리스트 6개 확장(경적, 개, 드릴, 총, 사이렌, 무)
- 각 아이템 별 재생 버튼 기능 삭제
- DangerFragment 통합 리스닝버튼 작업 및 이미지뷰 -> GifImageView 전환
- 통합 리스닝 버튼 on/off 시 png 변화 및 진행시간 기능 추가



---

###### TODO List

* 현재 모든 대화가 완료된 시점에 결과 자막을 보여주는데 중간에 계속해서 텍스트가 수정되도록 onPartialResults 메서드를 사용해서 수정 구현할것.
* SttFragment UI 구조 수정 -> 가이드파일과 비슷하게.
* Cry Fragment RecyclerView 구현 및 AsyncTask 클래스를 통한 라즈베리파이 통신구현.