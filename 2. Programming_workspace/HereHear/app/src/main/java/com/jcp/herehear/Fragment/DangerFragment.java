package com.jcp.herehear.Fragment;

import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcp.herehear.Class.DangerData;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.ImageView;
import android.widget.TextView;

public class DangerFragment extends Fragment {

    final private static String RECORD_FILE = "/sdcard/tempRecorded.wav";
    MediaRecorder recorder;

    private Timer mTimer;
    private TimerTask mTask;

    /* View */
    private RecyclerView recyclerView;              // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;        // 리사이클러 뷰 어댑터
    private TextView txtTime;                       // 진행 시간 표시 뷰

    /* 생성자 */
    public DangerFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        /* View 연동 */
        recyclerView = view.findViewById(R.id.DangerFragment_RecyclerView_recyclerView);
        txtTime = view.findViewById(R.id.DangerFragment_TextView_time);
        txtTime.setText("00:00:00");

        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        /* 임시 데이터 */
        private ArrayList<DangerData> listData = new ArrayList<>();

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함 */
        public RecyclerAdapter(){

            /* 구급차, 경찰차, 자동차 경적 - 예시로 생성 */
            Drawable icon_amb = getResources().getDrawable(R.drawable.ambulance);
            Drawable icon_pol = getResources().getDrawable(R.drawable.police);
            Drawable icon_horn = getResources().getDrawable(R.drawable.horn);

            DangerData ambulance = new DangerData("구급차", icon_amb);
            DangerData police = new DangerData("경찰차", icon_pol);
            DangerData horn = new DangerData("자동차 경적", icon_horn);

            listData.add(ambulance);
            listData.add(police);
            listData.add(horn);

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
            holder.imgvPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* TODO : 여기서 각 뷰의 플레이 버튼을 눌렀을 때 동작해야 하는 것을 적는다. */
                    if(!listData.get(position).getListening()){
                        /* 현재 리스닝 중이 아님 -> 리스닝 시작 */

                        /* 이미지 아이콘 및 상태 변화 */
                        holder.imgvPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_on));
                        holder.imgvWave.setImageDrawable(getResources().getDrawable(R.drawable.soundwave_on));
                        listData.get(position).setListening(true);

