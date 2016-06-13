package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

public class NewOfficeZhaoActivity extends AppCompatActivity {

    private Context mcontext;

    private String mUsername = "";
    private String mIdentify = "";
    private String mSelfids = "";
    private String mDuties = "";

    private RelativeLayout niqianyue;
    private RelativeLayout yiqianyue;
    private RelativeLayout zhongdian;
    private RelativeLayout tichu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_office_activity_zhao);
        initview();
    }

    private void initview() {

        niqianyue=(RelativeLayout)findViewById(R.id.niqianyue_rl);
        yiqianyue=(RelativeLayout)findViewById(R.id.yiqianyue_rl);
        zhongdian=(RelativeLayout)findViewById(R.id.zhongdian_rl);
        tichu=(RelativeLayout)findViewById(R.id.tichu_rl);
        Intent intent = this.getIntent();
        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");

        niqianyue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent tmpIntent = new Intent(NewOfficeZhaoActivity.this, CoverStyleActivity.class);
            Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2","拟签约项目");

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
                Intent tmpIntent = new Intent(NewOfficeZhaoActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2","已签约项目");

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
                Intent tmpIntent = new Intent(NewOfficeZhaoActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2","重点更进项目");


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
                Intent tmpIntent = new Intent(NewOfficeZhaoActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2","已剔除项目");


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
