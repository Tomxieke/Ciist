package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;

public class ResetActivity extends AppCompatActivity {

    private Context mcontext;

    private CiistTitleView titleView;
    private EditText password;
    private EditText password_sure;
    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mcontext = getApplicationContext();
        initview();
    }

    private void initview() {

        password=(EditText)findViewById(R.id.reset_password);
        password_sure=(EditText)findViewById(R.id.reset_password_sure);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(password.hasFocus()==false){
                    String passwordsa=password.getText().toString();
                    if(passwordsa.length()<6){
                        Toast.makeText(ResetActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
                    }else if(passwordsa.length()>20){
                        Toast.makeText(ResetActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        password_sure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(password_sure.hasFocus()==false){
                    String passwords=password.getText().toString();
                    String confirmpassword=password_sure.getText().toString();
                    if(passwords.equals(confirmpassword)){
                        password_sure.setTextColor(Color.GREEN);
                    }else{
                        Toast.makeText(ResetActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                        password_sure.setTextColor(Color.RED);
                    }

                }else {
                    password_sure.setTextColor(Color.BLACK);

                }
            }
        });

        next=(Button)findViewById(R.id.reset_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(password_sure.getText().toString())) {
                    startActivity(new Intent(ResetActivity.this, ResetSuccessActivity.class));
                } else {
                    Toast.makeText(ResetActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                }
            }
        });

        titleView=(CiistTitleView)findViewById(R.id.reset_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
