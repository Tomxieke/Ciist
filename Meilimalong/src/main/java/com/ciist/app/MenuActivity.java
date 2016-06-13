package com.ciist.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.ciist.customview.xlistview.CiistTitleView;

import java.util.ArrayList;


public class MenuActivity extends FragmentActivity {

    private CiistTitleView menu_title;
    private RadioGroup mMenuGroup;
    private ViewPager mContentPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        initView();
    }

    private void initView() {
        menu_title=(CiistTitleView)findViewById(R.id.documen_titleview);
        menu_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menu_title.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,SearchActivity.class));
            }
        });
        mContentPager = (ViewPager) findViewById(R.id.content_viewpager);
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


        mMenuGroup = (RadioGroup) findViewById(R.id.menu_radioGroup);
        ((RadioButton) mMenuGroup.getChildAt(0)).setChecked(true);
        mMenuGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.personal_btn:
                        mContentPager.setCurrentItem(0);
                        break;
                    case R.id.more_btn:
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
        listData.add(DocumentManaFragment.newInstance("a"));
        listData.add(DocumentQiyeFragment.newInstance("a"));
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