                        /* 리스닝 코드 */







                    }else{
                        /* 현재 리스닝 중임 -> 리스닝 중단 */

                        /* 이미지 아이콘 변화 */
                        holder.imgvPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_off));
                        holder.imgvWave.setImageDrawable(getResources().getDrawable(R.drawable.soundwave_off));
                        listData.get(position).setListening(false);

                        /* 리스닝 중단 코드 */





                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgvTypeIcon;
        private TextView txtTypeText;
        private ImageView imgvPlay;
        private ImageView imgvWave;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgvTypeIcon = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_SoundTypeIcon);
            txtTypeText = itemView.findViewById(R.id.DangerFragmentAdapter_TextView_SoundTypeName);
            imgvPlay = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_Play);
            imgvWave = itemView.findViewById(R.id.DangerFragmentAdapter_ImageView_Wave);

        }

    }

    // 서버에 접속해서 파일 업로드 구현 https://stackoverflow.com/questions/34089436/how-to-upload-a-wav-file-using-urlconnection
    // 저장 파일을 임시 폴더로 옮기도록
    // 매번 저장이 제대로 되는지 확인
    // 파일 conflict나지 않도록 조정
    // 버튼 클릭이 아닌 4초마다 스플릿해서 이거 호출하고, 버튼 클릭되면 동작 종료되도록 구현

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("1", "DangerFragment Constructer wawawawawa");

        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        Button recordBtn = (Button) view.findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) view.findViewById(R.id.recordStopBtn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void startRecoding(){
                Log.d("2", "DangerFragment Constructer Button was Clicked!!!");
                if (recorder != null) {
                    try{
                        recorder.stop();
                    } catch (RuntimeException e){
                        Log.d("2-2", "DangerFragment Constructer Button recorder is empty!!!!!");
                    } finally{
                        recorder.release();
                        recorder = null;
                    }
                    //recorder.release();
                    //recorder = null;
                }
                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(RECORD_FILE);

                try {
                    recorder.prepare();
                    recorder.start();
                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }

            public void endRecoding(){
                Log.d("3", "DangerFragment Constructer Stop Button was Clicked!!!");
                if (recorder == null)
                    return;
                recorder.stop();
                recorder.release();
                recorder = null;

                ContentValues values = new ContentValues(10);

                mTimer.cancel();

                // 이부분이 values를 저장하는 부분인듯.
                // 기존 파일이 있는지 여부 먼저 파악 후, 있으면 삭제하기
                values.put(MediaStore.MediaColumns.TITLE, "Recorded");
                values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
                values.put(MediaStore.Audio.Media.ARTIST, "MIKE");
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio");
                values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
                values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
                values.put(MediaStore.MediaColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
                values.put(MediaStore.Audio.Media.DATA, RECORD_FILE);


                Log.d("4", String.valueOf(values));

                Uri audioUri = getContext().getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        values
                );

                Log.d("5", String.valueOf(audioUri));
                if(audioUri == null){
                    Log.d("SampleAudioRecorder", "Audio insert failed.");
                    return;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void sendDjango(){
                // 여기서부터 실험.
                String requestURL = "https://api.wit.ai/speech?v=20160526"; // Django 파일 업로드 링크 지정
                URL url = null;
                try {
                    url = new URL(requestURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection httpConn = null;
                try {
                    httpConn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpConn.setUseCaches(false);
                httpConn.setDoOutput(true); // indicates POST method
                httpConn.setDoInput(true);

                try {
                    httpConn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }

                httpConn.setRequestProperty("Connection", "Keep-Alive");
                httpConn.setRequestProperty("Cache-Control", "no-cache");
                httpConn.setRequestProperty("Authorization", "Bearer XXXXXXXXXXXXXXXXXXXXXX");;
                httpConn.setRequestProperty("Content-Type", "audio/wav");;
                File waveFile= new File("RecordAudio.wav"); // 여기에 파일 경로 지정하기
                byte[] bytes = new byte[0];
                try {
                    bytes = Files.readAllBytes(waveFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataOutputStream request = null;
                try {
                    request = new DataOutputStream(httpConn.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String response = "";

                // checks server's status code first
                int status = 0;
                try {
                    status = httpConn.getResponseCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (status == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = null;
                    try {
                        responseStream = new BufferedInputStream(httpConn.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    BufferedReader responseStreamReader
                            = new BufferedReader(new InputStreamReader(responseStream));

                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while (true) {
                        try {
                            if (!((line = responseStreamReader.readLine()) != null)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stringBuilder.append(line).append("\n");
                    }
                    try {
                        responseStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    response = stringBuilder.toString(); // response를 signal로 Django로부터 return 받기
                    httpConn.disconnect();
                } else {
                    try {
                        throw new IOException("Server returned non-OK status: " + status);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 여기까지 실험
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                startRecoding();

                mTask = new TimerTask(){
                    public void run(){
                        // 4초마다 반복할 업무를 여기에 지정
                        endRecoding(); // 파일 저장 전에, 기존 파일 여부 확인 후 삭제
                        // 장고연결과 startRecodinig은 Thread로 하도록 공부해보자.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 장고와 연결하여 temp wav 전달, return값에 따라 UI에 표시
                                sendDjango();
                                startRecoding();
                            }
                        }).start();
                    }
                };
                mTimer = new Timer();
                mTimer.schedule(mTask, 4, 4);
            }
        });

        // 녹음을 종료시키는 코드
        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void endRecoding(){
                Log.d("3", "DangerFragment Constructer Stop Button was Clicked!!!");
                if (recorder == null)
                    return;
                recorder.stop();
                recorder.release();
                recorder = null;
                ContentValues values = new ContentValues(10);
                mTimer.cancel();
            }

            @Override
            public void onClick(View v) {
                endRecoding();
            }
        });

        return view;
    }

    */

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