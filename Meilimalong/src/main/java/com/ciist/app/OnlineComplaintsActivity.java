package com.ciist.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ciist.customview.xlistview.CiistTitleView;

import java.util.ArrayList;

public class OnlineComplaintsActivity extends FragmentActivity {

    private CiistTitleView online_com_title;
    private RadioGroup mMenuGroup;
    private ViewPager mContentPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_complaints);
        initview();
    }

    private void initview() {

        online_com_title=(CiistTitleView)findViewById(R.id.online_com_titleview);
        online_com_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContentPager = (ViewPager) findViewById(R.id.online_com_viewpager);
        ViewPagerFragmentAdapter pagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        pagerAdapter.setFragment(getFragment());
        mContentPager.setAdapter(pagerAdapter);
        mContentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) mMenuGroup.getChildAt(position)).toggle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });


        mMenuGroup = (RadioGroup) findViewById(R.id.online_com_radioGroup);
        ((RadioButton) mMenuGroup.getChildAt(0)).setChecked(true);
        mMenuGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.online_com_btn:
                        mContentPager.setCurrentItem(0);
                        break;
                    case R.id.online_com_more_btn:
                        mContentPager.setCurrentItem(1);
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
        listData.add(OnlineTousuFragment.newInstance("a"));
        listData.add(OnlineHuifuFragment.newInstance("a"));
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
