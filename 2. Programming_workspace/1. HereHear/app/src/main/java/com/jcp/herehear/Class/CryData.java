
package com.jcp.herehear.Class;

/*

    CryData 는 임시 클래스로
    Http 통신 Request 를 통해 전달 받은 데이터 값 또는
    애기가 울었을때 시간과 소리크기 등, 리사이클러뷰에 들어갈 데이터 정보들을 담는다.

     --> Http 통신 하지 않고 Firebase DB 수신을 위한 DTO 클래스로 사용한다.

    추후 용도에 맞게 수정해서 써야 함.

*/


import java.text.SimpleDateFormat;
import java.util.Date;

public class CryData {

    /* member variables */
    public final static int STATE_CRY = 1;                   // 아이 울음 상태
    public final static int STATE_NOCRY = 0;                 // 아이 안우는 상태
    private int State;                                       // 아이 상태 저장
    private String Time;                                     // 아이 상태 변화 시간
    private Date formatedTime;                               // 시간 포매터

    /* constructor */
    public CryData() {
        this.State = STATE_NOCRY;
        this.Time = "";
    }

    /* Time 값을 포맷화 해서 저장한다. */
    public void setTimeFormatted(){
        formatedTime = new Date(Time);
    }

    /* getter, setter */
    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Date getFormattedTime(){
        return formatedTime;
    }

}
