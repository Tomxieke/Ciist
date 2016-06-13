package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.util.SharedPreferenceHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

public class SignActivity extends AppCompatActivity {

    private Context mcontext;


    //网络请求地址
    private String GetInfoPre = "http://211.149.212.154:2015/apps/";
    private static final String APPLICATION_JSON = "application/json";
    //  public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private SharedPreferenceHelper sharedPreferenceHelper;

    private CiistTitleView sign_title;
    private EditText sign_zhanghao;
    private EditText sign_password;
    private TextView sign_forgetpassword;
    private TextView sign_new_user;
    private Button sign_button;

    private static final int MSG_FAIL = 101;//登录失败
    private static final int MSG_SUCCESS = 104;//登录成功


    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAIL:
                    Toast.makeText(SignActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                    break;
                case MSG_SUCCESS:

                    startActivity(new Intent(SignActivity.this, ServiceActivity.class));
                    SignActivity.this.finish();

                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mcontext = getApplicationContext();
        initview();
    }

    private void initview() {
        sign_title = (CiistTitleView) findViewById(R.id.sign_titleview);
        sign_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sign_zhanghao = (EditText) findViewById(R.id.sign_zhanghu);
        sign_password = (EditText) findViewById(R.id.sign_password);
        sign_forgetpassword = (TextView) findViewById(R.id.sign_forget_password);
        sign_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this,ForgrtPasswordActivity.class));
            }
        });
        sign_new_user = (TextView) findViewById(R.id.sign_new_user);
        sign_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this,RegisterActivity.class));
            }
        });
        sign_button = (Button) findViewById(R.id.sign_button);
        sign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign_zhanghao.length() == 0 && sign_password.length() == 0) {
                    Toast.makeText(SignActivity.this, "请输入账户和密码", Toast.LENGTH_LONG).show();
                } else if (sign_zhanghao.length() != 0 && sign_password.length() == 0) {
                    Toast.makeText(SignActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                } else if (sign_zhanghao.length() == 0 && sign_password.length() != 0) {
                    Toast.makeText(SignActivity.this, "请输入账户", Toast.LENGTH_LONG).show();
                } else if (sign_zhanghao.length() != 0 && sign_password.length() != 0) {
                        new signThread().start();

                }
            }
        });








    }

    /**
     * 用户登录
     */
    private class signThread extends Thread{
        String resultStr = ""; //上传是否成功返回的结果
        @Override
        public void run() {
            HttpClient httpClient = null;
            //    ResultInfo result = new ResultInfo();
            String _url = GetInfoPre + "ca/checkin";  //上传地址

            Log.e("test", "-用户登录-_url---" + _url);
            //  RequestHeader _header, string phone,string validcode,string pwd,string nickname
            HttpPost _post = new HttpPost(_url);
            _post.setHeader("Accept", APPLICATION_JSON);
            _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SKEY","ciistkey");
                jsonObject.put("SelfIDS","123456");
                jsonObject.put("PageIndex",1);
                jsonObject.put("PageSize",2);
                jsonObject.put("Identify","99876");


                JSONObject sendObj = new JSONObject();
                sendObj.put("_header",jsonObject);
                sendObj.put("phone",sign_zhanghao.getText().toString());
                sendObj.put("pwd",sign_password.getText().toString());
                Log.e("test",sendObj.toString());

                StringEntity entity = new StringEntity(sendObj.toString(), HTTP.UTF_8);//需要设置成utf-8否则汉字乱码
                entity.setContentType(CONTENT_TYPE_TEXT_JSON);
                entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
                _post.setEntity(entity);
                // 向WCF服务发送请求

                httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(_post);
                // 判断是否成功,返回的为OK则成功
                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                    sharedPreferenceHelper=SharedPreferenceHelper.getInstance(mcontext);
                    sharedPreferenceHelper.addPassport("_passport",resultStr);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (httpClient != null){
                    httpClient.getConnectionManager().shutdown();  //关闭连接
                }
            }
            if(resultStr==null||resultStr.equals("")){
                Log.e("test","-用户登录-result--______________-"+resultStr);
                mhandler.sendEmptyMessage(MSG_FAIL);


            }else{
                mhandler.sendEmptyMessage(MSG_SUCCESS);
            }
            Log.e("test","-用户登录-result---"+resultStr+"请求结束");

            //    Toast.makeText(MainActivity.this,"请求结束",Toast.LENGTH_SHORT).show();
           /* if (resultStr.substring(1,3).equalsIgnoreCase("OK")){
                mHandler.obtainMessage(POST_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(POST_FAILURE,result).sendToTarget();
            }*/

//            ServiceActivity s = new ServiceActivity();
//            s.setIndetify(resultStr);

        }
    }



}
