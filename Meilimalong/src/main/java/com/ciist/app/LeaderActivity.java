package com.ciist.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.hw.ciist.util.Hutils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
public class LeaderActivity extends Activity implements XListView.IXListViewListener{

    private CiistTitleView coverstyle_title;
    private XListView coverStyleLv;//列表
    private ProgressBar coverStylePb;
    private boolean NetState = false;//网络状态
    private String mSubjectCode;//编码
    private String mSubjectName;//标题名
    private Context mContext;
    private int index = 1;
    private CiistAdapter2 adapter = null;
    private ArrayList<ItemInfo> mItemList = new ArrayList<ItemInfo>();

    private boolean mIsDuban = false;
    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";
    private int mGPS = 0;
    private int mGIS = 0;
    private GISLocation mCurrentLocation = new GISLocation();
    private boolean isFullScreen = false;
    public AMapLocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);
        initView();
        initOther();
        getNetStateAndData();
        TotalListener();
    }
    /**
     * 初始化其他
     */
    private void initOther() {
        mContext = LeaderActivity.this;
        Intent intent = this.getIntent();
        mSubjectCode = intent.getStringExtra("subjectcode");
        mSubjectName = intent.getStringExtra("subjectname");
        NetState = Hutils.getNetState(mContext);

        mIsDuban = intent.getBooleanExtra("IsDuban", false);
        mIdentify = intent.getStringExtra("identify");
        mUserName = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");

        mGPS = intent.getIntExtra("GPS", 1);
        mGIS = intent.getIntExtra("GIS", 0);
        isFullScreen = intent.getBooleanExtra("full", false);
        ItemInfo info=new ItemInfo();
        Intent intent2 = this.getIntent();
        coverstyle_title.setTitle(intent2.getStringExtra("subjectname2"));
        coverstyle_title.setBgColor(intent2.getIntExtra("color", 0));
        coverstyle_title.setBgColor(intent2.getIntExtra("colorgird",0));
        if (mGPS == 1) {
            ExecuteMapABCLocation();
            coverstyle_title.setTitle(intent2.getStringExtra("subjectname2"));
            coverstyle_title.setBgColor(intent2.getIntExtra("color", 0));
            coverstyle_title.setBgColor(intent2.getIntExtra("colorgird",0));
        }
    }


    /**
     * 初始化控件
     */
    private void initView() {
        coverStyleLv = (XListView) findViewById(R.id.lead_coverStyleLv);
        coverStylePb = (ProgressBar) findViewById(R.id.lead_coverStylePb);
        coverstyle_title=(CiistTitleView)findViewById(R.id.lead_coverstyle_titleview);
        coverStyleLv.setPullRefreshEnable(false);
        coverStyleLv.setPullLoadEnable(false);
        coverStyleLv.setXListViewListener(this);
    }

    /**
     * listVIew 的监听
     */
    public void TotalListener(){
        coverStyleLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position--;
                ItemInfo itemInfo = mItemList.get(position);
                String type = itemInfo.getInfotype();
                if (type.equalsIgnoreCase("cover200")){   //如果是纯图模式取消点击事件
                    return;
                }
                startNextAct(mItemList.get(position));
            }
        });
        coverstyle_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        coverstyle_title.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaderActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        ItemInfo info=new ItemInfo();
        Intent intent = this.getIntent();
        coverstyle_title.setTitle(intent.getStringExtra("subjectname2"));
        coverstyle_title.setBgColor(intent.getIntExtra("color", 0));
        coverstyle_title.setBgColor(intent.getIntExtra("colorgird",0));
    }

    /**
     * jin ru xai yi ge activity
     * @param info
     */
    private void startNextAct(ItemInfo info) {
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(mContext, LeaderWebActivity.class);
            tmpIntent.putExtra("URL", info.getLinkurl());
            tmpIntent.putExtra("TITLE", info.getTitle());
            tmpIntent.putExtra("PUBDATE", info.getPubdate());
            tmpIntent.putExtra("source", info.getInfosource());
            tmpIntent.putExtra("author", info.getInfoauthor());
            tmpIntent.putExtra("infotype", info.getInfotype());
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("img_url",info.getImgsrc());
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
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {

            } else {
                Intent tmpIntent = new Intent(mContext, LeaderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.hongsetitle));
                bundle.putString("subjectname2", info.getTitle());

                bundle.putString("subjectname", info.getTitle());
                bundle.putString("subjectcode", info.getBak5());
                bundle.putBoolean("IsDuban", mIsDuban);
                bundle.putString("depcode", info.getDepcode());
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUserName);
                bundle.putString("selfids", mSelfids);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        }
    }


    /**
     * 根据的到的网络状态判断去取出数据
     */
    private void getNetStateAndData() {
        if (NetState) {
            getListData();
        } else {
            Toast.makeText(mContext, "网络状态异常", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 异步取数据
     */
    private void getListData() {
        String _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + index + "/10";
        System.out.println(_url + ".....................");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mContext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
                    coverStyleLv.setPullLoadEnable(true);
                    if (jsonArray.length() < 10) {
                        coverStyleLv.setPullLoadEnable(false);
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        itemInfo = new ItemInfo();
                        JSONObject object = jsonArray.getJSONObject(j);
                        String link = ServerInfo.InfoDetailPath;
                        itemInfo.setTitle(object.getString("Title"));
                        itemInfo.setPubdate(object.getString("pubDate"));
                        itemInfo.setLinkurl(link + object.getString("Info_Ids"));
                        itemInfo.setImgsrc(ServerInfo.ServerRoot + object.getString("Image"));
                        itemInfo.setInfosource(object.getString("Sourse"));
                        itemInfo.setInfoauthor(object.getString("Author"));
                        itemInfo.setInfotype(object.getString("Type"));
                        itemInfo.setBak1(object.getString("Remark1"));
                        itemInfo.setBak2(object.getString("Remark2"));
                        itemInfo.setBak3(object.getString("Remark3"));
                        itemInfo.setBak4(object.getString("Remark4"));
                        itemInfo.setBak5(object.getString("Remark5"));
                        itemInfo.setSource(object.getString("Sourse"));
                        itemInfo.setAuthor(object.getString("Author"));
                        itemInfo.setTelnum(object.getString("contracttel"));
                        itemInfo.setLongtidue(object.getDouble("longtidue"));
                        itemInfo.setLatidue(object.getDouble("latidue"));
                        itemInfo.setVisitCount(object.getInt("VisitCount"));
                        System.out.println(itemInfo.getBak1() + ".....................");
                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        mItemList.add(itemInfo);
                    }
                    if (adapter == null) {
                        adapter = new CiistAdapter2(mContext, mItemList);
                        coverStyleLv.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    onLoad();
                    coverStylePb.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        index++;
        getNetStateAndData();
    }

    /**
     * 刷新完成停止所以加载动作
     */
    private void onLoad() {
        coverStyleLv.stopRefresh();
        coverStyleLv.stopLoadMore();
        coverStyleLv.setRefreshTime("刚刚");
    }


    //   ---------------------------------------------------------------------------------------
    //    map abc location
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
                        //mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        //mHandler.obtainMessage(MSG_GPS, mCurrentLocation).sendToTarget();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
