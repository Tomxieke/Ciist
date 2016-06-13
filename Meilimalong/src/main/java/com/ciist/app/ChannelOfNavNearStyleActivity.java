package com.ciist.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.ItemInfoAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChannelOfNavNearStyleActivity extends AppCompatActivity {
    private String mSubjectCode = "";
    private String mSubjectName = "";
    private int mGPS = 0;
    private int mGIS = 0;
    private GISLocation mCurrentLocation = new GISLocation();
    private boolean isFullScreen = false;
    private int headerHeight = 0;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_GPS = 90;//
    //    private ProgressDialog loading;
    private ProgressBar waiting;
    private ListView coverlistView;
    private List<ItemInfo> data;
    private LocationManager locationManager;
    private String locationProvider;
    public AMapLocationClient mLocationClient = null;

    //    map abc end
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    //    private ArrayAdapter<ItemInfo> adapter;
    private ItemInfoAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private View footer;
    private Context context;
    private InternetOfSubject0Thread subjectThread;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
                    coverlistView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ItemInfo>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
                        coverlistView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                        return;
                    }
                    if (result.getSimpleItemObj().size() < 10) {
                        coverlistView.removeFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getSimpleItemObj());
                    if (adapter == null) {
                        int resID = R.layout.listview_item;
                        adapter = new ItemInfoAdapter(context, resID, data);
                        coverlistView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    currentHavePageIndex++;
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
//                    loading.dismiss();
                    break;
                case MSG_FAILURE:
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    Toast.makeText(context, "亲,网络不给力呀!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NODATA:
                    coverlistView.removeFooterView(footer);
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
                case MSG_GPS:
                    GISLocation _location = (GISLocation) msg.obj;
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy();
                    LoadDatas();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLocationClient.unRegisterLocationListener(MyLocationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_of_nav_near_style_activity);
        CiistTitleView chan_zhou_title=(CiistTitleView)findViewById(R.id.channel_zhoubian_titleview);

        chan_zhou_title.setVisibility(View.VISIBLE);
        chan_zhou_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ItemInfo info=new ItemInfo();
        Intent intent = this.getIntent();
        chan_zhou_title.setTitle(intent.getStringExtra("subjectname2"));
        chan_zhou_title.setBgColor(intent.getIntExtra("color", 0));
        waiting = (ProgressBar) findViewById(R.id.waitloading);
        isFullScreen = intent.getBooleanExtra("full", false);
        context = getApplicationContext();
        if (1 == 1) {
            ExecuteMapABCLocation();
        }
        if (!isFullScreen) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(mSubjectName);
            toolbar.setVisibility(View.GONE);
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            toolbar.setBackgroundColor(Color.rgb(r, g, b));
            setSupportActionBar(toolbar);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }
        coverlistView = (ListView) findViewById(R.id.coverlist);
        coverlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if (id == -1) {
                        LoadDatas();
                        return;
                    }
                    View _view = view.findViewById(R.id.simpleTextView1);
                    TextView tmp = (TextView) _view;
                    ItemInfo tmpobj = (ItemInfo) tmp.getTag();
                    String linkType = tmpobj.getBak4();
                    String subjectLink = tmpobj.getBak5();
                    if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                        Intent tmpIntent = new Intent(context, WebActivity.class);
                        tmpIntent.putExtra("URL", tmpobj.getLinkurl());
                        tmpIntent.putExtra("TITLE", tmpobj.getTitle());
                        tmpIntent.putExtra("PUBDATE", tmpobj.getPubdate());
                        tmpIntent.putExtra("source", tmpobj.getInfosource());
                        tmpIntent.putExtra("author", tmpobj.getInfoauthor());
                        tmpIntent.putExtra("infotype", tmpobj.getInfotype());
                        tmpIntent.putExtra("ROOT", "#Content");
                        tmpIntent.putExtra("blank", true);
                        tmpIntent.putExtra("telnum", tmpobj.getTelnum());
                        tmpIntent.putExtra("latidue_e", tmpobj.getLatidue());
                        tmpIntent.putExtra("longtidue_e", tmpobj.getLongtidue());
                        tmpIntent.putExtra("latidue_b", mCurrentLocation.getLatitude());
                        tmpIntent.putExtra("longtidue_b", mCurrentLocation.getLongitude());
                        startActivity(tmpIntent);
                    } else {
                        if (linkType != null && linkType == "ciist1") {

                        } else {
                            Intent tmpIntent = new Intent(ChannelOfNavNearStyleActivity.this, ChannelOfNavNearStyleActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("subjectname", tmpobj.getTitle());
                            bundle.putString("subjectcode", tmpobj.getBak5());
                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage().toString());
                }
            }
        });
        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        iniview();
//        LoadDatas();
    }

    void iniview() {
        try {
            footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
            if (footer == null) {
                return;
            }
            coverlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    try {
                        if (coverlistView.getFooterViewsCount() <= 0) {
                            return;
                        }
                        int lastItemIndexID = firstVisibleItem + visibleItemCount;
                        if (lastItemIndexID <= 0) return;
                        if (lastItemIndexID == totalItemCount) {
                            View lastItemView = coverlistView.getChildAt(coverlistView.getChildCount() - 1);
                            headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                            int bottomChazhi = Math.abs(coverlistView.getBottom() - lastItemView.getBottom());
                            if (lastItemView != null && bottomChazhi == headerHeight && footer != null) {
                                currentPageInfo.setPageIndex(currentHavePageIndex + 1);
                                LoadDatas();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage().toString());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage().toString());
        }
    }

    void LoadDatas() {
        try {
            subjectThread = new InternetOfSubject0Thread();
            subjectThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage().toString());
        }
    }

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.GetNavDistanceInfoPre + mCurrentLocation.getLongitude()
                        +"/"+mCurrentLocation.getLatitude()+ "/" + currentPageInfo.getPageIndex() + "/10";
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
                if (jsonString == "") {
                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
                    return;
                }
                JSONArray jsonArray = new JSONArray(jsonString);
                if (jsonArray == null || jsonArray.length() <= 0) {
                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
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
                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setSimpleItemObj(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

    //    map abc location
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
                    mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                }
            }
        }
    };

}
