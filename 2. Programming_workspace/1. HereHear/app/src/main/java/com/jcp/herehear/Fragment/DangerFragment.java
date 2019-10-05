package com.jcp.herehear.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jcp.herehear.Activity.MainActivity;
import com.jcp.herehear.Class.AudioListening;
import com.jcp.herehear.Class.DangerData;
import com.jcp.herehear.Class.HttpSoundRequest;
import com.jcp.herehear.Class.Permission;
import com.jcp.herehear.Class.RecordTask;
import com.jcp.herehear.Class.TimeHandler;
import com.jcp.herehear.Class.WavRecorder;
import com.jcp.herehear.Dialog.FragmentDialog;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Timer;

public class DangerFragment extends Fragment implements TimeHandler.TimeHandleResponse, HttpSoundRequest.AsyncResponse, AudioListening {

    private final String RECORD_FILE_NAME =
            "recorded.wav";                         // wav 파일 이름
    public final static String RECORD_FILE_DIR =
            "/sdcard/AudioRecorder/recorded.wav";   // wav 파일 저장 경로
    private final int RECORD_CYCLE = 4000;          // wav 파일 레코딩 주기
    private final int Half_RECORD_CYCLE = 2000;     // wav 파일 레코딩 주기
    private final int VIBRATE_CYCLE = 1000;         // 진동 주기

    private Timer mTimer;
    private RecordTask recordTask;                  // 주기별로 녹음하고 요청처리하는 Task
    private WavRecorder wavRecorder;                // .wav 포맷 레코더

    /* View */
    private RecyclerView recyclerView;              // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;        // 리사이클러 뷰 어댑터
    private TextView txtTime;                       // 진행 시간 표시 뷰
    private ImageView imgvPlay;                     // 사운드 리스팅 이미지뷰

    /* Listening */
    private boolean isListening;                    // 현재 듣기 여부
    private long baseTime;                          // 경과 시간 체크를 위한 현재 시간 저장
    private HttpSoundRequest.AsyncResponse delegate;// Callback 처리를 위한 delegate
    private FragmentDialog fragmentDialog;          // Callback 에서 다이얼로그를 띄워줌.
    private UI_DangerHandler uiDangerHandler;       // Callback 에서 메인스레드 UI 충돌방지를 위한 핸들러 사용

    /* 생성자 */
    public DangerFragment() {
        uiDangerHandler = new UI_DangerHandler();
    }

    /* Time Handler - 타이머 클래스 */
    private final TimeHandler timeHandler = new TimeHandler(this);

