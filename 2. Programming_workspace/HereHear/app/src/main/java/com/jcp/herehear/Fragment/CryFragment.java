package com.jcp.herehear.Fragment;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcp.herehear.Class.CryData;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class CryFragment extends Fragment {

    /* View */
    private RecyclerView recyclerView;                 // 리사이클러 뷰
    private RecyclerAdapter recyclerAdapter;           // 리사이클러 뷰 어댑터
    private TextView CrytxtTime;                       // 진행 시간 표시 뷰

    /* 생성자 */
    public CryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cry, container, false);

        /* View 연동 */
        recyclerView = view.findViewById(R.id.CryFragment_RecyclerView_recyclerView);
        CrytxtTime = view.findViewById(R.id.CryFragment_TextView_time);
        CrytxtTime.setText("11:43:00");//진행시간 text지정


        /* RecyclerView 처리 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);


        return view;
    }


    /*데이터: [시간, 소리크기] 리스트 Array*/
    private class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        /* 임시 데이터 */
        private ArrayList<CryData> listData = new ArrayList<>();

        /* constructor - 임시 데이터 셋 생성 */
        /* 추후 리스트에 나타낼 데이터의 용도에 맞게 따로 커스터마이징 해서 설정해주어야 함
         * 현재 시안에는 cry에 시간, 소리크기가 나와있으므로 그것으로 잡음*/
        public RecyclerAdapter() {

            CryData tmp1 = new CryData("오전8:02", "80dB");
            CryData tmp2 = new CryData("오전11:25", "140dB");

            listData.add(tmp1);
            listData.add(tmp2);
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

            holder.whenCryText.setText(curData.getWhenCry());
            holder.howCryText.setText(curData.getHowCry());

        }


        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView whenCryText;
        private TextView howCryText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            whenCryText = itemView.findViewById(R.id.CryFragmentAdapter_TextView_whenCry);
            howCryText = itemView.findViewById(R.id.CryFragmentAdapter_TextView_howCry);
        }

    }

}