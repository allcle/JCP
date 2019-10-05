package com.jcp.herehear.Fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcp.herehear.Activity.MainActivity;
import com.jcp.herehear.Class.AudioListening;
import com.jcp.herehear.Class.CryData;
import com.jcp.herehear.Class.Permission;
import com.jcp.herehear.Class.TimeHandler;
import com.jcp.herehear.R;
import java.util.ArrayList;

public class CryFragment extends Fragment implements TimeHandler.TimeHandleResponse, ValueEventListener, AudioListening {

    /* Time Handler - 타이머 클래스 */
    private final TimeHandler myTimer = new TimeHandler(this);

    /* View */
    private RecyclerView recyclerView;                 // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;           // 리사이클러 뷰 어댑터
    private TextView txtTime;                          // 진행 시간 표시 뷰
    private ImageView imgvPlay;                        // 사운드 리스팅 이미지뷰

    /* Listening */
    private boolean isListening;                       // 현재 듣기 여부
    private long baseTime;                             // 경과 시간 체크를 위한 현재 시간 저장

    /* DB 리스닝 */
    CryFragment thisFragment;                          // 인터페이스 상속 Onclicklistener 내부 사용 위한 저장.
    private FirebaseDatabase firebaseDatabase;         // Firebase DB
    private DatabaseReference databaseReference;       // Firebase DB 특정 경로

    /* 생성자 */
    public CryFragment() {
        thisFragment = this;
    }

    /* 타임 핸들러에 대한 콜백 UI 처리 */
    @Override
    public void processTimerUI() {
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        String elapsedTime = String.format("%02d:%02d:%02d", outTime / 1000 / 60, (outTime / 1000) % 60, (outTime % 1000) / 10);
        txtTime.setText(elapsedTime);
        myTimer.sendEmptyMessage(0);
    }

    /*

        Firebase DB가 변했을 때 처리.
        DB를 읽고 오브젝트를 검증하여 적절한 UI 를 처리한다.

    */
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

        for(DataSnapshot data : iter){
            CryData obj = data.getValue(CryData.class);
            Log.d("Firebase DB Changed!", "CRY STATE : " + obj.getState());
            Log.d("Firebase DB Changed!", "CRY TIME : " + obj.getTime());

            /* 아이가 울고 있는 상태일 경우 업데이트 */
            if(obj.getState() == CryData.STATE_CRY){
                recyclerAdapter.listData.add(obj);
                recyclerAdapter.notifyDataSetChanged();
                Log.d("CryFragment", "DataSet Updated!");
            }

        }
    }

    /* Firebase DB 리스닝 실패했을 때 */
    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("msg", "********************* CryFragment Constructer *********************");

        View view = inflater.inflate(R.layout.fragment_cry, container, false);
        final CryData danger = new CryData();

        /* View 연동 */
        recyclerView = view.findViewById(R.id.CryFragment_RecyclerView_recyclerView);
        txtTime = view.findViewById(R.id.CryFragment_TextView_time);
        txtTime.setText("00:00:00");//진행시간 text지정
        imgvPlay = view.findViewById(R.id.CryFragmentAdapter_ImageView_soundPlay);

        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        /* Firebase init */
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("stateData");

        /* Listening 통신 쪽 처리 */
        imgvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListening) {
                    /* 듣기 시작 */
                    Log.d("Msg", "startRecoding 동작! 1번만 수행되야 정상.");
                    /* Fragment Permission 체크 */
                    MainActivity mainActivity = (MainActivity)getActivity();
                    Permission.CheckAllPermission(mainActivity);
                    boolean permissionCheck = Permission.CheckPermissionProblem(mainActivity);

                    if(permissionCheck){
                        startListening();
                    }
                } else {
                    stopListening();
                }
            }

        });

        return view;
    }

    @Override
    public void startListening() {

        isListening = true;
//        imgvPlay.setImageResource(R.drawable.cry_yes);
        Glide.with(this).load(R.drawable.cry_yes).into(imgvPlay);

        /* Firebase realtime DB Listening 시작 */
        recyclerAdapter.listData.clear();
        recyclerAdapter.notifyDataSetChanged();
        databaseReference.addValueEventListener(thisFragment);

        /* 진행시간 갱신 */
        baseTime = SystemClock.elapsedRealtime();
        myTimer.sendEmptyMessage(0);
    }

    @Override
    public void stopListening() {
        if(!isListening) return;

        /* 듣기 종료 */
        isListening = false;
//        imgvPlay.setImageResource(R.drawable.cry_no);
        Glide.with(this).load(R.drawable.cry_no).into(imgvPlay);

        /* Firebase realtime DB Listening 해제 */
        databaseReference.removeEventListener(thisFragment);

        /* 진행시간 초기화 */
        myTimer.removeMessages(0); //핸들러 메세지 제거
        txtTime.setText("00:00:00");
    }

    /*데이터: [시간, 소리크기] 리스트 Array*/
    private class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        /* 임시 데이터 */
        private ArrayList<CryData> listData = new ArrayList<>();

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함
         * 현재 시안에는 cry에 시간, 소리크기가 나와있으므로 그것으로 잡음*/
        public RecyclerAdapter() {

        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cry_adapter, parent, false);
            return new CryFragment.ItemViewHolder(view);

        }

        /*
            Adapter 리스트 내용이 notify 되었을 경우 onBindViewHolder 가 호출된다.
            다음 함수에서 실제 받아온 listData 의 데이터 정보(DangerData)를 통해
            리스트뷰의 특정 목록의 데이터를 커스터마이징 한다.
        */

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

            final CryData curData = listData.get(position);
            String curNo = String.valueOf(position + 1);

            holder.txtNo.setText(curNo);
            holder.txtCryTime.setText(curData.getTime());

        }

        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNo;
        private TextView txtCryTime;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNo = itemView.findViewById(R.id.CryFragmentAdapter_TextView_index);
            txtCryTime= itemView.findViewById(R.id.CryFragmentAdapter_TextView_cryTime);
        }
    }
}