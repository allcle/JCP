package com.jcp.herehear.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.jcp.herehear.Fragment.CryFragment;
import com.jcp.herehear.Fragment.DangerFragment;
import com.jcp.herehear.Fragment.SttFragment;
import com.jcp.herehear.R;

import java.util.ArrayList;
import java.util.List;

/*

    메인 액티비티 클래스.
    로딩화면 이후 진입하는 메인 화면.
    뷰페이저와 탭레이아웃으로 구성되어 있으며
    각각의 뷰페이저에는 3개의 기능(번역, 위험소리감지, 아이울음소리)을
    보여주고 작동하는 Fragment 들이 연결되어 있음.

*/

public class MainActivity extends AppCompatActivity {

    /* 멤버변수 */
    private ViewPager mViewPager;           // 뷰 페이저
    private AppBarLayout mAppLayout;        // App 바 레이아웃
    private TabLayout mTabLayout;           // 탭 레이아웃
    private ImageView imgStt;               // 번역하기 탭 이미지뷰
    private ImageView imgDanger;            // 위험소리 탭 이미지뷰
    private ImageView imgCry;               // 울음소리 탭 이미지뷰

    /* 탭 번호 정의 - 0.번역, 1.위험소리, 2.울음소리 */
    private final int TAB_DICTATE = 0;
    private final int TAB_DANGER = 1;
    private final int TAB_CRY = 2;
    //private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;

    /* 탭 메뉴 파일 정의 */
    private int tabImg[][] = {

            /*

                2차원 배열의 첫번째 인덱스는 현재 결정해주어야 할 탭의 인덱스
                2차원 배열의 두번째 인덱스는 현재 선택된 탭의 인덱스

                ex:) cry fragment 가 선택된 상태에서 stt 탭의 이미지
                tabImg[TAB_DICTATE][TAB_CRY];
                tabImg[결정해야할 탭][현재 선택된 탭]

            */

            {R.drawable.tab_stt_selected_stt, R.drawable.tab_stt_selected_danger, R.drawable.tab_stt_selected_cry},
            {R.drawable.tab_danger_selected_stt, R.drawable.tab_danger_selected_danger, R.drawable.tab_danger_selected_cry},
            {R.drawable.tab_cry_selected_stt, R.drawable.tab_cry_selected_danger, R.drawable.tab_cry_selected_cry}
    };


    /*

        onCreate
        뷰페이저를 xml 컨테이너와 매칭하고,
        뷰페이저의 설정값을 세팅한다.(Fragment Page Adapter를 set)

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Permission Check */
        int record_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int write_permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ArrayList<String> permissions = new ArrayList<String>();
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            permissions.add(Manifest.permission.RECORD_AUDIO);

        if(permissions.size()>0){
            String[] reqPermissionArray = new String[permissions.size()];
            reqPermissionArray = permissions.toArray(reqPermissionArray);
            ActivityCompat.requestPermissions(this, reqPermissionArray, 1);
        }

        /*
        if (write_permissionCheck == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        if (record_permissionCheck== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
*/


        /* ViewPager 및 TabLayout 세팅 */
        mAppLayout = findViewById(R.id.MainActivity_AppBarLayout_barLayout);
        mViewPager = findViewById(R.id.MainActivity_ViewPager_viewPager);
        mTabLayout = findViewById(R.id.MainActivity_TabLayout_tabLayout);
        setViewPager(mViewPager, mTabLayout);
    }

    public void onRequestPermissionResult(int requestCode,
                                          String permissions[], int[] grantResults){
        switch(requestCode){
            case 1:{
                if(grantResults.length==2){
                    Toast.makeText(this, "필요한 승인이 모두 허가되었습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(this, "두 가지 승인이 모두 허가되지 않았습니다.", Toast.LENGTH_LONG).show();
                    else if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "Record Audio 승인이 허가 되었습니다. 저장 승인을 필요로합니다.", Toast.LENGTH_LONG).show();
                    else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this, "저장 승인이 허가 되었습니다. Record Audio 승인을 필요로합니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /*


        ViewPager 의 초기 세팅을 한다.
        페이지 어댑터를 생성하고 Fragment 들을 등록한 뒤
        ViewPager 에 페이지 어댑터를 장착

    */
    public void setViewPager(final ViewPager viewPager, final TabLayout tabLayout) {

        final MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SttFragment(), "음성 번역");
        adapter.addFragment(new DangerFragment(), "생활 알림");
        adapter.addFragment(new CryFragment(), "아이 케어");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setupTabCustomView(tabLayout, adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int selectedIdx) {

                /*
                    각 페이지가 선택될 때 마다 이벤트를 정의한다.

                    탭의 이미지를 갱신함.

                */

                int pageSize = adapter.getCount();
                for (int currentIdx = 0; currentIdx < pageSize; currentIdx++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(currentIdx);
                    ImageView tpImg = (ImageView) tab.getCustomView();
                    tpImg.setImageResource(
                            tabImg[currentIdx][selectedIdx]
                    );
                    tab.setCustomView(tpImg);

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /* 각 탭 뷰의 커스텀뷰를 셋업 */
    private void setupTabCustomView(TabLayout tabLayout, PagerAdapter adapter) {

        mAppLayout.setExpanded(true, true);
        int pageSize = adapter.getCount();
        for (int currentIdx = 0; currentIdx < pageSize; currentIdx++) {
            TabLayout.Tab tab = tabLayout.getTabAt(currentIdx);
            ImageView tpImg = new ImageView(this);
            /* 이미지 가로 스케일 맞춘다 */
            tpImg.setScaleType(ImageView.ScaleType.FIT_XY);
            tpImg.setImageResource(
                    tabImg[currentIdx][TAB_DICTATE]
            );
            tab.setCustomView(tpImg);
        }

    }


    /*

        프래그먼트 페이지 어댑터 - 내부클래스로 구현

    */
    private class MainPageAdapter extends FragmentPagerAdapter {

        /* 멤버변수 */
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /* 생성자 */
        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        /* ViewPager에 새로운 Fragment를 추가한다. */
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /* position에 해당하는 Fragment의 title을 반환하다. */
        public String getPageTitle(int position) {
            return "";
//            return mFragmentTitleList.get(position);
        }

        /* position에 해당하는 Fragment를 반환한다. */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /* ViewPager에 넣을 Fragment 집합의 개수를 반환한다. */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
