package com.ciist.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.ciist.beans.ResultBeans;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.entities.SubjectsInfo;
import com.ciist.entities.WeatherDayInfo;
import com.ciist.toolkits.CiistAdapter2;
import com.ciist.util.NetUtil;
import com.ciist.util.RefreshableView;
import com.hw.ciist.util.Hutils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.scxh.slider.library.SliderLayout;
import com.scxh.slider.library.SliderTypes.BaseSliderView;
import com.scxh.slider.library.SliderTypes.TextSliderView;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

//import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
//import com.baidu.autoupdatesdk.UICheckUpdateCallback;

//import com.umeng.update.UmengUpdateAgent;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class IndexOfStyle2Activity2 extends AppCompatActivity implements AdapterView.OnItemClickListener, RefreshableView.RefreshListener, SearchActivity.ItemClickedListener {

    //系统级
    private Context mContext;
    private String mUsername = "";
    private String mIdentify = "";
    private String mSelfids = "";
    private String mDuties = "";

    //handler常量
    private static final int MSG_wicon = 0;//weather image
    private static final int MSG_wdesc = 1;//weather desc
    private static final int MSG_temp = 2;//weather tempratrue
    private static final int MSG_direct = 3;//wind direct
    private static final int MSG_wind = 4;//wind power
    private static final int MSG_days = 5;//
    private static final int MSG_week = 6;//
    private static final int MSG_humidity = 7;//
    private static final int MSG_obj = 8;//
    private static final int MSG_OA_OK = 100;
    private static final int MSG_OA_NO = 101;
    private static final int MSG_RE_OK = 110;
    private static final int MSG_GEYDATASUCCESS = 2222;

    //自定义常量
    private String WeatherFloderName = "weather";

    //自定义变量
    private ResultBeans resultBeans;
    private static Boolean isExit = false;
    private ArrayList<ItemInfo> DataOfHotNews = new ArrayList<ItemInfo>();  //热点资讯数据集合
    private ArrayList<ItemInfo> DataOfScrollImageNews = new ArrayList<ItemInfo>();//滚动图片新闻数据集合
    private boolean LoadDataBusyFlag_Weather = false;
    private boolean LoadDataBusyFlag_HotNews = false;
    private boolean LoadDataBusyFlag_ScrollImageNews = false;
    public String status1 = "晴";
    public String status2 = "晴转";
    public String status3 = "雨夹雪";
    public String status4 = "雷电";
    public String status5 = "阴";
    public String status6 = "雪";
    public String status7 = "雨";
    public String status8 = "多云";

    //系统控件级
    private ImageView weather_image;
    private TextView w_desc;
    private TextView weather_qing;
    private TextView weather_wendu;
    private TextView weather_shidu;
    private TextView weather_fengxiang;
    private TextView weather_fengli;
    private TextView shownum, showtitle;
    private ScrollView indexOfStyle2Activity2Sv;
    private ProgressBar waitSliderPb;  //滚动资讯加载进度

    //自定义控件
    private RefreshableView home_refre;
    //handler
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_obj:
                    //
                    WeatherDayInfo _weatherinfo = (WeatherDayInfo) msg.obj;
                    if (_weatherinfo != null) {
//                       if (_weatherinfo.get_weatherImageUrl() != null && _weatherinfo.get_weatherImageUrl() != "") {
//                           AsyncImageLoader asyImg = new AsyncImageLoader(WeatherFloderName);
//                           asyImg.LoadImage(_weatherinfo.get_weatherImageUrl(), weather_image);
//                       }
//                       w_desc.setBackgroundResource(R.drawable.ciist_home_icon_weather);
                        weather_qing.setText(_weatherinfo.get_weatherDescription());
                        weather_wendu.setText(_weatherinfo.get_temperature());
                        weather_fengli.setText(_weatherinfo.get_windp());
                        weather_fengxiang.setText(_weatherinfo.get_wind());
                        weather_shidu.setText(_weatherinfo.get_relativeHumidity());
                        String weatherim = _weatherinfo.get_weatherDescription();
                        if (weatherim.contains(status1)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_2);
                        } else if (weatherim.contains(status2)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_1);
                        } else if (weatherim.contains(status3)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_4);
                        } else if (weatherim.contains(status6)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_4);
                        } else if (weatherim.contains(status7)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_3);
                        } else if (weatherim.contains(status4)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_5);
                        } else if (weatherim.contains(status5)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_6);
                        } else if (weatherim.contains(status8)) {
                            weather_image.setBackgroundResource(R.drawable.ciist_home_icon_weather_1);
                        }
                    }

                case MSG_wdesc:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                        w_desc.setText(_tmp);
                    }
                    break;
                case MSG_temp:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                        weather_wendu.setText(_tmp);
                    }
                    break;
                case MSG_direct:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                        weather_fengxiang.setText(_tmp);
                    }
                    break;
                case MSG_days:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                    }
                    break;

                case MSG_wind:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                        weather_fengli.setText(_tmp);
                    }
                    break;
                case MSG_humidity:
                    if (msg.obj != null && msg.obj.toString() != "" && w_desc != null) {
                        String _tmp = msg.obj.toString();
                        weather_shidu.setText(_tmp);
                    }
                    break;
                case MSG_OA_OK:
