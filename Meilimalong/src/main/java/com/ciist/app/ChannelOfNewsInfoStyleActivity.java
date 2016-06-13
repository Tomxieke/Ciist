package com.ciist.app;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.Toast;

import com.ciist.entities.SubjectsInfo;
import com.ciist.toolkits.ChannelOfNewsInfoStylePagerAdapter;

import java.util.List;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ChannelOfNewsInfoStyleActivity extends AppCompatActivity {

    private ChannelOfNewsInfoStylePagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private List<SubjectsInfo> mSubjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_of_newsinfo_style_activity);

        Intent intent = this.getIntent();
        mSubjectList = (List<SubjectsInfo>) intent.getSerializableExtra("subjectlist");
        mSectionsPagerAdapter = new ChannelOfNewsInfoStylePagerAdapter(getSupportFragmentManager(), getSubjects(0));
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    List<SubjectsInfo> getSubjects(int fcode) {
        return mSubjectList;
    }


    //class end
}
