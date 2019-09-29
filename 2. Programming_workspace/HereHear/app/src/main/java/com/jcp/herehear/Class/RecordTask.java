package com.jcp.herehear.Class;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.util.Log;
import com.jcp.herehear.Fragment.DangerFragment;
import java.util.TimerTask;
import static java.lang.Math.log10;

/*

    Record 부분의 코드를 refactoring 해서 따로 빼주었습니다.

    .wav 파일의 정확한 포맷이 맞지 않아 WavRecorder 클래스로 대체하였습니다..
    (.wav 파일 포맷이 정확하지 않아 Librosa 에서 load가 불가능했음)

    TODO : getMaxAmplitude 값을 통해 데시벨 처리하는 것이 WavRecorder 클래스에 없습니다.
    TODO : WavRecorder 는 AudioRecord 를 .wav 전용으로 래핑한 클래스인데
    TODO : AudioRecord 에 주어진 함수를 통해 WavRecorder.getMaxAmplitude() 함수를 구현해야 합니다.

*/

public class RecordTask extends TimerTask {

    /* member variables */
//    private MediaRecorder recorder;             // 미디어 레코더
    private WavRecorder wavRecorder;            // wav 형식 레코더

    /* constructor */
    public RecordTask(WavRecorder wavRecorder) {
        this.wavRecorder = wavRecorder;
    }


    /* Timer Task */
    @Override
    public void run() {

        Log.d("msg", "4초 경과! 4초마다 수행되야 정상.");
        // 4초마다 반복할 업무를 여기에 지정
        // ★★★ ToDoList 데시벨 저장
//        final double powerDb = 20 * log10 (recorder.getMaxAmplitude());
        /* TODO : 이곳에 powerDb = 20 * log10(wavRecorder.getMaxAmplitude()) 를 써야 함 */
//        Log.d("powerDb : ", String.valueOf(powerDb));
//        endRecoding(); // 파일 저장 전에, 기존 파일 여부 확인 후 삭제
        wavRecorder.stopRecording();

        /* Runnable 인터페이스를 상속한 요청 클래스 사용 */
        new HttpSoundRequest(0).start();

        /*

            startRecording 함수가 스레드 내부의 통신요청
            코드 다음에 있어서 절차적으로 진행되었습니다.
            스레드 바깥으로 레코드를 빼주었습니다.

        */
//        startRecoding();
        wavRecorder.startRecording();

    }


    /*

        기존 MediaRecorder 를 통해 구현한 레코딩 코드 보존.

    */
//    public void startRecoding() {
//        Log.d("msg", "#### recordBtn의 startRecoding 동작!");
//        if (recorder != null) {
//            try {
//                recorder.stop();
//                recorder.release();
//            } catch (RuntimeException e) {
//                Log.d("msg", "#### recordBtn의 startRecoding에서 recorder.stop()를 수행하고자 했으나 에러 발생. 즉, 기존의 동작중인 recorder가 없다.");
//            } finally {
//                recorder = null;
//            }
//        }
//        // 새 recorder 동작.
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setOutputFile(DangerFragment.RECORD_FILE_DIR);
//
//        Log.d("msg", "#### recordBtn의 startRecoding에서 새로운 recorder 설정 완료.");
//        try {
//            recorder.prepare();
//            recorder.start();
//            Log.d("msg", "#### recordBtn의 startRecoding에서 새로운 recorder.start() 성공!.");
//        } catch (Exception ex) {
//            Log.e("SampleAudioRecorder", "Exception : ", ex);
//        }
//    }
//
//    public void endRecoding(){
//        Log.d("msg", "@@@@ recordBtn의 endRecoding 동작!");
//        if (recorder == null) return;
//
//        ContentValues values = new ContentValues(10);
//
//        //mTimer.cancel(); // mTimer을 여기서 중단시키는 코드가 원래 있었는데, 논리 상 안맞고 타이머가 중단되므로 주석처리한 상황
//
//        // 이부분이 values를 저장하는 부분.
//        // ★ ToDoList
//        // 기존 파일이 있는지 여부 먼저 파악 후, 있으면 삭제하기 기능을 구현하길 요망한다.
//        values.put(MediaStore.MediaColumns.TITLE, "JCP");
//        values.put(MediaStore.Audio.Media.ALBUM, "tempRecorded");
//        values.put(MediaStore.Audio.Media.ARTIST, "HereHear");
//        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "toSendDjango");
//        values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
//        values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
//        values.put(MediaStore.MediaColumns.DATE_ADDED,
//                System.currentTimeMillis() / 1000);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
//        values.put(MediaStore.Audio.Media.DATA, DangerFragment.RECORD_FILE_DIR);
//
//        Log.d("msg", "@@@@ recordBtn의 endRecoding에서 values 저장 완료!");
//        Log.d("msg-values : ", String.valueOf(values));
//    }
//
//    public void finishRecording(){
//        Log.d("msg", "온전하게 기능을 종료시키는 버튼 클릭!");
//        if (recorder == null)
//            return;
//        try{
//            recorder.stop();
//        }catch (RuntimeException e){
//            Log.d("StopRecording Error", " while finishRecording() : " + e);
//        }finally {
//            recorder.release();
//        }
//        recorder = null;
//        ContentValues values = new ContentValues(10);
//    }

}
