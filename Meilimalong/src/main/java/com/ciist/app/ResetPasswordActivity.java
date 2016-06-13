package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.util.SharedPreferenceHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ResetPasswordActivity extends AppCompatActivity {
    private Context mcontext;

    //网络请求地址
    private String GetInfoPre = "http://211.149.212.154:2015/apps/";
    private static final String APPLICATION_JSON = "application/json";
    //  public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private SharedPreferenceHelper sharedPreferenceHelper;

    private CiistTitleView titleView;
    private EditText password;
    private EditText password_sure;
    private EditText password_old;
    private Button next;

    private static final int MSG_FAIL = 101;//失败
    private static final int MSG_SUCCESS = 102;//成功

    Handler mhandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FAIL:
                    Toast.makeText(ResetPasswordActivity.this,"修改失败，请检查旧密码",Toast.LENGTH_LONG).show();
                    break;
                case MSG_SUCCESS:
                    startActivity(new Intent(ResetPasswordActivity.this,ResetSuccessActivity.class));
                    ResetPasswordActivity.this.finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initview();
        mcontext = getApplicationContext();
        sharedPreferenceHelper = SharedPreferenceHelper.getInstance(this);
    }

    private void initview() {

        password_old = (EditText) findViewById(R.id.rpass_old_password);
        password = (EditText) findViewById(R.id.rpass_password);
        password_sure = (EditText) findViewById(R.id.rpass_password_sure);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (password.hasFocus() == false) {
                    String passwordsa = password.getText().toString();
                    if (passwordsa.length() < 6) {
                        Toast.makeText(ResetPasswordActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
                    } else if (passwordsa.length() > 20) {
                        Toast.makeText(ResetPasswordActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        password_sure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (password_sure.hasFocus() == false) {
                    String passwords = password.getText().toString();
                    String confirmpassword = password_sure.getText().toString();
                    if (passwords.equals(confirmpassword)) {
                        password_sure.setTextColor(Color.GREEN);
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                        password_sure.setTextColor(Color.RED);
                    }

                } else {
                    password_sure.setTextColor(Color.BLACK);

                }
            }
        });

        next = (Button) findViewById(R.id.rpass_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new xiugaiThread().start();
            }
        });

        titleView = (CiistTitleView) findViewById(R.id.rpass_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * 修改密码
     */
    private class xiugaiThread extends Thread {
        String resultStr = ""; //上传是否成功返回的结果

        @Override
        public void run() {
            HttpClient client = null;
            String _url = GetInfoPre + "ca/ChangePassword/ciistkey/" +
                    getPasspotIdentif(sharedPreferenceHelper.getPassport("_passport")) +"/"+
                    password_old.getText().toString()+"/" + password.getText().toString();  //上传地址

            Log.e("test", "-用户注册-_url---" + _url);
            client=new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    resultStr = EntityUtils.toString(entity);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
            if(resultStr.equals("-9")){
                mhandler.sendEmptyMessage(MSG_FAIL);
            }else {
                mhandler.sendEmptyMessage(MSG_SUCCESS);
            }



            Log.e("test", "-用户注册-result---" + resultStr + "请求结束");

        }
    }


    private String getPasspotIdentif(String s) {
        String identify = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            identify = jsonObject.getString("identify");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return identify;

    }


}
