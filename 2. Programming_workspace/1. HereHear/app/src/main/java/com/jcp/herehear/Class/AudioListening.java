package com.jcp.herehear.Class;

/*

    각 Fragment 의 오디오 통제를 위한 인터페이스
    각 프레그먼트는 다음 인터페이스를 implement 하여 사용합니다.
    구현된 함수 위에 오디오 리스닝의 시작과 종료를 설정하고
    MainActivity 에서도 다음 함수를 통해 화면간 전환을 통제하게끔 합니다.

*/
public interface AudioListening{

    boolean isListening = false;

    void startListening();
    void stopListening();

}