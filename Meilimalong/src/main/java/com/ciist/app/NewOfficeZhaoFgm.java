package com.ciist.app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


public class NewOfficeZhaoFgm extends android.support.v4.app.Fragment {
    private Context mcontext;

    private String mTitle = "";
    private String mUsername = "";
    private String mIdentify = "";
    private String mSelfids = "";
    private String mDuties = "";

    private RelativeLayout niqianyue;
    private RelativeLayout yiqianyue;
    private RelativeLayout zhongdian;
    private RelativeLayout tichu;
    private RelativeLayout zaitan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_office_activity_zhao, null);
        mcontext = getActivity();

        Bundle data = getArguments();//获得从activity中传递过来的值
        mIdentify = data.getString("identify");
        mUsername = data.getString("username");
        mSelfids = data.getString("selfids");
        initview(view);
        return view;
    }

    private void initview(View v) {
        niqianyue=(RelativeLayout)v.findViewById(R.id.niqianyue_rl);
        yiqianyue=(RelativeLayout)v.findViewById(R.id.yiqianyue_rl);
        zhongdian=(RelativeLayout)v.findViewById(R.id.zhongdian_rl);
        tichu=(RelativeLayout)v.findViewById(R.id.tichu_rl);
        zaitan=(RelativeLayout)v.findViewById(R.id.zaitan_rl);

//        Intent intent = this.getIntent();
//        mIdentify = intent.getStringExtra("identify");
//        mUsername = intent.getStringExtra("username");
//        mSelfids = intent.getStringExtra("selfids");
        zaitan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(mcontext, IndexOfOACoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                bundle.putString("title", "在谈项目");
                bundle.putString("action", "nqyxm");
                bundle.putString("subjectname", "在谈项目");
                bundle.putString("subjectcode", "ZSPROJECTSINDEX");
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        niqianyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(mcontext, IndexOfOACoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                bundle.putString("title", "拟签约项目");
                bundle.putString("action", "nqyxm");
                bundle.putString("subjectname", "拟签约项目");
                bundle.putString("subjectcode", "5F4487B7-13FF-4F55-B0F3-5E8AE57AC031");
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });
        yiqianyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(mcontext, IndexOfOACoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                bundle.putString("title", "已签约项目");
                bundle.putString("action", "yqyxm");
                bundle.putString("subjectname", "已签约项目");
                bundle.putString("subjectcode", "82679EB8-419C-46D0-99EA-67876486FB23");
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });
        zhongdian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(mcontext, IndexOfOACoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                bundle.putString("title", "重点更进项目");
                bundle.putString("action", "zdgjxm");
                bundle.putString("subjectname", "重点更进项目");
                bundle.putString("subjectcode", "557C6885-8D79-40CA-A0DD-E4BE0DD7023B");
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });
        tichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(mcontext, IndexOfOACoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                bundle.putString("title", "已剔除项目");
                bundle.putString("action", "ytcxm");
                bundle.putString("subjectname", "已剔除项目");
                bundle.putString("subjectcode", "081D3059-A8FE-45B3-A2C4-FBD8E683F9CC");
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });
    }


}