    /* 타임 핸들러에 대한 콜백 UI 처리 */
    @Override
    public void processTimerUI() {
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        String elapsedTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
        txtTime.setText(elapsedTime);
        timeHandler.sendEmptyMessage(0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("msg", "********************* DangerFragment Constructer *********************");
        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        /* View 연동 */
        recyclerView = view.findViewById(R.id.DangerFragment_RecyclerView_recyclerView);
        txtTime = view.findViewById(R.id.DangerFragment_TextView_time);
        txtTime.setText("00:00:00");
        imgvPlay = view.findViewById(R.id.DangerFragmentAdapter_ImageView_soundPlay);
        Glide.with(this).load(R.drawable.speaker_off).into(imgvPlay);

        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        /* Wav 레코더 생성 */
        wavRecorder = new WavRecorder(RECORD_FILE_NAME);
        delegate = this;

        /* Listening 통신 쪽 처리 */
        imgvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListening) {
                    /* Fragment Permission 체크 */
                    MainActivity mainActivity = (MainActivity)getActivity();
                    Permission.CheckAllPermission(mainActivity);
                    boolean permissionCheck = Permission.CheckPermissionProblem(mainActivity);
                    if(permissionCheck){
                        startListening();
                    }
                } else {
                    stopListening();
                }
            }

        });
        return view;
    }

    /* 오디오 리스닝작업 시작 */
    @Override
    public void startListening() {
        /* 듣기 시작 */
        Log.d("Msg", "startRecoding 동작! 1번만 수행되야 정상.");
        isListening = true;
//        imgvPlay.setImageResource(R.drawable.speaker_on);
        Glide.with(this).load(R.drawable.speaker_on).into(imgvPlay);
        Animation palpitateAnimate = AnimationUtils.loadAnimation(getContext(), R.anim.palpitate);
        imgvPlay.startAnimation(palpitateAnimate);

        /* 진행시간 갱신 */
        baseTime = SystemClock.elapsedRealtime();
        timeHandler.sendEmptyMessage(0);

        /* 레코딩 시작 */
        recordTask = new RecordTask(wavRecorder, delegate);
        wavRecorder.startRecording();
        mTimer = new Timer();
        mTimer.schedule(recordTask, RECORD_CYCLE, RECORD_CYCLE);

        /* 레코딩 관련 본코드 */
        recyclerAdapter.listData.get(recyclerAdapter.preListeningIdx).setListening(true);
        recyclerAdapter.notifyDataSetChanged();
    }

    /* 오디오 리스닝작업 종료 */
    @Override
    public void stopListening() {
        if(!isListening) return;

        /* 듣기 종료 */
        isListening = false;
//        imgvPlay.setImageResource(R.drawable.speaker_off);
        Glide.with(this).load(R.drawable.speaker_off).into(imgvPlay);
        imgvPlay.clearAnimation();

        /* 진행시간 초기화 */
        timeHandler.removeMessages(0); //핸들러 메세지 제거
        txtTime.setText("00:00:00");

        /* 레코딩 종료 */
        wavRecorder.stopRecording();
        mTimer.cancel();

        recyclerAdapter.listData.get(recyclerAdapter.preListeningIdx).setListening(false);
        recyclerAdapter.preListeningIdx = 5;
        recyclerAdapter.notifyDataSetChanged();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        /* 임시 데이터 */
        public ArrayList<DangerData> listData = new ArrayList<>();
        public int preListeningIdx = 5;                                        // 가장 최근의 소리

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함 */
        public RecyclerAdapter() {

            /* 경적, 개, 드릴, 총, 사이렌, nothing - 예시로 생성 */
            Drawable icon_horn = getResources().getDrawable(R.drawable.danger_icon_horn_no);
            Drawable icon_barking = getResources().getDrawable(R.drawable.danger_icon_dog_no);
            Drawable icon_drill = getResources().getDrawable(R.drawable.danger_icon_drilling_no);
            Drawable icon_gun = getResources().getDrawable(R.drawable.danger_icon_gun_no);
            Drawable icon_siren = getResources().getDrawable(R.drawable.danger_icon_siren_no);
            Drawable icon_nothing = getResources().getDrawable(R.drawable.danger_icon_background_no);

            Drawable icon_horn_yes = getResources().getDrawable(R.drawable.danger_icon_horn_yes);
            Drawable icon_barking_yes = getResources().getDrawable(R.drawable.danger_icon_dog_yes);
            Drawable icon_drill_yes = getResources().getDrawable(R.drawable.danger_icon_drilling_yes);
            Drawable icon_gun_yes = getResources().getDrawable(R.drawable.danger_icon_gun_yes);
            Drawable icon_siren_yes = getResources().getDrawable(R.drawable.danger_icon_siren_yes);
            Drawable icon_nothing_yes = getResources().getDrawable(R.drawable.danger_icon_background_yes);

            DangerData horn = new DangerData("경적소리", icon_horn, icon_horn_yes);
            DangerData barking = new DangerData("개짖는소리", icon_barking, icon_barking_yes);
            DangerData drill = new DangerData("드릴소리", icon_drill, icon_drill_yes);
            DangerData gun = new DangerData("총소리", icon_gun, icon_gun_yes);
            DangerData siren = new DangerData("사이렌소리", icon_siren, icon_siren_yes);
            DangerData nothing = new DangerData("아무소리없음", icon_nothing, icon_nothing_yes);

            listData.add(horn);
            listData.add(barking);
            listData.add(drill);
            listData.add(gun);
            listData.add(siren);
            listData.add(nothing);

        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_danger_adapter, parent, false);
            return new ItemViewHolder(view);

        }

        /*

            Adapter 리스트 내용이 notify 되었을 경우 onBindViewHolder 가 호출된다.
            다음 함수에서 실제 받아온 listData 의 데이터 정보(DangerData)를 통해
            리스트뷰의 특정 목록의 데이터를 커스터마이징 한다.

        */
        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            final DangerData curData = listData.get(position);

            if (curData.getListening()) {
                /* 듣는 중 */
//                holder.imgvWave.setImageResource(R.drawable.voice_on_light);
                Glide.with(getContext()).load(R.drawable.voice_on_light).into(holder.imgvWave);
                Glide.with(getContext()).load(curData.getImg_on()).into(holder.imgvTypeIcon);

            } else {
                /* 안 듣는 중 */
//                holder.imgvWave.setImageResource(R.drawable.soundwave_off);
                Glide.with(getContext()).load(R.drawable.soundwave_off).into(holder.imgvWave);
                Glide.with(getContext()).load(curData.getImg_off()).into(holder.imgvTypeIcon);
            }

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgvTypeIcon;
        private ImageView imgvWave;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgvTypeIcon = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_SoundTypeIcon);
            imgvWave = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_Wave);

        }

    }


    /*

        HttpSoundRequest 를 통해 결과 값을 처리하는
        Callback 함수

        Classified Index 에 해당하는 UI 를 처리한다.

    */
    @Override
    public void onSoundResponseResult(int index) {
        Log.d("Classified .wav ", String.valueOf(index));

        /* 종료가 되고 http 로 수신했을 때는 처리하지 않는다. */
        if(!isListening) return;

        /* 그래프 움짤을 바꾸는 코드 */
        Message msg = new Message();
        msg.what = uiDangerHandler.HANDLE_RECYCLER_NOTIFY;
        msg.obj = index;
        uiDangerHandler.sendMessage(msg);

        /* 팝업 출력 코드 */
        // index가 5로 판별되어 배경음인 경우, 팝업 출력 자체를 안한다.
        if(index < 5 && index >= 0){
            MainActivity mainActivity = (MainActivity)getActivity(); // 메인 엑티비티 가져오기
            if(fragmentDialog != null){
                fragmentDialog.dismiss();
                fragmentDialog = null;
            }
            fragmentDialog = new FragmentDialog(); // 출력하고자 하는 팝업 dialog 생성자 호출
            fragmentDialog.setIndex(index); // 판별한 결과를 팝업에 전달
            fragmentDialog.show(getActivity().getSupportFragmentManager(), "tag"); // 팝업 출력

            // 4초 후 팝업 자동 종료
            /*

                이전의 Fragment 가 on 일 경우 끄고 다시 시작하는 방식으로 만들었습니다.
                delayTime 내부의 함수에서 handler 를 불러올 수가 없어서 delayTime의 dismiss 함수가 작동하지 않습니다.
                그냥 이전의 dialog가 떠있는 상태일 경우 끄고 다시 띄우는 방식으로 구현하였습니다.

            */
//            FragmentDialog.delayTime(RECORD_CYCLE, dialog);
            //FragmentDialog.delayTime(Half_RECORD_CYCLE, dialog);

            // 진동 구현
            /* TODO : 진동 구현 방식 찾아야 함! */
            Vibrator vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_CYCLE);
        }
    }

    /*

        Callback 을 받아 UI 를 처리하더라도 여러 쓰레드에서 겹쳐서 callback
        처리된다면 메인 UI 쓰레드에서 돌아가지 않는듯 하다.
        이때 recyclerview.notifyDataSetChanged() 가 메인쓰레드에서 콜이 안되어
        예외가 발생, UI 처리가 꼬이게 됨.

        따라서 Callback 으로 받고 callback 에서는 UI 핸들러 메세지를 보내는 방식으로 구현.

    */
    private class UI_DangerHandler extends Handler{

        public final int HANDLE_RECYCLER_NOTIFY = 0;

        public void handleMessage(Message msg){
            switch(msg.what){
                case HANDLE_RECYCLER_NOTIFY:
                    int index = (int)msg.obj;
                    recyclerAdapter.listData.get(recyclerAdapter.preListeningIdx).setListening(false);
                    recyclerAdapter.listData.get(index).setListening(true);
                    recyclerAdapter.preListeningIdx = index;
                    recyclerAdapter.notifyDataSetChanged();
                    Log.d("Handler operate", "recyclerAdapter Notified");
            }
        }

    }

}