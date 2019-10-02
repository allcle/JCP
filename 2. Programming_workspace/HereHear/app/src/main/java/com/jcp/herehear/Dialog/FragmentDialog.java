package com.jcp.herehear.Dialog;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.jcp.herehear.R;

public class FragmentDialog extends DialogFragment {
    private Fragment fragment;
    public FragmentDialog() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        /* TODO : 여기서 판별된 class를 integer로 받아서 그에 맞는 레이아웃을 선택해서 view에 넣어야된다. */

        View view = inflater.inflate(R.layout.popup_danger_background, container, false);
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");

        return view;
    }

    public static void delayTime(long time, final DialogFragment df){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                df.dismiss();
            }
        }, time);
    }
}
