package com.ciist.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ciist.app.IndexOfStyle2Activity2;
import com.ciist.app.R;
import com.ciist.entities.ServerInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CiistService extends Service {
    String mIdentify;
    String mWhois;
    private Timer tmpTimer;
    boolean isSendMsg = false;
    int msgCount = 0;
    Notification notify2 = null;
    NotificationManager manager = null;


    public CiistService() {
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        try {


            final Handler timerhandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            if (mIdentify != null && mIdentify != "" && msgCount > 0) {
                                try {
                                    if (manager == null) {
                                        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    }
                                    // 下面需兼容Android 2.x版本是的处理方式
                                    // Notification notify1 = new Notification(R.drawable.message,
                                    // "TickerText:" + "您有新短消息，请注意查收！", System.currentTimeMillis());
//                                    Notification notify1 = new Notification();
//                                    notify1.icon = R.drawable.ic_launcher;
//                                    notify1.tickerText = mWhois + "您好，您有新的消息，请及时前往魅力马龙进行处理！";
//                                    //notify1.when = System.currentTimeMillis();
//                                    // notify1.number = 1;
//                                    notify1.flags = Notification.FLAG_NO_CLEAR; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//                                    // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示
//                                    notify1.defaults = Notification.DEFAULT_SOUND;
                                    PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0,
                                            new Intent(getApplicationContext(), IndexOfStyle2Activity2.class), 0);
                                    String NOTIFICATION_DELETED_ACTION = "com.android.mms.NOTIFICATION_DELETED_ACTION";
                                    if (notify2 == null) {
                                        notify2 = new Notification.Builder(getApplicationContext())
                                                .setSmallIcon(R.drawable.ciist_icon_translate48)
                                                .setTicker("魅力马龙发来新消息，请及时前往处理！")// 设置在status
                                                        // bar上显示的提示文字
                                                .setContentTitle("魅力马龙")// 设置在下拉status
                                                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                                                .setContentText(mWhois + "您好:" + "魅力马龙提醒您，您有新的工作信息，请及时前往魅力马龙APP中进行处理！")// TextView中显示的详细内容
                                                .setContentIntent(pendingIntent2) // 关联PendingIntent
//                                                .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                                                .getNotification(); // 需要注意build()是在API level
                                        // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                                        notify2.flags = Notification.FLAG_AUTO_CANCEL;
                                        notify2.defaults = Notification.DEFAULT_SOUND;
                                    }
//                                    isBusy = false;
                                    notify2.number = 1;
                                    manager.notify(1979, notify2);
                                    SharedPreferences mySharedPreferences = getSharedPreferences("CIIST", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                                    editor.putBoolean("SENDMSG", true);
                                    editor.commit();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
//                            isBusy=false;
                            break;
                    }
                    super.handleMessage(msg);
                }
            };
            final TimerTask tmpTask = new TimerTask() {
                @Override
                public void run() {
//                    if(notify2!=null){
//                        System.out.println("****************** number ******************="+notify2.number);
//                    }else{
//                        System.out.println("*************** notify is null");
//                    }

                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    if(hour<7){
                        return;
                    }
                    SharedPreferences sharedPreferences0 = getSharedPreferences("CIIST", Context.MODE_PRIVATE);
                    isSendMsg = sharedPreferences0.getBoolean("SENDMSG", false);

                    if (1==1) {    // if (isSendMsg==false) {
//TODO

                        SharedPreferences sharedPreferences = getSharedPreferences("passport", Context.MODE_PRIVATE);
                        mIdentify = sharedPreferences.getString("identify", "");
                        mWhois = sharedPreferences.getString("whois", "");

//                http get begin
                        String _url = ServerInfo.ServerBKRoot + "/info/getNoticeToMe/ciistkey/" + mIdentify;
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet(_url);
                        JSONObject json = null;
                        String jsonString = "";
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
//                http get end
//                        parse result
                        if (jsonString != null) {
                            try {
                                if(jsonString.length()>0)
                                msgCount = Integer.parseInt(jsonString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        parse result end
                        Message message = new Message();
                        message.what = 1;
                        timerhandler.sendMessage(message);
                    }
                }
            };
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (tmpTimer == null) {
                        tmpTimer = new Timer(true);
                    }
                    tmpTimer.schedule(tmpTask, 1000, 60000);
                }
            }.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
