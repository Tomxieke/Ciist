package com.ciist.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ciist.customview.xlistview.CiistTitleView;

import java.util.ArrayList;

public class TodayMaActivity extends AppCompatActivity {


    private CiistTitleView title;
    private RadioGroup Group;
    private ViewPager Pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_ma);
        initView();

    }

    private void initView() {
        title=(CiistTitleView)findViewById(R.id.today_titleview);
        title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TodayMaActivity.this, SearchActivity.class));
            }
        });
        Pager = (ViewPager) findViewById(R.id.today_viewpager);
        ViewPagerFragmentAdapter pagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        pagerAdapter.setFragment(getFragment());
        Pager.setAdapter(pagerAdapter);
        Pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) Group.getChildAt(position)).toggle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        Group = (RadioGroup) findViewById(R.id.today_radioGroup);
        ((RadioButton) Group.getChildAt(0)).setChecked(true);
        Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.malong_btn:
                        Pager.setCurrentItem(0);
                        break;
                    case R.id.nongye_btn:
                        Pager.setCurrentItem(1);
                        break;
                }
            }
        });




    }





    /**
     * 加载Fragment数据
     * @return
     */
    public ArrayList<Fragment> getFragment() {
        ArrayList<Fragment> listData = new ArrayList<>();
        listData.add(ReInformationActivity.newInstance("a"));
        listData.add(NongyekauiActivity.newInstance("a"));
        return listData;
    }


    /**
     * ViewPager 的适配器
     */
    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> listFragment = new ArrayList<Fragment>();

        public ViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }


        public void setFragment(ArrayList<Fragment> data){
            this.listFragment = data;
            notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int i) {
            return listFragment.get(i);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }
    }

}
