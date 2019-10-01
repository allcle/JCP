package com.jcp.herehear.Class;

import android.util.Log;

import java.util.TimerTask;

/*

    Record 부분의 코드를 refactoring 해서 따로 빼주었습니다.

    .wav 파일의 정확한 포맷이 맞지 않아 WavRecorder 클래스로 대체하였습니다..
    (.wav 파일 포맷이 정확하지 않아 Librosa 에서 load가 불가능했음)

*/

public class RecordTask extends TimerTask {

    /* member variables */
//    private MediaRecorder recorder;                   // 미디어 레코더
    private WavRecorder wavRecorder;                    // wav 형식 레코더
    private HttpSoundRequest.AsyncResponse delegate;    // Http 콜백처리를 위한 delegate

    /* constructor */
    public RecordTask(WavRecorder wavRecorder, HttpSoundRequest.AsyncResponse delegate) {
        this.wavRecorder = wavRecorder;
        this.delegate = delegate;
    }

    /* Timer Task */
    @Override
    public void run() {

        /* 데시벨 계산 */
        Log.d("msg", "4초 경과! 4초마다 수행되야 정상.");
        /* TODO : 이곳에 powerDb = 20 * log10(wavRecorder.getMaxAmplitude()) 를 써야 함 */
        //final double powerDb = 20 * Math.log10 (wavRecorder.getMaxAmplitude());
        final double powerDb = wavRecorder.getMaxAmplitude();

        /* Recording 종료 및 .wav 파일 저장 */
        wavRecorder.stopRecording();

        /* Runnable 인터페이스를 상속한 요청 클래스 사용 */
        new HttpSoundRequest(powerDb, delegate).start();

        /* Recording 재시작 */
        wavRecorder.startRecording();

    }
}
