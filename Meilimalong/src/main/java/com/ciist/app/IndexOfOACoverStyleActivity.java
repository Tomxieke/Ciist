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
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.ItemInfoAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexOfOACoverStyleActivity extends AppCompatActivity {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static final int CREATE_NEW_TASK_FLAG = 1;

    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";

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
        setContentView(R.layout.index_of_oa_cover_style_activity);
        waiting = (ProgressBar) findViewById(R.id.waitloading);
        Intent intent = this.getIntent();
        mSubjectCode = intent.getStringExtra("subjectcode");
        mSubjectName = intent.getStringExtra("subjectname");

        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");

        mGPS = intent.getIntExtra("GPS", 0);
        mGIS = intent.getIntExtra("GIS", 0);
        isFullScreen = intent.getBooleanExtra("full", false);
        context = getApplicationContext();
        if (mGPS == 1) {
//            ExecuteMapABCLocation();
//            GISLocation myLocation = getCurrentLocation();
//            ExecuteLocation();
//            if (mLocationClient != null) {
//                mLocationClient.start();
//            }
        }
        if (!isFullScreen) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(mSubjectName);
            toolbar.setVisibility(View.VISIBLE);
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            toolbar.setBackgroundColor(Color.rgb(r, g, b));
            setSupportActionBar(toolbar);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }
        Button dubanBtn= (Button) findViewById(R.id.oa_duban_btn);
        dubanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(IndexOfOACoverStyleActivity.this, IndexOfOACreateNewTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                tmpIntent.putExtras(bundle);
                startActivityForResult(tmpIntent, CREATE_NEW_TASK_FLAG);
            }
        });
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
                        tmpIntent.putExtra("IsDuban",true);
                        tmpIntent.putExtra("depcode",tmpobj.getDepcode());
                        tmpIntent.putExtra("identify",mIdentify);
                        tmpIntent.putExtra("username",mUsername);
                        tmpIntent.putExtra("selfids",mSelfids);
                        startActivity(tmpIntent);
                    } else {
                        if (linkType != null && linkType == "ciist1") {

                        } else {
                            Intent tmpIntent = new Intent(IndexOfOACoverStyleActivity.this, IndexOfOACoverStyleActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("subjectname", tmpobj.getTitle());
                            bundle.putString("subjectcode", tmpobj.getBak5());

                            bundle.putString("identify",mIdentify);
                            bundle.putString("username",mUsername);
                            bundle.putString("selfids",mSelfids);
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


//        try {
//            if (loading == null)
//                loading = new ProgressDialog(context, ProgressDialog.STYLE_HORIZONTAL);
//            loading.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
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
                String _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/20";

//                backup
//                JSONStringer _jsonstrig;
//                _jsonstrig = new JSONStringer()
//                        .object()
//                        .key("_header")
//                        .object()
//                        .key("SKEY").value("CIISTKEY")
//                        .key("PageIndex").value(currentPageInfo.getPageIndex())
//                        .key("PageSize").value(currentPageInfo.getPageSize())
//                        .key("Identify").value("54fffff444")
//                        .endObject()
//                        .key("MenuIDS").value(mSubjectCode)
//                        .endObject();
//                backup
//post begin
//                    entity.setContentType(CONTENT_TYPE_TEXT_JSON);
//                    entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));

//                HttpPost _post = new HttpPost(ServerInfo.requestDataUrlRoot);
//                _post.setHeader("Accept", APPLICATION_JSON);
////                _post.setHeader("Content-type", "application/json");
//                _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
//                JSONStringer _jsonstrig;
//                try {
//                    _jsonstrig = new JSONStringer()
//                            .object()
//                            .key("_header")
//                            .object()
//                            .key("SKEY").value("CIISTKEY")
//                            .key("PageIndex").value(currentPageInfo.getPageIndex())
//                            .key("PageSize").value(currentPageInfo.getPageSize())
//                            .key("Identify").value("54fffff444")
//                            .endObject()
//                            .key("MenuIDS").value(mSubjectCode)
//                            .endObject();
//
//                    StringEntity entity = new StringEntity(_jsonstrig.toString(), HTTP.UTF_8);//需要设置成utf-8否则汉字乱码
//                    entity.setContentType(CONTENT_TYPE_TEXT_JSON);
//                    entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
//                    _post.setEntity(entity);
//                    // 向WCF服务发送请求
//
//                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpResponse response = httpClient.execute(_post);
//                    // 判断是否成功
//                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
//                        jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
////                        System.out.println("strResp=" + strResp);
//                    }
//
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (NullPointerException ex) {
//                    ex.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                post end


                //json

//                URL url = new URL(_url);
//                ByteArrayOutputStream os = new ByteArrayOutputStream();
//                byte[] datacache = new byte[1024];
//                int len = 0;
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                InputStream is = conn.getInputStream();
//                while ((len = is.read(datacache)) != -1) {
//                    os.write(datacache, 0, len);
//                }
//                is.close();
//                jsonString = new String(os.toByteArray());
//                conn.disconnect();

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
                //json
//            String zixunUrl = "http://149.212.154:2015/meilimalong/search/index/" + mSubjectCode + "," + currentPageInfo.getPageIndex() + ",10";
//            Document doc;
//            String RootNode = "#list";
//            try {
//                doc = Jsoup.connect(zixunUrl).timeout(9000).post();
//                Document content = Jsoup.parse(doc.toString());
//                if (content.toString().contains("No Data")) {
//                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
//                    return;
//                }
//                Elements divs = content.select(RootNode);
//                Document divcontions = Jsoup.parse(divs.toString());
//                Elements element = divcontions.getElementsByTag("li");
//                for (Element links : element) {
//                    String title = links.select("a").text();
//                    if (title == "") continue;
//                    String link = links.select("a").attr("href").trim();
//                    String linkUrl = "http://.149.212.154:2015" + link;
//                    String linkDate = links.select("a").attr("createdate").trim();
//                    String _source = links.select("a").attr("sourse").trim();
//                    String _author = links.select("a").attr("author").trim();
//                    String _image = links.select("a").attr("image").trim();
//                    String _type = links.select("a").attr("type").trim();
//                    String _bak1 = links.select("a").attr("Remark1").trim();
//                    String _bak2 = links.select("a").attr("Remark2").trim();
//                    String _bak3 = links.select("a").attr("Remark3").trim();
//                    String _bak4 = links.select("a").attr("Remark4").trim();
//                    String _bak5 = links.select("a").attr("Remark5").trim();
//                    ContentValues values = new ContentValues();
//                    values.put("Title", title);
//                    values.put("Url", linkUrl);
//                    ItemInfo tmpIteminfo = new ItemInfo();
//                    tmpIteminfo.setTitle(title);
//                    tmpIteminfo.setPubdate(linkDate);
//                    tmpIteminfo.setLinkurl(linkUrl);
//                    tmpIteminfo.setImgsrc(_image);
//                    tmpIteminfo.setInfosource(_source);
//                    tmpIteminfo.setInfoauthor(_author);
//                    tmpIteminfo.setInfotype(_type);
//                    tmpIteminfo.setBak1(_bak1);
//                    tmpIteminfo.setBak2(_bak2);
//                    tmpIteminfo.setBak3(_bak3);
//                    tmpIteminfo.setBak4(_bak4);
//                    tmpIteminfo.setBak5(_bak5);
//                    tmpIteminfo.setSource(_source);
//                    tmpIteminfo.setAuthor(_author);
//                    data.add(tmpIteminfo);
//                }
                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setSimpleItemObj(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

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
