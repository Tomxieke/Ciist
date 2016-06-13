package com.ciist.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;

public class YiJianActivity extends AppCompatActivity {

    private ScrollView scr;
    private EditText name_edi;
    private EditText phonenumber_edi;
    private EditText neirong_edi;

    private CiistTitleView titleView;

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yi_jian);
        name_edi = (EditText) findViewById(R.id.yijian_name_edi);
        phonenumber_edi = (EditText) findViewById(R.id.yijian_phonenum_edi);
        neirong_edi = (EditText) findViewById(R.id.yijian_neirong_edi);
        scr = (ScrollView) findViewById(R.id.yijian_scr);
        button=(Button)findViewById(R.id.yijian_button);
        titleView = (CiistTitleView) findViewById(R.id.yijian_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                != null&& web2_phonenumber_edi.getText().toString() != null  && web2_IDnumber_edi.getText().toString() != null &&web2_gridview!=null) {
//                web2_name_edi.getText().toString().equals(web2_phonenumber_edi.getText().toString().equals( web2_IDnumber_edi.getText().toString().equals(web2_gridview!=null)))
                if (name_edi.length() == 0 && phonenumber_edi.length() == 0 && neirong_edi.length() == 0) {
                    Toast.makeText(YiJianActivity.this, "请输入信息", Toast.LENGTH_LONG).show();

                } else if (name_edi.length() != 0 && phonenumber_edi.length() != 0 && neirong_edi.length() != 0) {
                    startActivity(new Intent(YiJianActivity.this, ServiceActivity.class));
                } else if (name_edi.length() != 0 && neirong_edi.length() != 0 && phonenumber_edi.length() != 11) {
                    Toast.makeText(YiJianActivity.this, "请输入11位手机号码", Toast.LENGTH_LONG).show();
                } else if (name_edi.length() == 0 && neirong_edi.length() != 0 && phonenumber_edi.length() == 11) {
                    Toast.makeText(YiJianActivity.this, "请输入姓名", Toast.LENGTH_LONG).show();
                } else if (name_edi.length() != 0 && neirong_edi.length() == 0 && phonenumber_edi.length() == 11) {
                    Toast.makeText(YiJianActivity.this, "请输入意见内容", Toast.LENGTH_LONG).show();
                }else if(name_edi.length() != 0 && neirong_edi.length() == 0 && phonenumber_edi.length() != 11){
                    Toast.makeText(YiJianActivity.this, "请输入意见内容", Toast.LENGTH_LONG).show();
                }

            }
        });



    }
}
