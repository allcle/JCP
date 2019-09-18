package com.jcp.herehear.Class;

/*

    DangerData 는 임시 클래스로
    Http 통신 Request 를 통해 전달 받은 데이터 값 또는
    소리 종류와 관련된 리스트뷰에 들어갈 데이터 정보들을 담는다.

    추후 용도에 맞게 수정해서 써야 함.

*/

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DangerData {

    private String name;
    private Drawable img;
    private boolean isListening;

    /* constructor */
    public DangerData(){

    }

    public DangerData(String name, Drawable img){
        this.name = name;
        this.img = img;
        this.isListening = false;
    }


    /* getter, setter */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public Boolean getListening() {
        return isListening;
    }

    public void setListening(Boolean listening) {
        isListening = listening;
    }

    // ★ToDoList
    // 실험시작 - 기능 구현이 완료되면, 사용하지 않는 코드들 모두 지우기
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String sendDjango() {
        String result = "";
        return result; //"Failed to fetch data!";
    }

    /*
    // 이건 지금 소스코드로 찾은 것. 진짜 그냥 처음부터 구현해보자.
    // ★ToDoList
    // 실험시작 - 기능 구현이 완료되면, 사용하지 않는 코드들 모두 지우기
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String sendDjango(){
        String asrJsonString="";
        String ASR_URL = "http://127.0.0.1:8000"; // Django의 url
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
                InputStream is  = new FileInputStream(wavfile);
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
    */
}
