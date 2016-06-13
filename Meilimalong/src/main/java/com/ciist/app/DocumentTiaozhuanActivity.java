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
import com.ciist.helper.GISHelper;
import com.ciist.toolkits.ItemInfoAdapter;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentTiaozhuanActivity extends AppCompatActivity {
    private Context mcontext;
    private CiistTitleView chan_title;
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
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

    private boolean mIsDuban=false;
    private String mIdentify="";
    private String mUserName="";
    private String mSelfids="";


    //    private ProgressDialog loading;
    private ProgressBar waiting;
    private ListView coverlistView;
    private List<ItemInfo> data;
    private LocationManager locationManager;
    private String locationProvider;
    //baidu location
    //private LocationClient mLocationClient = null;
    //map abc begin
    //声明AMapLocationClient类对象
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
//                    Toast.makeText(context, String.valueOf(_location.getLatitude()) + ",long=" + String.valueOf(_location.getLongitude()), Toast.LENGTH_SHORT).show();
//                    mLocationClient.unRegisterLocationListener(MyLocationListener);
//                    mLocationClient.stop();
                    mLocationClient.stopLocation();
                    mLocationClient.onDestroy(); //103.578478,25.428031
//                    double ddd = GISHelper.GetDistance(_location.getLatitude(), _location.getLongitude(), 25.428031,  103.578478);
//                    Toast.makeText(context, "距离=" + String.valueOf(ddd), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_document_tiaozhuan);
        mcontext = getApplicationContext();






        waiting = (ProgressBar) findViewById(R.id.document_tiaozhuan_waitloading);
        chan_title=(CiistTitleView)findViewById(R.id.document_tiaozhuan__titleview);
        chan_title.setVisibility(View.VISIBLE);
        chan_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ItemInfo info=new ItemInfo();
        Intent intent = this.getIntent();
        chan_title.setTitle(intent.getStringExtra("subjectname2"));
//        chan_title.setBgColor(intent.getIntExtra("color",1));


        mSubjectCode = intent.getStringExtra("subjectcode");
        mSubjectName = intent.getStringExtra("subjectname");

        mIsDuban=intent.getBooleanExtra("IsDuban",false);
        mIdentify=intent.getStringExtra("identify");
        mUserName=intent.getStringExtra("username");
        mSelfids=intent.getStringExtra("selfids");

        mGPS = intent.getIntExtra("GPS", 1);
        mGIS = intent.getIntExtra("GIS", 0);
        isFullScreen = intent.getBooleanExtra("full", false);
        context = getApplicationContext();

        if (mGPS == 1) {
            ExecuteMapABCLocation();
//            GISLocation myLocation = getCurrentLocation();
//            ExecuteLocation();
//            if (mLocationClient != null) {
//                mLocationClient.start();
//            }
        }
        if (!isFullScreen) {

            chan_title.setTitle(intent.getStringExtra("subjectname2"));
//            chan_title.setBgColor(intent.getIntExtra("colorgird",0));
            Toolbar toolbar = (Toolbar) findViewById(R.id.document_tiaozhuan_toolbar);
            toolbar.setTitle(mSubjectName);
            toolbar.setVisibility(View.GONE);
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            toolbar.setBackgroundColor(Color.rgb(r, g, b));
//            setSupportActionBar(toolbar);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.document_tiaozhuan_toolbar);
            toolbar.setVisibility(View.GONE);
        }
        coverlistView = (ListView) findViewById(R.id.document_tiaozhuan_coverlist);
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
                        Intent tmpIntent = new Intent(context, Web2Activity.class);
                        tmpIntent.putExtra("subjecttitle",tmpobj.getTitle());
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

                        tmpIntent.putExtra("IsDuban",mIsDuban);
                        tmpIntent.putExtra("depcode",tmpobj.getDepcode());
                        tmpIntent.putExtra("identify",mIdentify);
                        tmpIntent.putExtra("username",mUserName);
                        tmpIntent.putExtra("selfids",mSelfids);

                        startActivity(tmpIntent);
                    } else {
                        if (linkType != null && linkType == "ciist1") {

                        } else {
                            Intent tmpIntent = new Intent(DocumentTiaozhuanActivity.this, DocumentTiaozhuanActivity.class);
                            Bundle bundle = new Bundle();
//                            bundle.putInt("colorgird",getResources().getColor(R.color.hongsetitle));

//                            bundle.putString("subjectname2", info.getTitle());


                            bundle.putString("subjectname2", tmpobj.getTitle());
                            bundle.putString("subjectcode", tmpobj.getBak5());

                            bundle.putBoolean("IsDuban",mIsDuban);
                            bundle.putString("depcode", tmpobj.getDepcode());
                            bundle.putString("identify", mIdentify);
                            bundle.putString("username", mUserName);
                            bundle.putString("selfids", mSelfids);

                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
//                    System.out.println(e.getMessage().toString());
                }
            }
        });

        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        iniview();
        LoadDatas();







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
//            System.out.println(e.getMessage().toString());
        }
    }

    void LoadDatas() {
        try {
            subjectThread = new InternetOfSubject0Thread();
            subjectThread.start();
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e.getMessage().toString());
        }
    }

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/10";
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

                    tmpIteminfo.setDepcode(jsonObj.getString("depcode"));
                    tmpIteminfo.setIspub(jsonObj.getInt("ispub"));

                    data.add(tmpIteminfo);
                }
                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setSimpleItemObj(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }
    private void ExecuteMapABCLocation() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext()); //初始化定位
            mLocationClient.setLocationListener(mLocationListener);//设置定位回调监听
            AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
            mLocationOption = new AMapLocationClientOption();//初始化定位参数
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setNeedAddress(false);//设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
            mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
            mLocationClient.startLocation();//启动定位
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            try {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        mCurrentLocation.setLongitude(amapLocation.getLongitude());
                        mCurrentLocation.setLatitude(amapLocation.getLatitude());
                        //定位成功回调信息，设置相关消息
//                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                        amapLocation.getLatitude();//获取经度
//                        amapLocation.getLongitude();//获取纬度
//                        amapLocation.getAccuracy();//获取精度信息
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date date = new Date(amapLocation.getTime());
//                        df.format(date);//定位时间
//                        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
//                        amapLocation.getCountry();//国家信息
//                        amapLocation.getProvince();//省信息
//                        amapLocation.getCity();//城市信息
//                        amapLocation.getDistrict();//城区信息
//                        amapLocation.getRoad();//街道信息
//                        amapLocation.getCityCode();//城市编码
//                        amapLocation.getAdCode();//地区编码
                        mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
//    map abc locaton end
    //     location begin
//    private void ExecuteLocation() {
//        mLocationClient = new LocationClient(this.getApplicationContext());
//        //设置定位条件
//        LocationClientOption option = new LocationClientOption();
//        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
////            Toast.makeText(getApplicationContext(), "GPS已开启", Toast.LENGTH_SHORT).show();
//            option.setOpenGps(true);        //是否打开GPS
//        } else {
//            Toast.makeText(getApplicationContext(), "GPS没有开启", Toast.LENGTH_SHORT).show();
//            option.setOpenGps(false);        //是否打开GPS
//        }
//
//        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
//        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
//        option.setProdName("魅力马龙"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
//        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
////        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
//        mLocationClient.setLocOption(option);
//        //注册位置监听器
//        mLocationClient.registerLocationListener(MyLocationListener);
//    }
//
//    public BDLocationListener MyLocationListener = new BDLocationListener() {
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//
//            if (bdLocation == null) {
//                mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
//            }
//            mCurrentLocation.setLatitude(bdLocation.getLatitude());
//            mCurrentLocation.setLongitude(bdLocation.getLongitude());
//            if (bdLocation.getRadius() == 0) {
//                mCurrentLocation.setRadius(6378.137);
//            } else {
//                mCurrentLocation.setRadius(bdLocation.getRadius());
//            }
////            StringBuffer sb = new StringBuffer(256);
////            sb.append("Time : ");
////            sb.append(bdLocation.getTime());
////            sb.append("\nError code : ");
////            sb.append(bdLocation.getLocType());
////            sb.append("\nLatitude : ");
////            sb.append(bdLocation.getLatitude());
////            sb.append("\nLontitude : ");
////            sb.append(bdLocation.getLongitude());
////            sb.append("\nRadius : ");
////            sb.append(bdLocation.getRadius());
////            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
////                sb.append("\nSpeed : ");
////                sb.append(bdLocation.getSpeed());
////                sb.append("\nSatellite : ");
////                sb.append(bdLocation.getSatelliteNumber());
////            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
////                sb.append("\nAddress : ");
////                sb.append(bdLocation.getAddrStr());
////            }
////            LOCATION_COUTNS++;
////            sb.append("\n检查位置更新次数：");
////            sb.append(String.valueOf(LOCATION_COUTNS));
//            mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
//
//        }
//    };
//    locationend






}
