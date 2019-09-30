package com.jcp.herehear.Class;

import android.util.Log;

import com.jcp.herehear.Fragment.DangerFragment;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpSoundRequest extends Thread {

    private final String SERVER_URL = "http://10.0.2.2:8000/uploads/";
    private double powerDb;
    private final double LIMIT_DECIBEL = 80.0;
    private final MediaType CONTENT_TYPE = MediaType.parse("audio/wav");


    public HttpSoundRequest(double powerDb) {
        this.powerDb = powerDb;
    }

    @Override
    public void run() {
        // 장고와 연결하여 temp wav 전달, return값에 따라 UI에 표시
        Log.d("Msg", "sendDjango, startRecording Thread 동작");
        // ★★★ ToDoList 저장한 wav 파일에서 maximum 데시벨이 가이드라인보다 높은 경우 sendDjano. 데시벨은 몇이 적당?
        /* 에뮬레이터로는 녹음이 안되서 테스트를 위해 임시로 데시벨 조절함 원래 powerDb 값이 들어가야 함 */
        if (90 >= LIMIT_DECIBEL) {
            Log.d("msg : ", "powerDb가 80을 초과했습니다.");
            /* 여기서 Http 요청 */
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                File wavFile = new File(DangerFragment.RECORD_FILE_DIR);
                /*

                    drilling20.wav -> 디버깅 용도

                    Librosa load 에러가 계속 발생
                    Error opening <InMemoryUploadedFile: tpRecorded (audio/wav)>: File contains data in an unknown format.

                    Http 파일 전송방식에서 문제가 발생한 줄 알고
                    다른 방식 시도 및 Django 서버 수정 (Multipart/form-data, --data-binary )
                    그러나 계속 동일한 에러 발생

                    기존의 온전한 .wav 파일 에뮬레이터에 넣어서 전송해봄 -> 성공..
                    녹음을 저장할때 .wav 포맷이 정확히 일치해야 Librosa 로드가능하다는 것 확인.

                */
//                File wavFile = new File("/sdcard/drilling20.wav");        // DEBUG
                Log.d("WAVFILE to string", wavFile.toString());
                if(wavFile.isFile()){
                    Log.d("파일 유효성 검사", "파일 정상 임");
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file",wavFile.getName(), RequestBody.create(wavFile, CONTENT_TYPE))
                        .build();

                Request request = new Request.Builder()
                        .url(SERVER_URL)
                        .post(requestBody)
                        .build();

                try{
                    Response response = okHttpClient.newCall(request).execute();
                    if (!response.isSuccessful()){
                        Log.d("Request Failed!", "Unexpected code " + response);
                    }
                    Log.d("Response Success! --", response.body().string());

                    /* TODO : 이부분에서 UI 핸들링 한다!! */


















                }catch (IOException e){
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Log.d("msg : ", "powerDb가 80을 초과하지 못했습니다 : " + powerDb);
        }
    }

}