//                    Intent tmpIntentYES = new Intent(IndexOfStyle2Activity.this, IndexOfOAMainActivity.class);
                    Intent tmpIntentYES = new Intent(IndexOfStyle2Activity2.this, NewOfficeHomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subjectname", "办公管理系统");
                    bundle.putString("subjectcode", "OAFUNCS");
                    bundle.putString("identify", mIdentify);
                    bundle.putString("username", mUsername);
                    bundle.putString("selfids", mSelfids);
                    bundle.putString("Duties", mDuties);
                    bundle.putBoolean("full", true);
                    tmpIntentYES.putExtras(bundle);
                    startActivity(tmpIntentYES);
                    break;
                case MSG_OA_NO:
                    Intent tmpIntentNO = new Intent(IndexOfStyle2Activity2.this, OALoginActivity.class);
                    startActivity(tmpIntentNO);
                    break;

                case MSG_GEYDATASUCCESS:
                    //hwLvAdapter = new HwLvAdapter();
                    //btnStyle2IndexTodayNews.setAdapter(hwLvAdapter);
                    ciistAdapter = new CiistAdapter2(IndexOfStyle2Activity2.this,DataOfHotNews);
                    btnStyle2IndexTodayNews.setAdapter(ciistAdapter);
                    indexOfStyle2Activity2Sv.smoothScrollTo(0, 0);
                    break;

                case MSG_RE_OK:
                    home_refre.finishRefresh();
                    break;

            }
        }
    };


    //---------------------------------业务数据交互方法----------begin--------------------------------
    // 消息提醒配置
    void NotificationConfig() {
        SharedPreferences mySharedPreferences = getSharedPreferences("CIIST", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("SENDMSG", false);
        editor.commit();
    }

    private List<SubjectsInfo> getSubjects(int fcode) {

        List<SubjectsInfo> _subs = new ArrayList<SubjectsInfo>();
        if (fcode == 0) {
            _subs.add(new SubjectsInfo("今日马龙", "AA153C94-AA8F-4A7A-AB60-5409BCED15C3"));
            _subs.add(new SubjectsInfo("通知公告", "9007B6C1-9AE1-4338-83F2-1A82A8B4A279"));
//            _subs.add(new SubjectsInfo("部门动态", "4131F48F-EBDB-497F-B697-3059E4F3C1C3"));
//            _subs.add(new SubjectsInfo("乡镇动态", "A8F167D0-E227-482C-ACD0-F79DE1C5C999"));
//            _subs.add(new SubjectsInfo("工业交通", "F6BD63B1-AB8C-499D-BD96-1B1C11C6229F"));
//            _subs.add(new SubjectsInfo("统计信息", "5E64DE7D-74F9-4FE0-BF37-0ED69FAEE5DF"));
//            _subs.add(new SubjectsInfo("农业农村", "3D324209-BAA3-401A-AF6A-1D30277E52AF"));
//            _subs.add(new SubjectsInfo("社会公益", "863F1644-41EF-4EFF-89B9-95783CEA5BD2"));
//            _subs.add(new SubjectsInfo("人事任免", "33A5FEBF-A910-467D-B3D7-8C1EA5EC03CD"));
//            _subs.add(new SubjectsInfo("招考信息", "0E84432C-7199-43B3-935F-9A38703F46BD"));
        }
        if (fcode == 2) {
            _subs.add(new SubjectsInfo("旅游频道", "5B35CC31-8C58-461B-A7EB-80613C28E021"));
        }
        return _subs;

    }

    //获取天气预报信息
    void GetWeatherInfo() {
        LoadDataBusyFlag_Weather = true;
        new InternetOfWeatherInfoThread().start();
    }

    @Override
    public void onItemClickedListener(Hutils.Ciist_entity info) {
    //TODO
    }

    /**
     * 获取天气信息
     */
    private class InternetOfWeatherInfoThread extends Thread {
        public void run() {
            try {
//                String _url = "http://api.k780.com:88/?app=weather.today&weaid=101290405&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml";
//                String RootNode = "result";
//                Document doc;
//                doc = Jsoup.connect(_url).timeout(9000).get();
//                Document content = Jsoup.parse(doc.toString());
//                if (!content.toString().contains("马龙")) {
//                    return;
//                }
//                Elements divs = content.select(RootNode);
//                for (Element links : divs) {
//                    String _days = links.select("days").text();
//                    mHandler.obtainMessage(MSG_days, _days).sendToTarget();
//                    String _week = links.select("week").text();
//                    mHandler.obtainMessage(MSG_week, _week).sendToTarget();
//                    String _temperature = links.select("temperature").text();
//                    mHandler.obtainMessage(MSG_temp, _temperature).sendToTarget();
//                    String _humidity = links.select("humidity").text();
//                    mHandler.obtainMessage(MSG_humidity, _humidity).sendToTarget();
//                    String _weather = links.select("weather").text();
//                    mHandler.obtainMessage(MSG_wdesc, _weather).sendToTarget();
//                    String _weather_icon = links.select("weather_icon").text();
//                    mHandler.obtainMessage(MSG_wicon, _weather_icon).sendToTarget();
//                    String _wind = links.select("wind").text();
//                    mHandler.obtainMessage(MSG_direct, _wind).sendToTarget();
//                    String _winp = links.select("winp").text();
//                    mHandler.obtainMessage(MSG_wind, _winp).sendToTarget();
//                }
                String _url = "http://api.k780.com:88/?app=weather.today&weaid=101290405&appkey=17417&sign=e203438d9686d936b908e0c8c3a8fc6e&format=json";
                //json
                String jsonString = "";
                URL url = new URL(_url);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] datacache = new byte[1024];
                int len = 0;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                while ((len = is.read(datacache)) != -1) {
                    os.write(datacache, 0, len);
                }
                is.close();
                jsonString = new String(os.toByteArray());
                conn.disconnect();
                if (jsonString != "") {
                    JSONObject _obj = new JSONObject(jsonString);
                    if (_obj != null) {
                        JSONObject jsonObj = _obj.getJSONObject("result");
                        if (jsonObj != null) {
                            WeatherDayInfo _weather = new WeatherDayInfo();
                            _weather.set_date(jsonObj.getString("days"));
                            _weather.set_month(jsonObj.getString("week"));
                            _weather.set_relativeHumidity(jsonObj.getString("humidity"));
                            _weather.set_temperature(jsonObj.getString("temperature"));
                            _weather.set_weatherDescription(jsonObj.getString("weather"));
                            _weather.set_weatherImageUrl(jsonObj.getString("weather_icon"));
                            _weather.set_wind(jsonObj.getString("wind"));
                            _weather.set_windp(jsonObj.getString("winp"));
                            LoadDataBusyFlag_Weather = false;
                            isRefreashCompleted();
                            mHandler.obtainMessage(MSG_obj, _weather).sendToTarget();
                        }
                    }
                }

            } catch (Exception ex) {
                LoadDataBusyFlag_Weather = false;
                isRefreashCompleted();
            }
        }
    }

    /**
     * 获取滚动资讯数据Tom
     */
    private void getDataOfScrollImageNews() {
        LoadDataBusyFlag_ScrollImageNews = true;
        String urlPath = ServerInfo.GetInfoPre + "E1AC514F-0972-4647-BD97-20C5090F2704/1/10";  //获取地址
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlPath, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(IndexOfStyle2Activity2.this, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> listDate = new ArrayList<>();
               /* if(listDate != null||listDate.size()>0){
                    listDate.clear();
                }*/
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        return;
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(j);
                        String _Title = jsonObj.getString("Title");
                        if (_Title == "") continue;

                        String link = ServerInfo.InfoDetailPath;
                        String _linkUrl = link + jsonObj.getString("Info_Ids");
                        String _linkDate = jsonObj.getString("pubDate");
                        String _source = jsonObj.getString("Sourse");
                        String _author = jsonObj.getString("Author");
                        String _image = ServerInfo.ServerRoot + jsonObj.getString("Image");
                        String _type = jsonObj.getString("Type");
                        String _bak1 = jsonObj.getString("Remark1");
                        String _bak2 = jsonObj.getString("Remark2");
                        String _bak3 = jsonObj.getString("Remark3");
                        String _bak4 = jsonObj.getString("Remark4");
                        String _bak5 = jsonObj.getString("Remark5");
                        String _telnum = jsonObj.getString("contracttel");
                        Double _longtidue = jsonObj.getDouble("longtidue");
                        Double _latidue = jsonObj.getDouble("latidue");
                        int _visitecounter = jsonObj.getInt("VisitCount");
                        ContentValues values = new ContentValues();
                        values.put("Title", _Title);
                        values.put("Url", _linkUrl);
                        ItemInfo tmpIteminfo = new ItemInfo();
                        tmpIteminfo.setTitle(_Title);
                        tmpIteminfo.setPubdate(_linkDate);
                        tmpIteminfo.setLinkurl(_linkUrl);
                        tmpIteminfo.setImgsrc(_image);
                        tmpIteminfo.setInfosource(_source);
                        tmpIteminfo.setInfoauthor(_author);
                        tmpIteminfo.setInfotype(_type);
                        tmpIteminfo.setBak1(_bak1);
                        tmpIteminfo.setBak2(_bak2);
                        tmpIteminfo.setBak3(_bak3);
                        tmpIteminfo.setBak4(_bak4);
                        tmpIteminfo.setBak5(_bak5);
                        tmpIteminfo.setSource(_source);
                        tmpIteminfo.setAuthor(_author);
                        tmpIteminfo.setVisitCount(_visitecounter);
                        tmpIteminfo.setTelnum(_telnum);
                        tmpIteminfo.setLatidue(_latidue);
                        tmpIteminfo.setLongtidue(_longtidue);
                        listDate.add(tmpIteminfo);
                    }
                    LoadDataBusyFlag_ScrollImageNews = false;
                    isRefreashCompleted();
                    getViewHeadOfScrollImageNews(listDate);
                    waitSliderPb.setVisibility(View.GONE);


                } catch (JSONException e) {
                    e.printStackTrace();
                    LoadDataBusyFlag_ScrollImageNews = false;
                    isRefreashCompleted();
                }

            }
        });
    }

    /**
     * 滚动资讯数据配置Tom
     */
    private void getViewHeadOfScrollImageNews(ArrayList<ItemInfo> list) {
        SliderLayout mSliderLayout = (SliderLayout) indexOfStyle2Activity2Sv.findViewById(R.id.image_slider_layout);
        int length = list.size();
        mSliderLayout.removeAllSliders(); //移除原有数据
        for (int i = 0; i < length; i++) {
            final ItemInfo info = list.get(i);
            TextSliderView sliderView = new TextSliderView(IndexOfStyle2Activity2.this);   //向SliderLayout中添加控件
            sliderView.image(list.get(i).getImgsrc());
            //   Log.i("test","++ title +++"+list.get(i).getTitle());
            sliderView.description(list.get(i).getTitle());
            sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent tmpIntent = new Intent(mContext, WebActivity.class);
                    tmpIntent.putExtra("URL", info.getLinkurl());
                    tmpIntent.putExtra("TITLE", info.getTitle());
                    tmpIntent.putExtra("PUBDATE", info.getPubdate());
                    tmpIntent.putExtra("source", info.getInfosource());
                    tmpIntent.putExtra("author", info.getInfoauthor());
                    tmpIntent.putExtra("infotype", info.getInfotype());
                    tmpIntent.putExtra("img_url", info.getImgsrc());

                    tmpIntent.putExtra("ROOT", "#Content");
                    tmpIntent.putExtra("blank", true);
                    tmpIntent.putExtra("telnum", info.getTelnum());
                    tmpIntent.putExtra("latidue_e", info.getLatidue());
                    tmpIntent.putExtra("longtidue_e", info.getLongtidue());
                    startActivity(tmpIntent);


                }
            });

            mSliderLayout.addSlider(sliderView);
        }

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);  //将小圆点设置到右下方
    }

    /**
     * 异步获取首页10条热点资讯
     */
    private void getDataOfHotNews() {
        LoadDataBusyFlag_HotNews = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                {
                    ResultInfo result = new ResultInfo();
                    String jsonString = "";
                    try {
                        List<ItemInfo> data = new ArrayList<ItemInfo>();
                        String _url = ServerInfo.ServerBKRoot + "/info/getHotData/ciistkey/1/" + "AA153C94-AA8F-4A7A-AB60-5409BCED15C3/1/10";
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet(_url);
                        JSONObject json = null;
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

                        if (jsonString == "") {
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(jsonString);
                        if (jsonArray == null || jsonArray.length() <= 0) {
                            return;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            String _Title = jsonObj.getString("Title");
                            if (_Title == "") continue;

                            String link = ServerInfo.InfoDetailPath;
                            String _linkUrl = link + jsonObj.getString("Info_Ids");
                            String _linkDate = jsonObj.getString("pubDate");
                            String _source = jsonObj.getString("Sourse");
                            String _author = jsonObj.getString("Author");
                            String _image = ServerInfo.ServerRoot + jsonObj.getString("Image");
                            String _type = jsonObj.getString("Type");
                            String _bak1 = jsonObj.getString("Remark1");
                            String _bak2 = jsonObj.getString("Remark2");
                            String _bak3 = jsonObj.getString("Remark3");
                            String _bak4 = jsonObj.getString("Remark4");
                            String _bak5 = jsonObj.getString("Remark5");
                            ContentValues values = new ContentValues();
                            values.put("Title", _Title);
                            values.put("Url", _linkUrl);
                            ItemInfo tmpIteminfo = new ItemInfo();
                            tmpIteminfo.setTitle(_Title);
                            tmpIteminfo.setPubdate(_linkDate);
                            tmpIteminfo.setLinkurl(_linkUrl);
                            tmpIteminfo.setImgsrc(_image);
                            tmpIteminfo.setInfosource(_source);
                            tmpIteminfo.setInfoauthor(_author);
                            tmpIteminfo.setInfotype(_type);
                            tmpIteminfo.setBak1(_bak1);
                            tmpIteminfo.setBak2(_bak2);
                            tmpIteminfo.setBak3(_bak3);
                            tmpIteminfo.setBak4(_bak4);
                            tmpIteminfo.setBak5(_bak5);
                            tmpIteminfo.setSource(_source);
                            tmpIteminfo.setAuthor(_author);
                            int _visitecounter = jsonObj.getInt("VisitCount");
                            tmpIteminfo.setVisitCount(_visitecounter);
//                            System.out.print(tmpIteminfo.getAuthor()+" ooooooooooooooooooo");
                            DataOfHotNews.add(tmpIteminfo);
                        }
                        LoadDataBusyFlag_HotNews = false;
                        isRefreashCompleted();
                        mHandler.sendEmptyMessage(MSG_GEYDATASUCCESS);
                    } catch (Exception e) {
                        LoadDataBusyFlag_HotNews = false;
                        isRefreashCompleted();
                    }
                }
                //DataOfHotNews.addAll(Hutils.parseJSONData(Hutils.fromNetgetData(ServerInfo.GetInfoPre + "AA153C94-AA8F-4A7A-AB60-5409BCED15C3/1/10"),null));
            }
        }).start();
    }

    @Override
    //热点资讯Item监听
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemInfo info = (ItemInfo) parent.getAdapter().getItem(position);
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(mContext, WebActivity.class);
            tmpIntent.putExtra("URL", info.getLinkurl());
            tmpIntent.putExtra("TITLE", info.getTitle());
            tmpIntent.putExtra("PUBDATE", info.getPubdate());
            tmpIntent.putExtra("source", info.getInfosource());
            tmpIntent.putExtra("img_url", info.getImgsrc());

            tmpIntent.putExtra("author", info.getInfoauthor());
            tmpIntent.putExtra("infotype", info.getInfotype());
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("blank", false);
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {
//                    Intent tmpIntent = new Intent(mContext, ChannelOfCoverStyleActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("subjectname", tmpobj.getTitle());
//                    bundle.putString("subjectcode", tmpobj.getBak5());
//                    tmpIntent.putExtras(bundle);
//                    startActivity(tmpIntent);
            } else {
                Intent tmpIntent = new Intent(mContext, ChannelOfCoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subjectname", info.getTitle());
                bundle.putString("subjectcode", info.getBak5());
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        }
    }

    /**
     * 启动登陆页面
     */
    private void StartOA() {
        try {
            new InternetOfCheckportThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class InternetOfCheckportThread extends Thread {
        public void run() {
            String jsonString = "";
            try {
                // Simulate network access.
                SharedPreferences sharedPreferences = getSharedPreferences("passport", Context.MODE_PRIVATE);
                mIdentify = sharedPreferences.getString("identify", "");
                mUsername = sharedPreferences.getString("whois", "");
                mSelfids = sharedPreferences.getString("selfids", "");
                if (mIdentify == null || mIdentify.equals("") || mIdentify.equals("0")) {
                    mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                    return;
                }
                String _url = ServerInfo.CheckPassportPre + "/" + mIdentify;
//                http get begin
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(_url);
                JSONObject json = null;
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
                if (jsonString == "") {
                    mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                    return;
                }
                JSONObject jsonObj = new JSONObject(jsonString);
                if (jsonObj == null) {
                    mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                    return;
                }
                mIdentify = jsonObj.getString("identify");

                mUsername = jsonObj.getString("user_zh");
                mSelfids = jsonObj.getString("selfids");
                mDuties = jsonObj.getString("Duties");
                if (mIdentify == null || mIdentify == "") {
                    mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                    return;
                }
                mHandler.obtainMessage(MSG_OA_OK, "").sendToTarget();
                return;

            } catch (JSONException e) {
                e.printStackTrace();
                mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(MSG_OA_NO, "").sendToTarget();
                return;
            }
        }
    }

    /**
     * 主界面下拉刷新
     */
    @Override
    public void onRefresh(RefreshableView view) {
        if (DataOfHotNews != null) DataOfHotNews.clear();
        if (DataOfScrollImageNews != null) DataOfScrollImageNews.clear();
        getDataOfHotNews();//刷新热点资讯
        GetWeatherInfo();//刷新天气信息
        getDataOfScrollImageNews();//刷新滚动资讯

    }

    /**
     * 下拉刷新数据状态判定
     */
    void isRefreashCompleted() {
        if (LoadDataBusyFlag_Weather == false && LoadDataBusyFlag_HotNews == false) {
            mHandler.sendEmptyMessageDelayed(MSG_RE_OK, 3000);

        }
    }
    //-----------------------------------------------------------------业务数据交互方法----------end-----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_of_style2__activity2);
        mContext = getApplicationContext();
        NetUtil.hasNetWork(this);  //检查网络，没有网络弹出提示对话框
        initViews(); //初始化控件
        updateAppService(); //更新检查
        NotificationConfig();

        prepareFunctionBtns();
        GetWeatherInfo();
        GetScrollImageNewsDatas();//获取图片资源url   何旺 2016-1-6
        getDataOfHotNews();
        //    prepareViewPagers();
        getDataOfScrollImageNews();//获取滚动图片资讯数据
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        try {
            shownum = (TextView) findViewById(R.id.indexOfStyleShowNum2);
            showtitle = (TextView) findViewById(R.id.indexOfStyleShowTitle2);
            btnStyle2IndexTodayNews = (ScrollListView) findViewById(R.id.btnStyle2IndexTodayNews);
            btnStyle2IndexTodayNews.setOnItemClickListener(this);
            indexOfStyle2Activity2Sv = (ScrollView) findViewById(R.id.indexOfStyle2Activity2Sv);
            ImageButton _erweimabtn = (ImageButton) findViewById(R.id.home_erweima_go);
            _erweimabtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, AppErweimaCodeActivity.class);
                    startActivity(tmpIntent);
                }
            });
