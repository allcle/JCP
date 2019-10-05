package com.jcp.herehear.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.jcp.herehear.R;

public class FragmentDialog extends DialogFragment {

    private Fragment fragment;
    private ImageView imgvPopup;

    public FragmentDialog() {
    }

    public int index = 5;

    private final int viewList[] = {
            R.layout.popup_danger_horn,
            R.layout.popup_danger_dog,
            R.layout.popup_danger_drilling,
            R.layout.popup_danger_gun,
            R.layout.popup_danger_siren,
            R.layout.popup_danger_siren, // null 값 안쓰임.
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* TODO : 여기서 판별된 class를 integer로 받아서 그에 맞는 레이아웃을 선택해서 view에 넣어야된다. */
        View view = inflater.inflate(viewList[index], container, false);
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;

    }


    public void setIndex(int _index) {
        this.index = _index;
    }

    /* TODO : 현재 아래 코드 제대로 동작 안함 핸들러 발생 안함
    *
    *   W/System.err: java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
        W/System.err:     at android.os.Handler.<init>(Handler.java:204)
        at android.os.Handler.<init>(Handler.java:118)
        at com.jcp.herehear.Dialog.FragmentDialog.delayTime(FragmentDialog.java:90)
        at com.jcp.herehear.Fragment.DangerFragment.onSoundResponseResult(DangerFragment.java:284)
        at com.jcp.herehear.Class.HttpSoundRequest.run(HttpSoundRequest.java:94)

    * */
//    public static void delayTime(long time, final DialogFragment df) {
//        new Thread(
//        ).run();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                df.dismiss();
//            }
//        }, time);
//    }
}
