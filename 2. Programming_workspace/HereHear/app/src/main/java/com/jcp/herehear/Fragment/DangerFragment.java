package com.jcp.herehear.Fragment;

import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcp.herehear.Class.DangerData;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class DangerFragment extends Fragment {

    // sendToDjango에서 찾는 경로 : /storage/emulated/0/Recorded/audio.wav
    final private static String RECORD_FILE = "/sdcard/tempRecorded.wav";
    MediaRecorder recorder;

    private Timer mTimer;
    private TimerTask mTask;

    /* View */
    private RecyclerView recyclerView;              // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;        // 리사이클러 뷰 어댑터
    private TextView txtTime;                       // 진행 시간 표시 뷰
    private GifImageView imgvPlay;                  // 사운드 리스팅 이미지뷰

    /* Listening */
    private boolean isListening;                    // 현재 듣기 여부
    private long baseTime;                          // 경과 시간 체크를 위한 현재 시간 저장

    /* 생성자 */
    public DangerFragment() {

    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            txtTime.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("msg", "********************* DangerFragment Constructer *********************");
        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        final DangerData danger = new DangerData();

        /* View 연동 */
        recyclerView = view.findViewById(R.id.DangerFragment_RecyclerView_recyclerView);
        txtTime = view.findViewById(R.id.DangerFragment_TextView_time);
        txtTime.setText("00:00:00");
        imgvPlay = view.findViewById(R.id.DangerFragmentAdapter_ImageView_soundPlay);

        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        /* Listening 통신 쪽 처리 */
        imgvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isListening){
                    /* 듣기 시작 */
                    Log.d("Msg","startRecoding 동작! 1번만 수행되야 정상.");
                    isListening = true;
                    imgvPlay.setImageResource(R.drawable.sound_on);

                    /* 진행시간 갱신 */
                    baseTime = SystemClock.elapsedRealtime();
                    myTimer.sendEmptyMessage(0);

                    startRecoding();

                    // 4초마다 반복 동작할 업무 내역.
                    mTask = new TimerTask() {
                        public void run() {
                            Log.d("msg", "4초 경과! 4초마다 수행되야 정상.");
                            // 4초마다 반복할 업무를 여기에 지정
                            endRecoding(); // 파일 저장 전에, 기존 파일 여부 확인 후 삭제

                            // ★★★ ToDoList
                            // 장고연결과 startRecodinig은 Thread하고자 하는데, 현재는 절차식으로 구동되는 듯.
                            // Thread로 구현해야 정상적으로 4초 단위 wav파일이 생성된다.
                            new Thread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    // 장고와 연결하여 temp wav 전달, return값에 따라 UI에 표시
                                    Log.d("Msg", "sendDjango, startRecording Thread 동작");
                                    danger.sendDjango();

                                    // 데시벨 측정 코드. 특정 데시벨을 초과하는 경우에만 sendDjango()를 호출하도록 구현할 수 있다.
                                    // 일반적인 상황에서는 데시벨 100이 적당한 트리거인데 지금은 모바일에서 출력되는 소리로 실험하므노 60, 70쯤이 적당
                                    if(20 * Math.log10((double)Math.abs(recorder.getMaxAmplitude()))>60.0){
                                        Log.d("msg : ", "powerDb가 60를초과했습니다.");
                                    }
                                    else{
                                        Log.d("msg : ", "powerDb가 60를 초과하지 못했습니다.");
                                    }
                                    startRecoding();
                                }
                            }).start();
                        }
                    };
                    mTimer = new Timer(); // 4초단위 Timer 설정.
                    mTimer.schedule(mTask, 4000, 4000);

                    /* 예시 - 이런식으로 wav 이미지 변경한다. */
                    recyclerAdapter.listData.get(0).setListening(true);
                    recyclerAdapter.notifyDataSetChanged();

                }else{
                    /* 듣기 종료 */
                    isListening = false;
                    imgvPlay.setImageResource(R.drawable.sound_off);

                    /* 진행시간 초기화 */
                    myTimer.removeMessages(0); //핸들러 메세지 제거
                    txtTime.setText("00:00:00");

                    /* 예시 - 이런식으로 wav 이미지 변경한다. */
                    recyclerAdapter.listData.get(0).setListening(false);
                    recyclerAdapter.notifyDataSetChanged();

                    Log.d("msg", "온전하게 기능을 종료시키는 버튼 클릭!");
                    if (recorder == null)
                        return;
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    ContentValues values = new ContentValues(10);
                    mTimer.cancel();

                }
            }
            public void startRecoding() {
                Log.d("msg", "#### recordBtn의 startRecoding 동작!");
                if (recorder != null) {
                    try {
                        recorder.stop();
                        recorder.release(); // release위치가 여기가 맞나..?
                    } catch (RuntimeException e) {
                        Log.d("msg", "#### recordBtn의 startRecoding에서 recorder.stop()를 수행하고자 했으나 에러 발생. 즉, 기존의 동작중인 recorder가 없다.");
                    } finally {
                        //recorder.release(); // 원래 release 위치는 여기였다. 에러 해결을 위해 try문으로 이동시켜본 상황.
                        recorder = null;
                    }
                }
                // 새 recorder 동작.
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(RECORD_FILE);

                Log.d("msg", "#### recordBtn의 startRecoding에서 새로운 recorder 설정 완료.");
                try {
                    recorder.prepare();
                    recorder.start();
                    Log.d("msg", "#### recordBtn의 startRecoding에서 새로운 recorder.start() 성공!.");
                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }

            public void endRecoding(){
                Log.d("msg", "@@@@ recordBtn의 endRecoding 동작!");
                if (recorder == null) return;

                ContentValues values = new ContentValues(10);

                //mTimer.cancel(); // mTimer을 여기서 중단시키는 코드가 원래 있었는데, 논리 상 안맞고 타이머가 중단되므로 주석처리한 상황

                // 이부분이 values를 저장하는 부분.
                // ★ ToDoList
                // 기존 파일이 있는지 여부 먼저 파악 후, 있으면 삭제하기 기능을 구현하길 요망한다.
                values.put(MediaStore.MediaColumns.TITLE, "JCP");
                values.put(MediaStore.Audio.Media.ALBUM, "tempRecorded");
                values.put(MediaStore.Audio.Media.ARTIST, "HereHear");
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, "toSendDjango");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
                values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
                values.put(MediaStore.MediaColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
                values.put(MediaStore.Audio.Media.DATA, RECORD_FILE);

                Log.d("msg", "@@@@ recordBtn의 endRecoding에서 values 저장 완료!");
                Log.d("msg-values : ", String.valueOf(values));
            }

        });
        return view;
    }

    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        /* 임시 데이터 */
        public ArrayList<DangerData> listData = new ArrayList<>();

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함 */
        public RecyclerAdapter(){

            /* 경적, 개, 드릴, 총, 사이렌, nothing - 예시로 생성 */
            Drawable icon_horn = getResources().getDrawable(R.drawable.ambulance);
            Drawable icon_barking = getResources().getDrawable(R.drawable.police);
            Drawable icon_drill = getResources().getDrawable(R.drawable.horn);
            Drawable icon_gun = getResources().getDrawable(R.drawable.ambulance);
            Drawable icon_siren = getResources().getDrawable(R.drawable.police);
            Drawable icon_nothing = getResources().getDrawable(R.drawable.horn);

            DangerData horn = new DangerData("경적소리", icon_horn);
            DangerData barking = new DangerData("개짖는소리", icon_barking);
            DangerData drill = new DangerData("드릴소리", icon_drill);
            DangerData gun = new DangerData("총소리", icon_gun);
            DangerData siren = new DangerData("사이렌소리", icon_siren);
            DangerData nothing = new DangerData("아무소리없음", icon_nothing);

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

            holder.txtTypeText.setText(curData.getName());
            holder.imgvTypeIcon.setImageDrawable(curData.getImg());

            if(curData.getListening()){
                /* 듣는 중 */
                holder.imgvWave.setImageResource(R.drawable.voice_on_light);

            }else{
                /* 안 듣는 중 */
                holder.imgvWave.setImageResource(R.drawable.soundwave_off);
            }

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgvTypeIcon;
        private TextView txtTypeText;
        private GifImageView imgvWave;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgvTypeIcon = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_SoundTypeIcon);
            txtTypeText = itemView.findViewById(R.id.DangerFragmentAdapter_TextView_SoundTypeName);
            imgvWave = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_Wave);

        }

    }

    public void onPause() {
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        recorder = new MediaRecorder();
    }
}