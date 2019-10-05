package com.jcp.herehear.Class;

/*

    DangerData 는 임시 클래스로
    Http 통신 Request 를 통해 전달 받은 데이터 값 또는
    소리 종류와 관련된 리스트뷰에 들어갈 데이터 정보들을 담는다.

    추후 용도에 맞게 수정해서 써야 함.

*/

import android.graphics.drawable.Drawable;

public class DangerData {

    private String name;
    private Drawable img_on;
    private Drawable img_off;
    private boolean isListening;

    /* constructor */
    public DangerData() {

    }

    public DangerData(String name, Drawable img_off, Drawable img_on) {
        this.img_on = img_on;
        this.img_off = img_off;
        this.isListening = false;
    }


    /* getter, setter */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImg_on() {
        return img_on;
    }

    public void setImg_on(Drawable img_on) {
        this.img_on = img_on;
    }

    public Boolean getListening() {
        return isListening;
    }

    public void setListening(Boolean listening) {
        isListening = listening;
    }

    public Drawable getImg_off() { return img_off; }

    public void setImg_off(Drawable img_off) { this.img_off = img_off; }

}





