package com.jcp.herehear.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.jcp.herehear.Fragment.CryFragment;
import com.jcp.herehear.Fragment.DangerFragment;
import com.jcp.herehear.R;
import com.jcp.herehear.Fragment.SttFragment;

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
    private TabLayout mTabLayout;           // 탭 레이아웃

    /* 탭 번호 정의 - 0.번역, 1.위험소리, 2.울음소리 */
    private final int TAB_DICTATE = 0;
    private final int TAB_DANGER = 1;
    private final int TAB_CRY = 2;

    /* 탭 아이콘 파일 정의 */
    private int tabIcons[] = {
            R.drawable.icon_dictate,
            R.drawable.icon_danger,
            R.drawable.icon_cry
    };

    /* 탭 아이콘-비선택 파일 정의 */
    private final int tabIcons_unSelected[] = {
            /*

                선택되지 않았을 때 아이콘의 파일 기록할 것.
                ex:) 불투명한 버전 아이콘

            */
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

        /* ViewPager 및 TabLayout 세팅 */
        mViewPager = findViewById(R.id.MainActivity_ViewPager_viewPager);
        mTabLayout = findViewById(R.id.MainActivity_TabLayout_tabLayout);
        setViewPager(mViewPager);
        setTabLayout(mTabLayout, mViewPager);

    }

    /*

        TabLayout과 ViewPager를 연동하고
        탭 레이아웃의 세팅(아이콘, 탭 선택/비선택 시 동작)

    */
    private void setTabLayout(TabLayout tabLayout, ViewPager viewPager) {

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(TAB_DICTATE).setIcon(tabIcons[TAB_DICTATE]);
        tabLayout.getTabAt(TAB_DANGER).setIcon(tabIcons[TAB_DANGER]);
        tabLayout.getTabAt(TAB_CRY).setIcon(tabIcons[TAB_CRY]);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /* 현재 탭이 선택 되었을 때 */
                int pos = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /* 현재 탭이 비선택 되었을 때 */
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /* 현재 탭이 다시 선택 되었을 때 */
            }
        });

    }


    /*

        ViewPager 의 초기 세팅을 한다.
        페이지 어댑터를 생성하고 Fragment 들을 등록한 뒤
        ViewPager 에 페이지 어댑터를 장착

    */
    public void setViewPager(ViewPager viewPager){

        MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SttFragment(), "음성 번역");
        adapter.addFragment(new DangerFragment(), "생활 알림");
        adapter.addFragment(new CryFragment(), "아이 케어");
        viewPager.setAdapter(adapter);

    }


    /*

        프래그먼트 페이지 어댑터 - 내부클래스로 구현

    */
    private class MainPageAdapter extends FragmentPagerAdapter{

        /* 멤버변수 */
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /* 생성자 */
        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        /* ViewPager에 새로운 Fragment를 추가한다. */
        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /* position에 해당하는 Fragment의 title을 반환하다. */
        public String getPageTitle(int position){
            return mFragmentTitleList.get(position);
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
