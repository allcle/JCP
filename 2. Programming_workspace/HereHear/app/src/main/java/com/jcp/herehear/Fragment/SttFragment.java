package com.jcp.herehear.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Locale;

/*

    Stt Fragment
    녹음을 시작하면 상대방의 말을 듣고
    번역해서 자막을 띄워준다.
    SpeechRecognition 을 사용.

*/

public class SttFragment extends Fragment {

    /* 멤버 변수 */
    private ImageView imgv_mic;             // 마이크 아이콘
    private TextView txt_announce;          // 마이크 안내 텍스트
    private TextView txt_dictate;           // 번역 자막 텍스트

    private SpeechRecognizer mRecognizer;   // 음성 인식기
    private Intent speechIntent;            // 음성 인식 인텐트
    private boolean isDictating;            // 현재 번역 진행 여부

    /* 생성자 */
    public SttFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stt, container, false);

        /* 권한 체크 */
        setSpeechPermission();

        /* 초기화 */
        imgv_mic = view.findViewById(R.id.SttFragment_ImageView_Mic);
        txt_announce = view.findViewById(R.id.SttFragment_TextView_announce);
        txt_dictate = view.findViewById(R.id.SttFragment_TextView_dictate);
        isDictating = false;

        /* 음성인식 초기화 */
        initializeSpeechRecognizer();

        /* Onclick Listener */
        imgv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDictating){
                    /* 음성인식 재생 아닌 상태 - 시작 */
                    speak();
                }else{
                    /* 음성인식 재생 중인 상태 - 중단 */
                    stopSpeak();
                }

            }
        });

        return view;
    }

    /* 권한 체크 */
    private void setSpeechPermission() {

        /* 권한 체크부터 하고 권한이 허용되어 있지 않으면 설정으로 넘어간다. */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(!(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)){

                Toast.makeText(getContext(), "마이크권한을 허용하고 다시 시작하세요", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+getContext().getPackageName()));
                startActivity(intent);
                getActivity().finish();

            }

        }

    }

    /* SpeechRecognizer Settings */
    private void initializeSpeechRecognizer() {

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results != null) {
                    txt_dictate.setText(results.get(0));
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    /* 음성인식 중단 */
    private void stopSpeak(){

        mRecognizer.stopListening();
        isDictating = false;

        txt_announce.setText(R.string.SttFragment_touchMic);
        txt_announce.setTextColor(Color.BLACK);
        imgv_mic.setImageResource(R.drawable.icon_mic_off);

    }

    /* 음성인식 시작 */
    private void speak(){

        mRecognizer.startListening(speechIntent);
        isDictating = true;

        txt_announce.setText(R.string.SttFragment_speakMic);
        txt_announce.setTextColor(Color.MAGENTA);
        imgv_mic.setImageResource(R.drawable.icon_mic_on);

    }

}
