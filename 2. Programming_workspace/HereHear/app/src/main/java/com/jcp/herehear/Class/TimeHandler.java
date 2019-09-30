package com.jcp.herehear.Class;

import android.os.Handler;
import android.os.Message;


/*

    Description : 진행시간 표현하는 Handler 클래스

    Handler, AsyncTask 는 안드로이드 상에서 멀티쓰레드 프로그래밍을 통해
    통신 처리, I/O로딩 처리, 타이머 등 실시간으로 동시에 처리되는 일들을
    비동기적으로 UI 연동 처리까지 할 수 있는 방법입니다.
    관련된 개념을 알아두신다면 꼭 안드로이드가 아니라도 프로그래밍적으로 많은 도움이 되실거에요
    다음은 안드로이드의 Handler 클래스와 스레드에 대한 괜찮은 설명글 입니다 : https://recipes4dev.tistory.com/166

    아래에 만들어놓은 클래스는
    Handler + Callback 함수로 구현한 타이머 클래스입니다.
    비동기처리에 대한 개념과 Callback 함수를 이해한다면 안드로이드 뿐만 아니라
    서버쪽이든 클라이언트쪽이든 구현 지식이 많이 늘겁니다.

    JAVA 에서는 interface 를 활용해서 콜백함수를 구현할 수 있습니다.
    또한 안드로이드에 있는 많은 기능들이 콜백을 통해 구현되어있습니다 (onActivityResult 등)

*/

public class TimeHandler extends Handler {

    /* UI callback 을 위한 인터페이스 */
    public interface TimeHandleResponse{
        void processTimerUI();
    }

    /* 타임 핸들러를 요청한 해당 프레그먼트의 주소 값을 저장 */
    public TimeHandleResponse delegate = null;

    /* constructor */
    public TimeHandler(TimeHandleResponse delegate){
        this.delegate = delegate;
    }

    @Override
    public void handleMessage(Message msg){

        /* Call back */
        delegate.processTimerUI();

    }



}
