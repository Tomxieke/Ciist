package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;

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

public class RegisterActivity extends AppCompatActivity {

    private Context mcontext;

    //网络请求地址
    private String GetInfoPre = "http://211.149.212.154:2015/apps/";
    private static final String APPLICATION_JSON = "application/json";
    //  public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";


    private CiistTitleView titleView;
    private EditText name;
    private EditText password;
    private EditText password_sure;
    private EditText phonenumber;
    private EditText phonenumber_yanzheng;
    private Button phonenumber_get;
    private Button register;

    private static final int MSG_FAIL = 101;//注册失败
    private static final int MSG_YIJING = 102;//已经注册
    private static final int MSG_WUXIAO = 103;//验证码无效
    private static final int MSG_SUCCESS = 104;//注册成功


    Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case MSG_FAIL:
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_LONG).show();
                   break;
               case MSG_YIJING:
                   Toast.makeText(RegisterActivity.this,"已经注册",Toast.LENGTH_LONG).show();
                   break;
               case MSG_WUXIAO:
                   Toast.makeText(RegisterActivity.this,"验证码无效",Toast.LENGTH_LONG).show();
                   break;
               case MSG_SUCCESS:
                   Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                   startActivity(new Intent(RegisterActivity.this,SignActivity.class));
                   break;

           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mcontext = getApplicationContext();
        intiview();
    }

    private void intiview() {

        titleView=(CiistTitleView)findViewById(R.id.register_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name=(EditText)findViewById(R.id.register_name_edi);
        password=(EditText)findViewById(R.id.register_password_edi);
        password_sure=(EditText)findViewById(R.id.register_password_sure_edi);
        phonenumber=(EditText)findViewById(R.id.register_phonenumber_edi);
        phonenumber_yanzheng=(EditText)findViewById(R.id.register_phonenumber_yanzheng_edi);

        phonenumber_get=(Button)findViewById(R.id.register_phonenumber_get_button);
        phonenumber_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartThread().start();
            }
        });



        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(password.hasFocus()==false){
                    String passwordsa=password.getText().toString();
                    if(passwordsa.length()<6){
                        Toast.makeText(RegisterActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
                    }else if(passwordsa.length()>20){
                        Toast.makeText(RegisterActivity.this, "请输入6-20个字符", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                        password_sure.setTextColor(Color.RED);
                    }

                } else {
                    password_sure.setTextColor(Color.BLACK);

                }
            }
        });

        register=(Button)findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RgThread().start();
            }
        });


    }

    //短信验证码。
    private class StartThread extends Thread{
        @Override
        public void run() {
            String jsonString = "";

            // String _url = "http://api.k780.com:88/?app=weather.today&weaid=101290405&appkey=17417&sign=e203438d9686d936b908e0c8c3a8fc6e&format=json";
            String _url = GetInfoPre+"ca/SMS/ciistkey/"+phonenumber.getText().toString()+"/"+getDeviceId();
            Log.d("test", "-----url------" + _url);
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    jsonString = EntityUtils.toString(entity);
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

        }
    }
    /**
     * 用户注册
     */
    private class RgThread extends Thread{
        String resultStr = ""; //上传是否成功返回的结果
        @Override
        public void run() {
            HttpClient httpClient = null;
            //    ResultInfo result = new ResultInfo();
            String _url = GetInfoPre + "ca/reg";  //上传地址

            Log.e("test", "-用户注册-_url---" + _url);
            //    Log.e("test","+_url++"+_url);
            //    Log.e("test","--mSelfids--"+mSelfids+"--mIdentify--"+mIdentify);
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
                sendObj.put("phone",phonenumber.getText().toString());
                sendObj.put("validcode",phonenumber_yanzheng.getText().toString());
                sendObj.put("pwd",password.getText().toString());
                sendObj.put("nickname",name.getText().toString());
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
            if(resultStr.equals("-9")){
             mhandler.sendEmptyMessage(MSG_FAIL);
            }else if(resultStr.equals("-7")){
                mhandler.sendEmptyMessage(MSG_WUXIAO);
            }else if(resultStr.equals("-8")){
                mhandler.sendEmptyMessage(MSG_YIJING);

            }else if(resultStr.equals("1")){
                mhandler.sendEmptyMessage(MSG_SUCCESS);
            }
            Log.e("test","-用户注册-result---"+resultStr+"请求结束");

            //    Toast.makeText(MainActivity.this,"请求结束",Toast.LENGTH_SHORT).show();
           /* if (resultStr.substring(1,3).equalsIgnoreCase("OK")){
                mHandler.obtainMessage(POST_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(POST_FAILURE,result).sendToTarget();
            }*/

        }
    }


    private String getDeviceId(){
        //获取Device_id
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;

    }

}
