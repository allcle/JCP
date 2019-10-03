package com.jcp.herehear.Dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.jcp.herehear.R;

public class FragmentDialog extends DialogFragment {
    private Fragment fragment;

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
//        String selectedLayout = "";
        View view = inflater.inflate(viewList[index], container, false);
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;


//        if(index == 0){
//            View view = inflater.inflate(R.layout.popup_danger_horn, container, false);
//            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//            Dialog dialog = getDialog();
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            return view;
//        }
//        else if(index == 1){
//            View view = inflater.inflate(R.layout.popup_danger_dog, container, false);
//            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//            Dialog dialog = getDialog();
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            return view;
//        }
//        else if(index == 2){
//            View view = inflater.inflate(R.layout.popup_danger_drilling, container, false);
//            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//            Dialog dialog = getDialog();
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            return view;
//        }
//        else if(index == 3){
//            View view = inflater.inflate(R.layout.popup_danger_gun, container, false);
//            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//            Dialog dialog = getDialog();
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            return view;
//        }
//        else{
//            View view = inflater.inflate(R.layout.popup_danger_siren, container, false);
//            fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//            Dialog dialog = getDialog();
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            return view;
//        }

    }


    public void setIndex(int _index) {
        this.index = _index;
    }

    public static void delayTime(long time, final DialogFragment df) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                df.dismiss();
            }
        }, time);
    }
}
