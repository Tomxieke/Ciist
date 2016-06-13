package com.ciist.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ciist.customview.xlistview.CiistTitleView;

public class SettingActivity extends AppCompatActivity {

    private CiistTitleView titleView;
    private LinearLayout xiugai_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initview();
    }

    private void initview() {

        titleView=(CiistTitleView)findViewById(R.id.setting_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        xiugai_password=(LinearLayout)findViewById(R.id.setting_xiugaimima_lin);
        xiugai_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,ResetPasswordActivity.class));
            }
        });
    }
}
