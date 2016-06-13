package com.ciist.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ciist.customview.xlistview.CiistTitleView;

public class BanjianChaxunActivity extends AppCompatActivity {
private CiistTitleView banjianchaxun_titleview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banshi_zhinan);
        banjianchaxun_titleview=(CiistTitleView)findViewById(R.id.banjianchaxun_titleview);
        banjianchaxun_titleview.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
