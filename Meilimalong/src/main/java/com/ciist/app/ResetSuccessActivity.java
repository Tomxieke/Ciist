package com.ciist.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ciist.customview.xlistview.CiistTitleView;

public class ResetSuccessActivity extends AppCompatActivity {

    private CiistTitleView titleView;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_success);
        titleView=(CiistTitleView)findViewById(R.id.reset_success_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button=(Button)findViewById(R.id.reset_success_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetSuccessActivity.this,SignActivity.class));
                ResetSuccessActivity.this.finish();
            }
        });
    }
}
