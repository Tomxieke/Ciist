package com.ciist.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.AsyncImageLoader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AppErweimaCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_erweima_code);
        //何旺 2016-1-6
        initViews();
        loadImages();

    }

    //何旺 2016-1-6
    private ImageView erweimaCodeIv;
    private String imgPath;//图片地址
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AsyncImageLoader imageLoader = new AsyncImageLoader("meilimalongerweima");
            imageLoader.LoadImage(imgPath,erweimaCodeIv);
        }
    };

    //初始化view
    private void initViews() {
        erweimaCodeIv = (ImageView) findViewById(R.id.erweimaCodeIv);
    }

    //获取图片资源url
    private void loadImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = "";
                String urlPath = ServerInfo.GetInfoPre + "7E48604A-FCF9-4D98-9F1E-8B931D527E79/1/1";
                HttpClient clent = new DefaultHttpClient();
                HttpGet get = new HttpGet(urlPath);
                try {
                    HttpResponse response = clent.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        jsonStr = EntityUtils.toString(entity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clent.getConnectionManager().shutdown();
                }
                if (jsonStr == null) {
                    return;
                }
                try {
                    JSONArray ja = new JSONArray(jsonStr);
                    if (ja == null || ja.length() <= 0) {
                        return;
                    }
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        imgPath = ServerInfo.ServerRoot + jo.getString("Image");
                        //Log.i("TAG", imgPath + ".....+++++++++++++++++........");
                        //imgUrls.add(imgPath);
                    }
                    handler.sendMessage(new Message());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
