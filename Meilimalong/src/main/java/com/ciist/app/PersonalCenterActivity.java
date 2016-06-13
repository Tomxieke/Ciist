package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.util.SharedPreferenceHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class PersonalCenterActivity extends AppCompatActivity {


    private Context mcontext;

    //网络请求地址
    private String GetInfoPre = "http://211.149.212.154:2015/apps/";
    private static final String APPLICATION_JSON = "application/json";
    //  public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private CiistTitleView titleView;

    private TextView name;
    private LinearLayout shenqing_l;
    private LinearLayout yuyue_l;
    private LinearLayout zixun_l;
    private LinearLayout yijian_l;
    private LinearLayout tousu_l;
    private LinearLayout shoucang_l;

    private Button yibanjie;
    private Button daibanli;
    private Button zhuxiao;

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        mcontext = getApplicationContext();
        initvie();


        new usernameThread().start();
    }

    private void initvie() {

        shenqing_l=(LinearLayout)findViewById(R.id.personal_c_shenqing_lin);

        yuyue_l=(LinearLayout)findViewById(R.id.personal_c_yuyue_lin);

        zixun_l=(LinearLayout)findViewById(R.id.personal_c_zixun_lin);

        yijian_l=(LinearLayout)findViewById(R.id.personal_c_yijian_lin);

        tousu_l=(LinearLayout)findViewById(R.id.personal_c_tousu_lin);

        shoucang_l=(LinearLayout)findViewById(R.id.personal_c_shoucang_lin);

        titleView=(CiistTitleView)findViewById(R.id.personal_c_titleview);
        titleView.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalCenterActivity.this, SettingActivity.class));
            }
        });

        yibanjie=(Button)findViewById(R.id.personal_c_yibanjie_button);

        daibanli=(Button)findViewById(R.id.personal_c_daibanli_button);

        zhuxiao=(Button)findViewById(R.id.personal_c_zhuxiao_button);
        zhuxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "";
                sharedPreferenceHelper.addPassport("_passport", s);
                Toast.makeText(PersonalCenterActivity.this, "注销成功，请重新登录", Toast.LENGTH_LONG).show();
                startActivity(new Intent(PersonalCenterActivity.this, ServiceActivity.class));
            }
        });
        name = (TextView) findViewById(R.id.personal_c_user_tv);
        sharedPreferenceHelper= SharedPreferenceHelper.getInstance(this);
        name.setText(getPasspotIdentif(sharedPreferenceHelper.getPassport("_passport")));

    }


    /**
     * 用户名
     */
    private class usernameThread extends Thread {

        String resultStr = ""; //上传是否成功返回的结果


        @Override
        public void run() {
            HttpClient client = null;
            //    ResultInfo result = new ResultInfo();
            String _url = GetInfoPre + "ca/reg";
            Log.e("test", "-用户登录-_url---" + _url);
            //  RequestHeader _header, string phone,string validcode,string pwd,string nickname
            client=new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
//            _post.setHeader("Accept", APPLICATION_JSON);
//            _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
//            JSONObject jsonObject = new JSONObject();
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

               Log.e("test", "-用户登录-result---" + resultStr + "请求结束");

        }
    }



    private String getPasspotIdentif(String s) {
        String identify = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            identify = jsonObject.getString("user_zh");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return identify;

    }

}
