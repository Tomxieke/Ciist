package com.ciist.toolkits;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ciist.app.ChannelOfNewsInfoStyleListFragment;
import com.ciist.entities.SubjectsInfo;


import java.util.List;

/**
 * Created by CIIST on 2015/11/8 0008.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ChannelOfNewsInfoStylePagerAdapter extends FragmentStatePagerAdapter {
//    List<String> mTitles;
    List<SubjectsInfo> mSubjectInfoLists;

    public ChannelOfNewsInfoStylePagerAdapter(FragmentManager fm, List<SubjectsInfo> subjectsInfoList) {
        super(fm);
        mSubjectInfoLists = subjectsInfoList;
    }

    /**
     * 只需要实现下面两个方法即可。
     */
    @Override
    public Fragment getItem(int position) {
//        System.out.println("           ********************************************         getItem position="+position);
        SubjectsInfo sj=mSubjectInfoLists.get(position);
        return ChannelOfNewsInfoStyleListFragment.newInstance(position, sj.get_subjectCode());
    }

    @Override
    public int getCount() {
        return  mSubjectInfoLists.size();

    }

    @Override
    public CharSequence getPageTitle(int position) {
        SubjectsInfo sj=mSubjectInfoLists.get(position);
        return sj.get_subjectName();
//        if (mTitles == null) getTitles();
//        return mTitles.get(position);
    }

//
//    private void getTitles() {
//        if (mTitles == null) mTitles = new ArrayList<String>();
//
//        mTitles.add("今日马龙");
//        mTitles.add("通知公告");
//        mTitles.add("部门动态");
//        mTitles.add("乡镇动态");
//        mTitles.add("工业交通");
//        mTitles.add("统计信息");
//        mTitles.add("农业农村");
//        mTitles.add("社会公益");
//        mTitles.add("人事任免");
//        mTitles.add("招考信息");
//    }
}
