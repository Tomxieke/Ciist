package com.ciist.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 中联软科 on 2016/2/19.
 */
public class NavNearStyleActivity extends Activity implements XListView.IXListViewListener{
    private boolean hasTime ;
    private CiistTitleView navneartitle;
    private XListView navNearStyleLv;
    private ProgressBar navNearStylePb;

    private int titleColor ;//标题背景颜色
    private String zhoubian = "";//周边

    private boolean mIsDuban = false;
    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";

    private AMapLocationClient mLocationClient = null;
    private GISLocation mCurrentLocation = new GISLocation();
    private ArrayList<ItemInfo> mData = new ArrayList<ItemInfo>();
    private int index = 1;
    private Context mContext = null;
    private CiistAdapter2 adapter = null;
    private int length;//获取新数据的长度

    private static final int MSG_GPS_SUCCESS = 1;
    private static final int MSG_SUCCESS = 2;
    private static final int MSG_GPS_CANCEL = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_GPS_SUCCESS:
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                    loadData();
                    break;
                case MSG_SUCCESS:
                    if (adapter == null){
                        adapter = new CiistAdapter2(mContext,mData);
                        navNearStyleLv.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    navNearStylePb.setVisibility(View.INVISIBLE);
                    if (length < 10){
                        navNearStyleLv.setPullLoadEnable(false);
                    }
                    onLoad();
                    break;
                case MSG_GPS_CANCEL:
                    Toast.makeText(mContext, "定位失败，重新定位中", Toast.LENGTH_SHORT).show();
                    ExecuteMapABCLocation();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_navnearstyle);
        initView();
        getIntentExtra();
        ExecuteMapABCLocation();
        TotalListener();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        navneartitle=(CiistTitleView)findViewById(R.id.chan_zhoubian_titleview);
        Intent intent = this.getIntent();
        hasTime = intent.getBooleanExtra("hasTime",true);
        navneartitle.setTitle(intent.getStringExtra("subjectname2"));
        navneartitle.setBgColor(intent.getIntExtra("color",0));
        navNearStyleLv = (XListView) findViewById(R.id.navNearStyleLv);
        navNearStylePb = (ProgressBar) findViewById(R.id.navNearStylePb);

        navNearStyleLv.setPullLoadEnable(false);
        navNearStyleLv.setPullRefreshEnable(false);
        navNearStyleLv.setXListViewListener(this);
    }

    /**
     * 得到Intent传入的值
     */
    private void getIntentExtra(){
        mContext = NavNearStyleActivity.this;
        Intent intent = this.getIntent();
        titleColor = intent.getIntExtra("color",0);
        zhoubian = intent.getStringExtra("subjectname");
    }

    /**
     * 加载数据
     */
    private void loadData(){
        navNearStyleLv.setPullLoadEnable(true);
        new InternetOfSubject0Thread().start();
    }

    /**
     * 所有控件的监听
     */
    private void TotalListener(){
        navneartitle.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navNearStyleLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                startNextAct(mData.get(position));
            }
        });
        navneartitle.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
                finish();
            }
        });
    }

    /**
     * 刷新完成
     */
    private void onLoad() {
        navNearStyleLv.stopRefresh();
        navNearStyleLv.stopLoadMore();
        navNearStyleLv.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
    //
    }

    @Override
    public void onLoadMore() {//加载更多
        index++;
        loadData();
    }

    /**
     * 得到数据
     */
    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            try {
                ArrayList<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.GetNavDistanceInfoPre + mCurrentLocation.getLongitude()
                        +"/"+mCurrentLocation.getLatitude()+ "/" + index + "/10";
                String jsonString = "";
                URL url = new URL(_url);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] datacache = new byte[1024];
                int len = 0;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                while ((len = is.read(datacache)) != -1) {os.write(datacache, 0, len);}
                is.close();
                jsonString = new String(os.toByteArray());
                conn.disconnect();
                if (jsonString == "") {return;}
                JSONArray jsonArray = new JSONArray(jsonString);
                length = jsonArray.length();
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
                    String _telnum=jsonObj.getString("contracttel");
                    Double _longtidue=jsonObj.getDouble("longtidue");
                    Double _latidue=jsonObj.getDouble("latidue");
                    Double _juli=jsonObj.getDouble("juli");
                    int _visitecounter=jsonObj.getInt("VisitCount");
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
                    tmpIteminfo.setJuli(_juli);
                    Log.e("a", _juli + "");
                    data.add(tmpIteminfo);
                }
                mData.addAll(data);
                mHandler.sendEmptyMessage(MSG_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
            }
        }
    }


    /**
     * jin ru xai yi ge activity
     * @param info
     */
    private void startNextAct(ItemInfo info) {
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(mContext, WebActivity.class);
            tmpIntent.putExtra("URL", info.getLinkurl());
            tmpIntent.putExtra("TITLE", info.getTitle());
            tmpIntent.putExtra("PUBDATE", info.getPubdate());
            tmpIntent.putExtra("source", info.getInfosource());
            tmpIntent.putExtra("author", info.getInfoauthor());
            tmpIntent.putExtra("infotype", info.getInfotype());
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("blank", true);
            tmpIntent.putExtra("telnum", info.getTelnum());
            tmpIntent.putExtra("latidue_e", info.getLatidue());
            tmpIntent.putExtra("longtidue_e", info.getLongtidue());
            tmpIntent.putExtra("latidue_b", mCurrentLocation.getLatitude());
            tmpIntent.putExtra("longtidue_b", mCurrentLocation.getLongitude());
            tmpIntent.putExtra("IsDuban", mIsDuban);
            tmpIntent.putExtra("depcode", info.getDepcode());
            tmpIntent.putExtra("identify", mIdentify);
            tmpIntent.putExtra("username", mUserName);
            tmpIntent.putExtra("selfids", mSelfids);
            tmpIntent.putExtra("hasTime",hasTime);
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {

            } else {
                Intent tmpIntent = new Intent(mContext, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subjectname", info.getTitle());
                bundle.putString("subjectcode", info.getBak5());
                bundle.putBoolean("IsDuban", mIsDuban);
                bundle.putString("depcode", info.getDepcode());
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUserName);
                bundle.putString("selfids", mSelfids);
                bundle.putBoolean("hasTime",hasTime);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        }
    }


    /**
     * map abc location
     */
    private void ExecuteMapABCLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext()); //初始化定位
        mLocationClient.setLocationListener(mLocationListener);//设置定位回调监听
        AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
        mLocationOption = new AMapLocationClientOption();//初始化定位参数
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.startLocation();//启动定位
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    mCurrentLocation.setLongitude(amapLocation.getLongitude());
                    mCurrentLocation.setLatitude(amapLocation.getLatitude());
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取经度
                    amapLocation.getLongitude();//获取纬度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getRoad();//街道信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    mHandler.sendEmptyMessage(MSG_GPS_SUCCESS);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    mHandler.sendEmptyMessageDelayed(MSG_GPS_CANCEL, 5 * 1000);
                }
            }
        }
    };

    /**
     *  fan hui jian jian ting
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            finish();
        }
        return false;
    }

}
