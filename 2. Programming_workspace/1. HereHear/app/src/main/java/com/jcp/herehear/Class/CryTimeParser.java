package com.jcp.herehear.Class;


/*

    Firebase DB 에서 Time 값에서 각
    년,월,일,시간,분,초 를 추출하는 파서

    Singleton 패턴으로 getInstance() 를 이용해 호출한다.

*/
public class CryTimeParser {

    private static class LazyHolder {
        public static final CryTimeParser INSTANCE = new CryTimeParser();
    }

    public static CryTimeParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    


}
