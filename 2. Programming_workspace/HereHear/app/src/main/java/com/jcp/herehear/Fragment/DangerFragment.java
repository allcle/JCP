package com.jcp.herehear.Fragment;

import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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

import pl.droidsonroids.gif.GifAnimationMetaData;
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

        View view = inflater.inflate(R.layout.fragment_danger, container, false);

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
                    isListening = true;
                    imgvPlay.setImageResource(R.drawable.sound_on);

                    /* 진행시간 갱신 */
                    baseTime = SystemClock.elapsedRealtime();
                    myTimer.sendEmptyMessage(0);

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


                }


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

    // 서버에 접속해서 파일 업로드 구현 https://stackoverflow.com/questions/34089436/how-to-upload-a-wav-file-using-urlconnection
    // 저장 파일을 임시 폴더로 옮기도록
    // 매번 저장이 제대로 되는지 확인
    // 파일 conflict나지 않도록 조정

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("1", "********************* DangerFragment Constructer *********************");

        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        Button recordBtn = (Button) view.findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) view.findViewById(R.id.recordStopBtn);
        final String ASR_URL="http://localhost:8000/uploads/simple/";

        // record 버튼을 클릭했을 때 호출되는 onClick method.
        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void startRecoding(){
                Log.d("msg", "#### recordBtn의 startRecoding 동작!");
                if (recorder != null) {
                    try{
                        recorder.stop();
                        recorder.release(); // release위치가 여기가 맞나..?
                    } catch (RuntimeException e){
                        Log.d("msg", "#### recordBtn의 startRecoding에서 recorder.stop()를 수행하고자 했으나 에러 발생. 즉, 기존의 동작중인 recorder가 없다.");
                    } finally{
                        //recorder.release(); // 원래 release 위치는 여기였다. 에러 해결을 위해 try문으로 이동시켜본 상황.
                        recorder = null;
                    }
                }

                // 새 recorder 동작.
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
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

                // ToDoList1. audioUri = null로 되어있다. 해결해야하는 문제.
                // 현재 설정한 저장위치는 _data=/sdcard/tempRecorded.wav ( = RECORD_FILE)
                // 밑의 sendDjango와 경로 맞춰줘야한다.
                // HereHear앱에서 "파일"앱의 데이터베이스에 접근하기 위한 기능인듯?
                // 근데 현재 실질적으로 사용되지 않는다. Django에 보내기 위한 파일을 sdcard에 직접접근하기 때문. 고로 해당 코드는 잠시 주석처리한다.
                // 해당 ContentResolve 코드는 Django와의 통신이 마무리되면 삭제해도 무방하다.
                /*
                Uri audioUri = getContext().getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        values
                );
                Log.d("msg", "@@@@ recordBtn의 endRecoding에서 values의 uri 지정 완료!");
                Log.d("msg-audioUri : ", String.valueOf(audioUri));
                if(audioUri == null){
                    Log.d("SampleAudioRecorder", "Audio insert failed.");
                    return;
                }
            }

     */

            /*

            // ★ToDoList
            // 실험시작 - 기능 구현이 완료되면, 사용하지 않는 코드들 모두 지우기
            @RequiresApi(api = Build.VERSION_CODES.O)
            public String sendDjango(){
                String asrJsonString="";
                String result = "";
                try {
                    Log.d("Msg","**** UPLOADING .WAV to Django 동작 시작!");
                    URL obj = new URL(ASR_URL);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                    Log.d("Msg","**** ASR_URL의 conn : " + conn);
                    //conn.setRequestProperty("X-Arg", "AccessKey=3fvfg985-2830-07ce-e998-4e74df");
                    conn.setRequestProperty("Content-Type", "audio/wav");
                    conn.setRequestProperty("enctype", "multipart/form-data");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    String wavpath = "/sdcard/tempRecorded.wav"; // 본래 오픈소스코드는 아래와 같은 경로 설정인데, 본 프로젝트의 파일 저장 경로는 sdcard를 이용하므로 이와 같이 사용한다.
                    // String wavpath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Recorded/audio.wav"; //audio.wav";
                    File wavfile = new File(wavpath);
                    boolean success = true;
                    if (wavfile.exists()) {
                        Log.d("Msg","**** audio.wav DETECTED: "+wavfile);
                    }
                    else{
                        Log.d("Msg","**** audio.wav MISSING: " +wavfile);
                    }

                    String charset="UTF-8";
                    String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                    String CRLF = "\r\n"; // Line separator required by multipart/form-data.

                    OutputStream output=null;
                    PrintWriter writer=null;

                    // ★★★ ToDoList3
                    // conn에 connect할 때 에러 발생해서 예외처리로 빠진다. 아직 Django 셋팅이 끝나지 않은 문제인듯
                    // 위에서도 conn사용하면 에러발생한다.
                    // Django에서 파일 업로드형식을 더 직접적 링크로 바꿔야할듯
                    // java.net.ConnectException: Failed to connect to localhost/127.0.0.1:8000
                    try {
                        Log.d("Msg","여기서 왜 에러가 발생할까? - 1 - conn을 처음으로 사용하는 위치");
                        output = conn.getOutputStream();
                        Log.d("Msg","여기서 왜 에러가 발생할까? - 2 - conn을 처음으로 사용하는 위치");

                        writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                        byte [] music=new byte[(int) wavfile.length()];//size & length of the file
                        InputStream             is  = new FileInputStream(wavfile);
                        BufferedInputStream bis = new BufferedInputStream   (is, 16000);
                        DataInputStream dis = new DataInputStream(bis);      //  Create a DataInputStream to read the audio data from the saved file
                        int i = 0;
                        copyStream(dis,output);
                    } catch(Exception e){
                        e.printStackTrace();
                        Log.d("Msg","Django에 wav를 전송하기 위한 데이터 처리 과정 중 에러 발생.");
                    }

                    Log.d("Msg","여기서 왜 에러가 발생할까? - 1 - conn을 2번째로 사용하는 위치");
                    conn.connect();
                    Log.d("Msg","여기서 왜 에러가 발생할까? - 2 - conn을 2번째로 사용하는 위치");

                    int responseCode = conn.getResponseCode();
                    Log.d("Msg","POST Response Code : " + responseCode + " , MSG: " + conn.getResponseMessage());

                    if (responseCode == HttpURLConnection.HTTP_OK) { //success
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d("Msg","***ASR RESULT: " + response.toString());


                        JSONArray jresponse=new JSONObject(response.toString()).getJSONObject("Recognition").getJSONArray("NBest");
                        asrJsonString=jresponse.toString();

                        for(int i = 0 ; i < jresponse.length(); i++){
                            JSONObject jsoni=jresponse.getJSONObject(i);
                            if(jsoni.has("ResultText")){
                                String asrResult=jsoni.getString("ResultText");
                                //ActionManager.getInstance().addDebugMessage("ASR Result: "+asrResult);
                                Log.d("Msg","*** Result Text: "+asrResult);
                                result = asrResult;
                            }
                        }
                        Log.d("Msg","***ASR RESULT: " + jresponse.toString());

                    } else {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d("Msg","POST FAILED: " + response.toString());
                        result = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Msg","HTTP Exception: " + e.getLocalizedMessage());
                }
                return result; //"Failed to fetch data!";
            }

            public void copyStream( InputStream is, OutputStream os) {
                final int buffer_size = 4096;
                try {

                    byte[] bytes = new byte[buffer_size];
                    int k=-1;
                    double prog=0;
                    while ((k = is.read(bytes, 0, bytes.length)) > -1) {
                        if(k != -1) {
                            os.write(bytes, 0, k);
                            prog=prog+k;
                            double progress = ((long) prog)/1000;///size;
                            Log.d("Msg","UPLOADING: "+progress+" kB");
                        }
                    }
                    os.flush();
                    is.close();
                    os.close();
                } catch (Exception ex) {
                    Log.d("Msg","File to Network Stream Copy error "+ex);
                }
            }
            // 실험 종료

            // 임시 sendDjango 실험체 백업. 위의 sendDjango가 완성되면 지워도 무방.
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void TempsendDjango(){
                // 여기서부터 실험.
                /*
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

               */

            /*

            // onClick method에 의해 실제 동작하는 내용
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // 버튼을 클릭하면 일단 startRecoding을 수행한다.
                Log.d("Msg","startRecoding 동작! 1번만 수행되야 정상.");
                startRecoding();

                // 4초마다 반복 동작할 업무 내역.
                mTask = new TimerTask(){
                    public void run(){
                        Log.d("msg","4초 경과! 4초마다 수행되야 정상.");
                        // 4초마다 반복할 업무를 여기에 지정
                        endRecoding(); // 파일 저장 전에, 기존 파일 여부 확인 후 삭제

                        // ★★★ ToDoList4
                        // 장고연결과 startRecodinig은 Thread하고자 하는데, 현재는 절차식으로 구동되는 듯.
                        // Thread로 구현해야 정상적으로 4초 단위 wav파일이 생성된다.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 장고와 연결하여 temp wav 전달, return값에 따라 UI에 표시
                                Log.d("Msg","sendDjango, startRecording Thread 동작");
                                sendDjango();
                                startRecoding();
                            }
                        }).start();
                    }
                };
                // 4초단위 Timer 설정.
                mTimer = new Timer();
                mTimer.schedule(mTask, 4000, 4000);
            }
        });

        // pause를 클릭했을 때, onClick method에 의해 호출되는 endRecoding. 여기서는 recorder를 완전히 stop한다.
        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void endRecoding(){
                Log.d("msg", "온전하게 기능을 종료시키는 버튼 클릭!");
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
                Log.d("msg", "온전하게 기능을 종료 완료!");
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