//            showNarer();
        } catch (Exception ex) {
        }
        waitSliderPb = (ProgressBar) findViewById(R.id.wait_Slider_pb);
        weather_image = (ImageView) findViewById(R.id.home_weather_ico_qing);
        weather_qing = (TextView) findViewById(R.id.home_weather_qing);
        weather_wendu = (TextView) findViewById(R.id.home_weather_wendu);
        weather_shidu = (TextView) findViewById(R.id.home_weather_shidu);
        weather_fengxiang = (TextView) findViewById(R.id.home_weather_fengsu);
        weather_fengli = (TextView) findViewById(R.id.home_weather_fengsudengli);
        home_refre = (RefreshableView) findViewById(R.id.refresh_rootaaa);
        home_refre.setRefreshListener(this);

    }

    /**
     * 功能按钮处理
     */
    private void prepareFunctionBtns() {
        try {
            //天气
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.home_weather_intent);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IndexOfStyle2Activity2.this, com.ciist.weather.WeatherMainActivity.class);
                    startActivity(intent);
                }
            });
            //二维码
            ImageView erweima = (ImageView) findViewById(R.id.home_erweima_go);
            erweima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IndexOfStyle2Activity2.this, AppErweimaCodeActivity.class);
                    startActivity(intent);
                }
            });
            //搜索
            findViewById(R.id.home_layout_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i = new Intent(IndexOfStyle2Activity2.this, SearchActivity.class);
//                    startActivityForResult(i, 900);
                    startActivity(new Intent(IndexOfStyle2Activity2.this, SearchActivity.class));
                }
            });
            //上方功能区域
            ImageView btnIndex0 = (ImageView) findViewById(R.id.btnStyle2Index02);
            btnIndex0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<SubjectsInfo> article = getSubjects(0);
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, TodayMaActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("subjectlist", (Serializable) article);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });
//            94D5A12F-8DB7-4CC0-BA0F-C24239D3C39E	招商频道
//            C21314D4-A1C9-49C1-BA36-5E967CAD2A3F	旅游频道
//            EA881484-5A36-48A6-A40C-1FC9C9B6E3C9	农业频道
            ImageView btnIndex1 = (ImageView) findViewById(R.id.btnStyle2Index12);
            btnIndex1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent tmpIntent = new Intent(IndexOfStyle2Activity.this, x_BusinessActivity.class);
