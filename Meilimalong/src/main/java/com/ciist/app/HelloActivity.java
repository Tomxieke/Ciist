package com.ciist.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciist.entities.ServerInfo;
import com.ciist.services.CiistService;
import com.ciist.toolkits.AsyncImageLoader;
import com.umeng.socialize.PlatformConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class HelloActivity extends Activity {

    private boolean ISLOADED = false;
    private Timer tmpTimer = new Timer(true);
    private String AdImageUrl = "";
    private String CurrentFloderName="ad";
    TextView ktv;
    int adcount = 4;
    //何旺1026-1-6
    private ImageView kiv;
    private String imgPath;//图片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        SharedPreferences mySharedPreferences = getSharedPreferences("CIIST", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("SENDMSG", false);
        editor.commit();
        RegShareInfo();
        kiv = (ImageView) findViewById(R.id.adImgView);//何旺2016-1-6
        ktv = (TextView) findViewById(R.id.copyrightTxtView);
        CheckPrepareSystem();
        loadImages();//何旺 2016-1-6
        LoadLastAd();
        final Handler timerhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ktv.setText(String.valueOf(adcount));
                        break;
                }
                super.handleMessage(msg);
            }
        };
        final TimerTask tmpTask = new TimerTask() {
            @Override
            public void run() {
                adcount--;
                Message message = new Message();
                message.what = 1;
                timerhandler.sendMessage(message);
                if (adcount <= 0) {
                    tmpTimer.cancel();
                    ISLOADED = true;
                    LoadMain();
                }
            }
        };
        tmpTimer.schedule(tmpTask, 1000, 1000);
        try {
            Intent _it = new Intent(HelloActivity.this, CiistService.class);
            startService(_it);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void LoadLastAd() {
        try {
            if (AdImageUrl == "") return;
            AsyncImageLoader asyImg = new AsyncImageLoader(CurrentFloderName);
            asyImg.LoadImage(AdImageUrl, (ImageView) findViewById(R.id.adImgView));
        } catch (Exception e) {
        }
    }
void RegShareInfo(){
    PlatformConfig.setWeixin("wxd6b81ed1a599125a", "00a966be987fcac69f65e726c354208c");
    //微信 appid appsecret
    PlatformConfig.setQQZone("1105045478", "sxRXAyoiw8yc2zOW");
    // QQ和Qzone appid appkey
//    PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");
//    //新浪微博 appkey appsecret
//    PlatformConfig.setAlipay("2015111700822536");
//    //支付宝 appid
//    PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//    //易信 appkey
//    PlatformConfig.setTwitter("3aIN7fuF685MuZ7jtXkQxalyi", "MK6FEYG63eWcpDFgRYw4w9puJhzDl0tyuqWjZ3M7XJuuG7mMbO");
//    //Twitter appid appkey
//    PlatformConfig.setPinterest("1439206");
//    //Pinterest appid
//    PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
//    //来往 appid appkey
}
    void LoadMain() {
        if (ISLOADED) {
            Intent tmpIntent = new Intent(HelloActivity.this, IndexOfStyle2Activity2.class);
            startActivity(tmpIntent);
            HelloActivity.this.finish();
        }
    }

    void CheckPrepareSystem() {
        String pathRoot = Environment.getExternalStorageDirectory()
                .getPath() + "/ciist";
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            File maiduo = new File(pathRoot);
            if (!maiduo.exists()) {
                maiduo.mkdir();
            }
        }
    }

    //何旺 2016-1-6
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AsyncImageLoader imageLoader = new AsyncImageLoader("meilimalongerweima");
            imageLoader.LoadImage(imgPath, kiv);
        }
    };

    //获取图片资源url
    private void loadImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = "";
                String urlPath = ServerInfo.GetInfoPre + "993F3195-85E5-4E91-90F0-603F4DA728B7/1/1";
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
