package com.jcp.herehear.Fragment;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jcp.herehear.R;

import java.util.Timer;
import java.util.TimerTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;

import android.content.*;

public class DangerFragment extends Fragment {

    final private static String RECORD_FILE = "/sdcard/tempRecorded.wav";
    MediaRecorder recorder;

    private Timer mTimer;
    private TimerTask mTask;

    /* 생성자 */
    public DangerFragment() {

    }

    // 서버에 접속해서 파일 업로드 구현 https://stackoverflow.com/questions/34089436/how-to-upload-a-wav-file-using-urlconnection
    // 저장 파일을 임시 폴더로 옮기도록
    // 매번 저장이 제대로 되는지 확인
    // 파일 conflict나지 않도록 조정
    // 버튼 클릭이 아닌 4초마다 스플릿해서 이거 호출하고, 버튼 클릭되면 동작 종료되도록 구현

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