
package com.jcp.herehear.Class;

/*

    CryData 는 임시 클래스로
    Http 통신 Request 를 통해 전달 받은 데이터 값 또는
    애기가 울었을때 시간과 소리크기 등, 리사이클러뷰에 들어갈 데이터 정보들을 담는다.

    추후 용도에 맞게 수정해서 써야 함.
    whenCry:언제 울었는가. 시간. string으로 저장하게 함.
    howCry:얼마나 크게 울었는가. 데시벨로 나타날 예정. string저장

*/


public class CryData {

    private String whenCry;//whenCry:언제 울었는가. 시간. string으로 저장하게 함.
    private String howCry;//howCry:얼마나 크게 울었는가. 데시벨로 나타날 예정. string저장
    private boolean isListening;//들리니!

    /* constructor */
    public CryData() {
    }

    public CryData(String whenCry, String howCry) {
        this.whenCry = whenCry;
        this.howCry = howCry;
        this.isListening = false;
    }


    /* getter, setter */
    public String getWhenCry() {
        return whenCry;
    }
    public void setWhenCry(String whenCry) { this.whenCry = whenCry; }
    public String getHowCry() {
        return howCry;
    }
    public void setHowCry(String howCry) {
        this.howCry = howCry;
    }
    public Boolean getListening() {
        return isListening;
    }
    public void setListening(Boolean listening) {
        isListening = listening;
    }

    public String sendDjango() {//FIXME!여기 dangerpart 주석보니까 안된거같은데 된건가요?
        String result = "";
        return result; //"Failed to fetch data!";
    }

}
