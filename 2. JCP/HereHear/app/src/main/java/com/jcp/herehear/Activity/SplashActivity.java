package com.jcp.herehear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jcp.herehear.R;

/*

    Description : 시작 로딩 화면을 보여주는 클래스
                  본 액티비티가 생성되자 마자
                  메인 액티비티를 시작하고 종료한다.
                  Layout과 연결은 따로 하지 않으며 Manifest에 정의된
                  SplashActivity 의 스타일 테마(SplashTheme)에 의해
                  로딩화면이 보인다.
                  시작하자 마자 흰 화면이 아닌 @drawable/splash_background 에
                  그려진 화면이 보임.

*/

public class SplashActivity extends AppCompatActivity {

    /* 메인엑티비티 전환 인텐트 */
    private Intent intent;


    /*

        Description : MainActivity로 가는 전환 인텐트 생성

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, MainActivity.class);

    }

    /*

        Description : 본 로딩 액티비티 시작 이후
                      res/values/delays.xml 에 정의된
                      delay_loading 시간 만큼 로고를 보여준다.
                      이후 메인 액티비티로 전환하고 본 액티비티는 종료.
                      만약 앱 개발이 진척되어 앱의 무게가 증가하고 앱을 로드하는데 걸리는
                      시간이 매우 커지면 delay_loading 시간을 0으로 잡아도
                      충분히 로딩화면이 지속된다.

    */
    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(intent);
                finish();

            }

        }, getResources().getInteger(R.integer.delay_loading));

    }
}