//                    startActivity(tmpIntent);
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, MerchantsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subjectname", "招商频道");
                    bundle.putString("subjectcode", "94D5A12F-8DB7-4CC0-BA0F-C24239D3C39E");
                    bundle.putBoolean("full", true);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });
            ImageView btnIndex2 = (ImageView) findViewById(R.id.btnStyle2Index22);
            btnIndex2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, TravelActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subjectname", "旅游频道");
                    bundle.putString("subjectcode", "C21314D4-A1C9-49C1-BA36-5E967CAD2A3F");
                    bundle.putBoolean("full", true);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });
            ImageView btnIndex3 = (ImageView) findViewById(R.id.btnStyle2Index32);
            btnIndex3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, AgricultureActivity.class);
                    startActivity(tmpIntent);
                }
            });
            ImageView zhengwufuwu=(ImageView)findViewById(R.id.home_zhengwufuwu);
            zhengwufuwu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(IndexOfStyle2Activity2.this,ServiceActivity.class);
                    startActivity(intent);
                }
            });
            ImageView jiaoyidating=(ImageView)findViewById(R.id.home_jiaoyidongdai);
            jiaoyidating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(IndexOfStyle2Activity2.this,TradingHallActivity.class);
                    startActivity(intent);
                }
            });
            //玩转马龙功能区域
            ImageView homeZhouBianImg = (ImageView) findViewById(R.id.home_zhoubian);  //周边选项
            homeZhouBianImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//ChannelOf
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, NavNearStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("color", getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2","周边");
                    bundle.putString("subjectname", "周边");
                    bundle.putBoolean("hasTime",false);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });

            ImageView homeJingquImg = (ImageView) findViewById(R.id.home_jingqu);  //景区选项
            homeJingquImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2","景区");
                    bundle.putString("subjectname", "景区");
                    bundle.putString("subjectcode", "5B35CC31-8C58-461B-A7EB-80613C28E021");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",false);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });

            ImageView homeZhusuImg = (ImageView) findViewById(R.id.home_zhusu); //住宿选项
            homeZhusuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2","住宿");

                    bundle.putString("subjectname", "住宿");
                    bundle.putString("subjectcode", "AC17015F-8649-4959-B2D7-03B5C081F38F");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",false);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });

            ImageView homeMeisuImg = (ImageView) findViewById(R.id.home_meishi); //美食选项
            homeMeisuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2","美食");

                    bundle.putString("subjectname", "美食");
                    bundle.putString("subjectcode", "2E42694A-E84B-4189-8E5E-3793D855B26A");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",false);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });

            ImageView homeTechanImg = (ImageView) findViewById(R.id.home_techan);  //特产选项
            homeTechanImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(IndexOfStyle2Activity2.this, CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2","特产");

                    bundle.putString("subjectname", "特产");
                    bundle.putString("subjectcode", "98DE6E2A-04FE-4F7B-9811-681E47027BB5");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",false);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });
            //办公系统
            ImageView btnIndex7 = (ImageView) findViewById(R.id.btnStyle2Index72);
            btnIndex7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartOA();
                }
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    //----------------------------------系统功能---begin------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }

    private void updateAppService() {
//        UmengUpdateAgent.setDeltaUpdate(false);
//        UmengUpdateAgent.setUpdateOnlyWifi(false);
//        UmengUpdateAgent.setUpdateCheckConfig(false);
//
//        UmengUpdateAgent.update(this);

//        dialog = new ProgressDialog(this);
//        dialog.setIndeterminate(true);
//        dialog.show();
        try {
            BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
       public void onCheckComplete() {
//            dialog.dismiss();
        }

   }
    //----------------------------------系统功能---end------------------------------


    //何旺
    private HwLvAdapter hwLvAdapter;
    private CiistAdapter2 ciistAdapter;

    /**
     * ListView适配器
     */
    private class HwLvAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DataOfHotNews.size();
        }

        @Override
        public Object getItem(int position) {
            return DataOfHotNews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_ciist_lv, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Hutils.LoadImage(DataOfHotNews.get(position).getImgsrc(), holder.ciistNewsLvItemImg1);
            holder.ciistNewsLvItemContent1.setText(DataOfHotNews.get(position).getTitle());
            holder.ciistNewsLvItemTime1.setText(formatTime(DataOfHotNews.get(position).getPubdate()));
            holder.ciistNewsLvItemNum1.setText(DataOfHotNews.get(position).getVisitCount() + "");
            return convertView;
        }

        /**
         * 改变事件显示格式
         *
         * @param time
         * @return time
         */
        public String formatTime(String time) {
            String strs[] = time.split(" ");
            for (int i = 0; i < strs.length; i++) {
                System.out.println(strs[i]);
            }
            String[] b = strs[0].split("-");
            for (int i = 0; i < b.length; i++) {
                System.out.println(b[i]);
            }
            return b[1] + "-" + b[2];
        }


        class Holder {
            TextView ciistNewsLvItemContent1, ciistNewsLvItemTime1, ciistNewsLvItemNum1;
            ImageView ciistNewsLvItemImg1;

            public Holder(View v) {
                ciistNewsLvItemContent1 = (TextView) v.findViewById(R.id.ciistNewsLvItemContent1);
                ciistNewsLvItemTime1 = (TextView) v.findViewById(R.id.ciistNewsLvItemTime1);
                ciistNewsLvItemNum1 = (TextView) v.findViewById(R.id.ciistNewsLvItemNum1);
                ciistNewsLvItemImg1 = (ImageView) v.findViewById(R.id.ciistNewsLvItemImg1);
            }
        }
    }

    //何旺 2016-01-06
    //TODO
   /* private ImgAdapter imgAdapter;
    private List<View> views2;
  //  private ViewPager vp2;*/
    private ScrollListView btnStyle2IndexTodayNews;

    private void GetScrollImageNewsDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = "";

                String urlPath = ServerInfo.GetInfoPre + "E1AC514F-0972-4647-BD97-20C5090F2704/1/10";
                HttpClient clent = new DefaultHttpClient();
                HttpGet get = new HttpGet(urlPath);
                try {
                    HttpResponse response = clent.execute(get);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
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
//                    for (int i = 0; i < ja.length(); i++) {
//                        JSONObject jo = ja.getJSONObject(i);
//                        String imgPath = ServerInfo.ServerRoot + jo.getString("Image");
//                        Log.i("TAG", imgPath + ".....+++++++++++++++++........");
//                        String link = ServerInfo.InfoDetailPath;
//                        String _linkUrl = link + jo.getString("Info_Ids");
//                        linkUrls.add(_linkUrl);
//                        imgUrls.add(imgPath);
//                    }
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jsonObj = ja.getJSONObject(i);
                        String _Title = jsonObj.getString("Title");
                        if (_Title == "") continue;

                        String link = ServerInfo.InfoDetailPath;
                        String _linkUrl = link + jsonObj.getString("Info_Ids");
                        String _linkDate = jsonObj.getString("pubDate");
                        String _source = jsonObj.getString("Sourse");
                        String _author = jsonObj.getString("Author");
                        String _image = ServerInfo.ServerRoot + jsonObj.getString("Image");
                        String _type = jsonObj.getString("Type");
                        String _bak1 = jsonObj.getString("Remark1");
                        String _bak2 = jsonObj.getString("Remark2");
                        String _bak3 = jsonObj.getString("Remark3");
                        String _bak4 = jsonObj.getString("Remark4");
                        String _bak5 = jsonObj.getString("Remark5");
                        String _telnum = jsonObj.getString("contracttel");
                        Double _longtidue = jsonObj.getDouble("longtidue");
                        Double _latidue = jsonObj.getDouble("latidue");
                        int _visitecounter = jsonObj.getInt("VisitCount");
                        ContentValues values = new ContentValues();
                        values.put("Title", _Title);
                        values.put("Url", _linkUrl);
                        ItemInfo tmpIteminfo = new ItemInfo();
                        tmpIteminfo.setTitle(_Title);
                        tmpIteminfo.setPubdate(_linkDate);
                        tmpIteminfo.setLinkurl(_linkUrl);
                        tmpIteminfo.setImgsrc(_image);
                        tmpIteminfo.setInfosource(_source);
                        tmpIteminfo.setInfoauthor(_author);
                        tmpIteminfo.setInfotype(_type);
                        tmpIteminfo.setBak1(_bak1);
                        tmpIteminfo.setBak2(_bak2);
                        tmpIteminfo.setBak3(_bak3);
                        tmpIteminfo.setBak4(_bak4);
                        tmpIteminfo.setBak5(_bak5);
                        tmpIteminfo.setSource(_source);
                        tmpIteminfo.setAuthor(_author);
                        tmpIteminfo.setVisitCount(_visitecounter);
                        tmpIteminfo.setTelnum(_telnum);
                        tmpIteminfo.setLatidue(_latidue);
                        tmpIteminfo.setLongtidue(_longtidue);
                        DataOfScrollImageNews.add(tmpIteminfo);
                    }
                    mHandler.sendEmptyMessage(0);//0表示图片网址已加载完成
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
