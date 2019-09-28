
package com.jcp.herehear.Fragment;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
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

import com.jcp.herehear.Class.CryData;
import com.jcp.herehear.Class.TimeHandler;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CryFragment extends Fragment implements TimeHandler.TimeHandleResponse {
//public class CryFragment extends Fragment implements TimeHandler.TimeHandleResponse {

    // sendToDjango에서 찾는 경로 : /storage/emulated/0/Recorded/audio.wav
    final private static String RECORD_FILE = "/sdcard/tempRecorded.wav";
    /* Time Handler - 타이머 클래스 */
    private final TimeHandler myTimer = new TimeHandler(this);
    MediaRecorder recorder;
    private Timer mTimer;
    private TimerTask mTask;
    /* View */
    private RecyclerView recyclerView;                 // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;           // 리사이클러 뷰 어댑터
    private TextView txtTime;                       // 진행 시간 표시 뷰
    private ImageView imgvPlay;                  // 사운드 리스팅 이미지뷰
    /* Listening */
    private boolean isListening;                    // 현재 듣기 여부
    private long baseTime;                          // 경과 시간 체크를 위한 현재 시간 저장

    /* 생성자 */
    public CryFragment() {

    }

    /* 타임 핸들러에 대한 콜백 UI 처리 */

    @Override
    public void processUI() {
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        String elapsedTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
        txtTime.setText(elapsedTime);
        myTimer.sendEmptyMessage(0);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("msg", "********************* CeyFragment Constructer *********************");

        View view = inflater.inflate(R.layout.fragment_cry, container, false);
        final CryData danger = new CryData();

        /* View 연동 */
        recyclerView = view.findViewById(R.id.CryFragment_RecyclerView_recyclerView);
        txtTime = view.findViewById(R.id.CryFragment_TextView_time);
        txtTime.setText("00:00:00");//진행시간 text지정
        imgvPlay = view.findViewById(R.id.CryFragmentAdapter_ImageView_soundPlay);

        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        /* Listening 통신 쪽 처리 */
        imgvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListening) {
                    /* 듣기 시작 */
                    Log.d("Msg", "startRecoding 동작! 1번만 수행되야 정상.");
                    isListening = true;
                    imgvPlay.setImageResource(R.drawable.cry_yes);

                    /* 진행시간 갱신 */
                    baseTime = SystemClock.elapsedRealtime();
                    myTimer.sendEmptyMessage(0);

                    startRecoding();

                    // 4초마다 반복 동작할 업무 내역.
                    mTask = new TimerTask() {
                        public void run() {
                            Log.d("msg", "4초 경과! 4초마다 수행되야 정상.");

                            // ToDoList 데시벨 측정 정상적인지 확인
                            // 데시벨 측정 코드. 특정 데시벨을 초과하는 경우에만 sendDjango()를 호출하도록 구현할 수 있다.
                            // 일반적인 상황에서는 데시벨 100이 적당한 트리거인데 지금은 모바일에서 출력되는 소리로 실험하므노 60, 70쯤이 적당
                            if (20 * Math.log10((double) Math.abs(recorder.getMaxAmplitude())) > 60.0) {
                                Log.d("msg : ", "powerDb가 60를초과했습니다.");
                            } else {
                                Log.d("msg : ", "powerDb가 60를 초과하지 못했습니다.");
                            }
                            endRecoding();
                            // Thread로 sendDjango & startRecoding 동작
                            Log.d("Msg", "sendDjango, startRecording Thread 동작");
                            new Thread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    // 장고와 연결하여 temp wav 전달, return값에 따라 UI에 표시
                                    danger.sendDjango();//장고랑 연결되는지 모르지만 일단 안지웠습니다. FIXME!
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

                } else {
                    /* 듣기 종료 */
                    isListening = false;
                    imgvPlay.setImageResource(R.drawable.cry_no);

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

                    // ToDoList 여기서 저장된 tempRecording.wav 삭제 구현할까?
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

            public void endRecoding() {
                Log.d("msg", "@@@@ recordBtn의 endRecoding 동작!");
                if (recorder == null) return;

                ContentValues values = new ContentValues(10);

                //mTimer.cancel(); // mTimer을 여기서 중단시키는 코드가 원래 있었는데, 논리 상 안맞고 타이머가 중단되므로 주석처리한 상황

                // 이부분이 values를 저장하는 부분.
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

    public void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        recorder = new MediaRecorder();
    }

    /*데이터: [시간, 소리크기] 리스트 Array*/
    private class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        /* 임시 데이터 */
        private ArrayList<CryData> listData = new ArrayList<>();

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함
         * 현재 시안에는 cry에 시간, 소리크기가 나와있으므로 그것으로 잡음*/
        public RecyclerAdapter() {

            CryData tmp1 = new CryData("오전8:02", "80dB");
            CryData tmp2 = new CryData("오전11:25", "140dB");

            listData.add(tmp1);
            listData.add(tmp2);
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cry_adapter, parent, false);
            return new CryFragment.ItemViewHolder(view);

        }

        /*
            Adapter 리스트 내용이 notify 되었을 경우 onBindViewHolder 가 호출된다.
            다음 함수에서 실제 받아온 listData 의 데이터 정보(DangerData)를 통해
            리스트뷰의 특정 목록의 데이터를 커스터마이징 한다.
        */

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            final CryData curData = listData.get(position);

            holder.whenCryText.setText(curData.getWhenCry());
            holder.howCryText.setText(curData.getHowCry());


        }


        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView whenCryText;
        private TextView howCryText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            whenCryText = itemView.findViewById(R.id.CryFragmentAdapter_TextView_whenCry);
            howCryText = itemView.findViewById(R.id.CryFragmentAdapter_TextView_howCry);
        }
    }
}