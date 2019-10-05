package com.jcp.herehear.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.jcp.herehear.Activity.MainActivity;
import com.jcp.herehear.Class.AudioListening;
import com.jcp.herehear.Class.Permission;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Locale;

/*

    Stt Fragment
    녹음을 시작하면 상대방의 말을 듣고
    번역해서 자막을 띄워준다.
    SpeechRecognition 을 사용.

*/
public class SttFragment extends Fragment implements AudioListening {

    /* 멤버 변수 */
    private ImageView imgv_ui;              // 번역 창 이미
    private TextView txt_dictate;           // 번역 자막 텍스트
    private TextView txt_announce;          // 번역 중 안내 텍스트

    private SpeechRecognizer mRecognizer;   // 음성 인식기
    private Intent speechIntent;            // 음성 인식 인텐트
    private boolean isListening;            // 현재 번역 진행 여부

    /* 생성자 */
    public SttFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stt, container, false);

        /* 초기화 */
        imgv_ui = view.findViewById(R.id.SttFragment_ImageView_dictateImage);
        txt_dictate = view.findViewById(R.id.SttFragment_TextView_dictatedText);
        txt_announce = view.findViewById(R.id.SttFragment_TextView_announce);
        txt_announce.setVisibility(View.INVISIBLE);
        isListening = false;

        /* Glide 이미지 초기화 */
        Glide.with(this).load(R.drawable.text_box).into(imgv_ui);

        /* 음성인식 초기화 */
        initializeSpeechRecognizer();

        /* Onclick Listener */
        imgv_ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isListening){
                    /* 음성인식 재생 아닌 상태 - 시작 */
                    /* Fragment Permission 체크 */
                    MainActivity mainActivity = (MainActivity)getActivity();
                    Permission.CheckAllPermission(mainActivity);
                    boolean permissionCheck = Permission.CheckPermissionProblem(mainActivity);

                    if(permissionCheck)
                        startListening();
                }else{
                    /* 음성인식 재생 중인 상태 - 중단 */
                    stopListening();
                }

            }
        });

        return view;
    }

    /* SpeechRecognizer Settings */
    private void initializeSpeechRecognizer() {

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
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
                stopListening();
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results != null) {
                    txt_dictate.setText(results.get(0));
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }


    /* 음성인식 시작 */
    @Override
    public void startListening() {
        mRecognizer.startListening(speechIntent);
        isListening = true;

//        Glide.with(this)
//                .load(R.drawable.text_box)
//                .transition(GenericTransitionOptions.with(R.anim.flicking))
//                .into(imgv_ui);

        Animation flickAnimate = AnimationUtils.loadAnimation(getContext(), R.anim.flicking);
        txt_announce.setVisibility(View.VISIBLE);
        txt_announce.startAnimation(flickAnimate);
    }

    /* 음성인식 중단 */
    @Override
    public void stopListening() {
        if(!isListening) return;

        mRecognizer.stopListening();
        isListening = false;

        txt_announce.setVisibility(View.INVISIBLE);
        txt_announce.clearAnimation();
    }
}
