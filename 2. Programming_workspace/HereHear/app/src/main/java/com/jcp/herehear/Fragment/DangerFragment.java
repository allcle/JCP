package com.jcp.herehear.Fragment;

import android.content.ContentValues;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jcp.herehear.R;

import java.io.IOException;

import android.content.*;

public class DangerFragment extends Fragment {

    final private static String RECORD_FILE = "/sdcard/tempRecorded.wav";
    MediaRecorder recorder;

    /* 생성자 */
    public DangerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("1", "DangerFragment Constructer wawawawawa");

        View view = inflater.inflate(R.layout.fragment_danger, container, false);

        Button recordBtn = (Button) view.findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) view.findViewById(R.id.recordStopBtn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("3", "DangerFragment Constructer Stop Button was Clicked!!!");
                if (recorder == null)
                    return;
                recorder.stop();
                recorder.release();
                recorder = null;

                ContentValues values = new ContentValues(10);

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