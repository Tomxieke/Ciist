package com.ciist.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistSmallTitle;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.IGridView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.IRecyclerViewManger;
import com.ciist.toolkits.TravelJingxuanAdapter;
import com.ciist.toolkits.TravelMeishiAdapter;
import com.ciist.toolkits.TravelTechanAdapter;
import com.ciist.util.NetUtil;
import com.ciist.util.RefreshableView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.scxh.slider.library.SliderLayout;
import com.scxh.slider.library.SliderTypes.BaseSliderView;
import com.scxh.slider.library.SliderTypes.TextSliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TravelActivity extends Activity implements View.OnClickListener, RefreshableView.RefreshListener {

    private boolean hasTime = false;   //在资讯详情界面（网页）下是否显示时间
    private final static String ZHUSU = "71A63030-0ED4-41E5-BC19-B02D2A288497";
    private final static String MEISHI = "C2B00AE2-3B81-448B-B3E0-36C7613086AC";
    private final static String TECHAN = "B85981D5-1BFF-486B-81B6-2BCEB60DAEF4";
    private final static String FENGJING = "FE32D439-9C84-4AC7-B32B-92AB64AE441C";
    private final static String LUNBOIMG = "A124E3FC-DF64-4BDF-98FD-F88C9B2BECE8";
    private final static String GONGLUE = "1A95F117-1861-46FE-9C74-5FC57061AC37";
    private final static String GUANGGAO = "4B4DE077-9D7A-4F86-83EB-6A4FCFA4AFE6";

    private String mSubjectCode = "";
    private String mSubjectName = "";
    private int mGPS = 0;
    private int mGIS = 0;

    private GISLocation mCurrentLocation = new GISLocation();
    private boolean mIsDuban = false;
    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";
    private Context context;

    private IGridView mGridView;  //住宿模块
    private IGridViewAdapter mGridAdapter;
    private CiistTitleView travel_title;

    private ScrollListView listView; // 美食模块
    private TravelMeishiAdapter mListViewAdapter;

    private RecyclerView mRecyclerView;  //特产模块
    private TravelTechanAdapter mRecyclerViewAdapter;

    private ScrollListView mJingxuanList;  //精选模块
    private TravelJingxuanAdapter mJingxuanAdapter;

    private ImageView iv_1, iv_2, iv_3, iv_4;  //风景四项图片
    private TextView tv_1, tv_2, tv_3, tv_4;    //风景四项标题
    private ImageView g_iv_1, g_iv_2, g_iv_3, g_iv_4;//广告四项
    private CiistSmallTitle travel_jingqutiejian_title;//景区标题
    private CiistSmallTitle travel_zhusu_title;//住宿标题
    private CiistSmallTitle travel_meishi_title;//美食标题
    private CiistSmallTitle travel_techan_title;//特产标题
    private CiistSmallTitle travel_jingxuan_title;//精选攻略标题
    private RefreshableView refreshableView;//刷新控件
    private ScrollView scrollView;//滚动控件

    private ArrayList<String> mImgSliderData = new ArrayList<String>();//滚动图片地址数据
    private ArrayList<ItemInfo> mItemList = new ArrayList<ItemInfo>();//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_travel);
        context = getApplicationContext();

        getIntentExtra();//接收值
        initView();//初始化

        getLunboData(LUNBOIMG);//轮播
        getFengJingData(FENGJING);//风景
        getGuangGaoData(GUANGGAO);//广告
        getZhusuData(ZHUSU);  //住宿数据
        getMeishiData(MEISHI);//美食
        getTechanData(TECHAN);//特产
        getJingXuanData(GONGLUE);//精选
    }

    /**
     * 接收传递的值
     */
    private void getIntentExtra(){
        Intent intent = this.getIntent();
        mSubjectCode = intent.getStringExtra("subjectcode");
        mSubjectName = intent.getStringExtra("subjectname");
        mIsDuban = intent.getBooleanExtra("IsDuban", false);
        mIdentify = intent.getStringExtra("identify");
        mUserName = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");
        mGPS = intent.getIntExtra("GPS", 1);
        mGIS = intent.getIntExtra("GIS", 0);
    }

    /**
     * 初始化
     */
    private void initView() {
        findViewById(R.id.trave_jingqu_part_img).setOnClickListener(this);
        findViewById(R.id.trave_zhusu_part_img).setOnClickListener(this);
        findViewById(R.id.trave_meishi_part_img).setOnClickListener(this);
        findViewById(R.id.trave_techan_part_img).setOnClickListener(this);

        travel_jingqutiejian_title=(CiistSmallTitle)findViewById(R.id.travel_jingqu_title);
        travel_zhusu_title=(CiistSmallTitle)findViewById(R.id.travel_zhushu_title);
        travel_meishi_title=(CiistSmallTitle)findViewById(R.id.travel_meishi_title);
        travel_techan_title=(CiistSmallTitle)findViewById(R.id.travel_techan_title);
        travel_jingxuan_title=(CiistSmallTitle)findViewById(R.id.travel_jingxuan_title);
        travel_title=(CiistTitleView)findViewById(R.id.travel_titleview);
        scrollView = (ScrollView) findViewById(R.id.travel_scorllview);

        scrollView.smoothScrollTo(0, 0);

        g_iv_1 = (ImageView) findViewById(R.id.travel_guanggao_iv1);
        g_iv_2 = (ImageView) findViewById(R.id.travel_guanggao_iv2);
        g_iv_3 = (ImageView) findViewById(R.id.travel_guanggao_iv3);
        g_iv_4 = (ImageView) findViewById(R.id.travel_guanggao_iv4);

        refreshableView = (RefreshableView) findViewById(R.id.Travel_refreshableView);
        refreshableView.setRefreshListener(this);

        travel_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//返回

        travel_title.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TravelActivity.this,SearchActivity.class));
            }
        });
        travel_jingqutiejian_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent5 = new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle5.putString("subjectname2","景区景点");
                bundle5.putString("subjectname", "景区景点");
                bundle5.putString("subjectcode", "5B35CC31-8C58-461B-A7EB-80613C28E021");
                bundle5.putBoolean("full", true);
                bundle5.putBoolean("hasTime",hasTime);
                tmpIntent5.putExtras(bundle5);
                startActivity(tmpIntent5);

            }
        });
        travel_zhusu_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent6= new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle6 = new Bundle();
                bundle6.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle6.putString("subjectname2","住宿");
                bundle6.putString("subjectname", "住宿");
                bundle6.putString("subjectcode", "AC17015F-8649-4959-B2D7-03B5C081F38F");
                bundle6.putBoolean("full", true);
                bundle6.putBoolean("hasTime",hasTime);
                tmpIntent6.putExtras(bundle6);
                startActivity(tmpIntent6);
            }
        });
        travel_meishi_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent7 = new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle7 = new Bundle();
                bundle7.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle7.putString("subjectname2","美食");
                bundle7.putString("subjectname", "美食");
                bundle7.putString("subjectcode", "2E42694A-E84B-4189-8E5E-3793D855B26A");
                bundle7.putBoolean("full", true);
                bundle7.putBoolean("hasTime",hasTime);
                tmpIntent7.putExtras(bundle7);
                startActivity(tmpIntent7);
            }
        });
        travel_techan_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent8 = new Intent(TravelActivity.this, CoverStyleActivity.class);
                Bundle bundle8 = new Bundle();
                bundle8.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle8.putString("subjectname2","特产");
                bundle8.putString("subjectname", "特产");
                bundle8.putString("subjectcode", "98DE6E2A-04FE-4F7B-9811-681E47027BB5");
                bundle8.putBoolean("full", true);
                bundle8.putBoolean("hasTime",hasTime);
                tmpIntent8.putExtras(bundle8);
                startActivity(tmpIntent8);
            }
        });
        travel_jingxuan_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent9 = new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle9 = new Bundle();
                bundle9.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle9.putString("subjectname2","精选攻略");
                bundle9.putString("subjectname", "精选攻略");
                bundle9.putString("subjectcode", GONGLUE);
                bundle9.putBoolean("full", true);
                bundle9.putBoolean("hasTime",hasTime);
                tmpIntent9.putExtras(bundle9);
                startActivity(tmpIntent9);
            }
        });
        initFengJingView();
        mGridView = (IGridView) findViewById(R.id.trave_zhusu_gridview);  //zhusu part
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo info = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(info);
            }
        });
        mGridAdapter = new IGridViewAdapter(context);
        mGridView.setAdapter(mGridAdapter);

        listView = (ScrollListView) findViewById(R.id.meishi_content_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo info = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(info);
            }
        });
        mListViewAdapter = new TravelMeishiAdapter(context);
        listView.setAdapter(mListViewAdapter);


        mRecyclerView = (RecyclerView) findViewById(R.id.travel_techan_recyclerView); //横向滚动特产
        IRecyclerViewManger linearLayoutManager = new IRecyclerViewManger(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new TravelTechanAdapter(context);
        mRecyclerViewAdapter.setOnItemClickLitener(new TravelTechanAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemInfo info = mRecyclerViewAdapter.getmDatas().get(position);
                startNextAct(info);
            }
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);


        mJingxuanList = (ScrollListView) findViewById(R.id.travel_jingxuan_listview); //jingxuan part
        mJingxuanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo info = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(info);
            }
        });
        mJingxuanAdapter = new TravelJingxuanAdapter(context);
        mJingxuanList.setAdapter(mJingxuanAdapter);
    }

    /**
     * 广告跳转部分
     */
    private void enterGuangGao(final List<ItemInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            final int positon = i;
            g_iv_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            g_iv_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            g_iv_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            g_iv_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
        }


    }
    /**
     * fengjing view
     */
    private void initFengJingView() {
        iv_1 = (ImageView) findViewById(R.id.travel_fengjin_iv1);
        iv_2 = (ImageView) findViewById(R.id.travel_fengjin_iv2);
        iv_3 = (ImageView) findViewById(R.id.travel_fengjin_iv3);
        iv_4 = (ImageView) findViewById(R.id.travel_fengjin_iv4);
        tv_1 = (TextView) findViewById(R.id.travel_fengjin_tv1);
        tv_2 = (TextView) findViewById(R.id.travel_fengjin_tv2);
        tv_3 = (TextView) findViewById(R.id.travel_fengjin_tv3);
        tv_4 = (TextView) findViewById(R.id.travel_fengjin_tv4);
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trave_jingqu_part_img: //景区
                Intent tmpIntent1 = new Intent(TravelActivity.this, CoverStyleActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle1.putString("subjectname2","景区景点");
                bundle1.putString("subjectname", "景区景点");
                bundle1.putString("subjectcode", "5B35CC31-8C58-461B-A7EB-80613C28E021");
                bundle1.putBoolean("full", true);
                bundle1.putBoolean("hasTime",hasTime);
                tmpIntent1.putExtras(bundle1);
                startActivity(tmpIntent1);
                break;
            case R.id.trave_zhusu_part_img:  //住宿
                Intent tmpIntent2 = new Intent(TravelActivity.this, CoverStyleActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle2.putString("subjectname2","住宿");
                bundle2.putString("subjectname", "住宿");
                bundle2.putString("subjectcode", "AC17015F-8649-4959-B2D7-03B5C081F38F");
                bundle2.putBoolean("full", true);
                bundle2.putBoolean("hasTime",hasTime);
                tmpIntent2.putExtras(bundle2);
                startActivity(tmpIntent2);
                break;
            case R.id.trave_meishi_part_img:  //美食
                Intent tmpIntent3 = new Intent(TravelActivity.this, CoverStyleActivity.class);
                Bundle bundle3 = new Bundle();
                bundle3.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle3.putString("subjectname2","美食");
                bundle3.putString("subjectname", "美食");
                bundle3.putString("subjectcode", "2E42694A-E84B-4189-8E5E-3793D855B26A");
                bundle3.putBoolean("full", true);
                bundle3.putBoolean("hasTime",hasTime);
                tmpIntent3.putExtras(bundle3);
                startActivity(tmpIntent3);
                break;
            case R.id.trave_techan_part_img: //特产
                Intent tmpIntent4 = new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle4 = new Bundle();
                bundle4.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle4.putString("subjectname2","特产");
                bundle4.putString("subjectname", "特产");
                bundle4.putString("subjectcode", "98DE6E2A-04FE-4F7B-9811-681E47027BB5");
                bundle4.putBoolean("full", true);
                bundle4.putBoolean("hasTime",hasTime);
                tmpIntent4.putExtras(bundle4);
                startActivity(tmpIntent4);
                break;
        }
    }

    /**
     * get fengjing data 风景
     *
     * @param subjectCode
     */
    private void getFengJingData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/4";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                    for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }
                    //    mGridAdapter.addData(itemList);
                    if (itemList.size() < 4){
                        return;
                    }
                    setFengJinItemData(itemList.get(0), iv_1, tv_1);
                    setFengJinItemData(itemList.get(1), iv_2, tv_2);
                    setFengJinItemData(itemList.get(2), iv_3, tv_3);
                    setFengJinItemData(itemList.get(3), iv_4, tv_4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * set fengjin part  风景
     *
     * @param info ItemInfo
     * @param iv   ImageView
     * @param tv   TextView
     */
    private void setFengJinItemData(final ItemInfo info, ImageView iv, TextView tv) {
       // Hutils.LoadImage(info.getImgsrc(), iv);
        Picasso.with(context).load(info.getImgsrc()).placeholder(R.drawable.default_bg_pic).into(iv);
        tv.setText(NetUtil.IsubString(info.getTitle(), 8));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextAct(info);
            }
        });
    }

    /**
     * get lunbo data 轮播数据
     *
     * @param subjectCode
     */
    private void getLunboData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/6";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                        mImgSliderData.add(itemInfo.getImgsrc());
                        mItemList.add(itemInfo);
                    }

                    /*for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }*/
                    //mGridAdapter.addData(itemList);
                    getXlistViewHead(mImgSliderData);  //头部图片轮播。

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    /**
     * get zhusu data 住宿
     *
     * @param subjectCode
     */
    private void getZhusuData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/6";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                    /*for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }*/
                    mGridAdapter.addData(itemList);
                    scrollView.smoothScrollTo(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * get meishi data 美食
     *
     * @param subjectCode
     */
    private void getMeishiData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/4";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }
                    mListViewAdapter.addData(itemList);
                    scrollView.smoothScrollTo(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * get techan data 特产
     *
     * @param subjectCode
     */
    private void getTechanData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/8";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                    Log.e("test", "----getTechanData-----");

                   /* for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }*/
                    mRecyclerViewAdapter.addData(itemList);
                    scrollView.smoothScrollTo(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * get jingxuan data 精选
     *
     * @param subjectCode
     */
    private void getJingXuanData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/2";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                    /*for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }*/
                    mJingxuanAdapter.addData(itemList);
                    scrollView.smoothScrollTo(0, 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * set slider data 图片轮播。
     *
     * @return
     */
    public void getXlistViewHead(/*String[] data*/ List<String> data) {
        SliderLayout mSliderLayout = (SliderLayout) findViewById(R.id.image_slider_layout);
        int length = data.size();
        mSliderLayout.removeAllSliders();  //移除
        for (int i = 0; i < length; i++) {
            TextSliderView sliderView = new TextSliderView(TravelActivity.this);   //向SliderLayout中添加控件
            sliderView.image(data.get(i));
            final ItemInfo info = mItemList.get(i);
            //    sliderView.description("本栏目有马龙县旅游局承办");
            sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //Toast.makeText(TravelActivity.this, "中国加剧芯片产业并购潮 砸钱模式难以理解", Toast.LENGTH_SHORT).show();
                    startNextAct(info);
                }
            });

            mSliderLayout.addSlider(sliderView);
        }

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);  //将小圆点设置到右下方
    }


    /**
     * get guanggao data 广告
     *
     * @param subjectCode
     */
    private void getGuangGaoData(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/5";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(context, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
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
                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                    // Log.e("test", "----getTechanData-----");

                   /* for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                        Log.d("test", o.getImgsrc());
                    }*/
                    cleanGuangGao();
                    //设置广告位置
                    for (i = 0; i < itemList.size(); i++) {
                        String position = itemList.get(i).getBak1();
                        if (position.equals("1")) {
                         //   Hutils.LoadImage(itemList.get(i).getImgsrc(), g_iv_1);
                            Picasso.with(context)
                                    .load(itemList.get(i).getImgsrc())
                                    .placeholder(R.drawable.default_bg_pic)
                                    .into(g_iv_1);
                            g_iv_1.setVisibility(View.VISIBLE);
                        } else if (position.equals("2")) {
                        //    Hutils.LoadImage(itemList.get(i).getImgsrc(), g_iv_2);
                            Picasso.with(context)
                                    .load(itemList.get(i).getImgsrc())
                                    .placeholder(R.drawable.default_bg_pic)
                                    .into(g_iv_2);
                            g_iv_2.setVisibility(View.VISIBLE);
                        } else if (position.equals("3")) {
                         //   Hutils.LoadImage(itemList.get(i).getImgsrc(), g_iv_3);
                            Picasso.with(context)
                                    .load(itemList.get(i).getImgsrc())
                                    .placeholder(R.drawable.default_bg_pic)
                                    .into(g_iv_3);
                            g_iv_3.setVisibility(View.VISIBLE);
                        } else if (position.equals("4")) {
                        //    Hutils.LoadImage(itemList.get(i).getImgsrc(), g_iv_4);
                            Picasso.with(context)
                                    .load(itemList.get(i).getImgsrc())
                                    .placeholder(R.drawable.default_bg_pic)
                                    .into(g_iv_4);
                            g_iv_4.setVisibility(View.VISIBLE);
                        }
                    }
                    enterGuangGao(itemList);
                    scrollView.smoothScrollTo(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 清除广告
     */
    private void cleanGuangGao() {
        g_iv_1.setVisibility(View.GONE);
        g_iv_2.setVisibility(View.GONE);
        g_iv_3.setVisibility(View.GONE);
        g_iv_4.setVisibility(View.GONE);
    }

    /**
     * enter next activity
     *
     * @param info
     */
    private void startNextAct(ItemInfo info) {
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(context, WebActivity.class);
            tmpIntent.putExtra("URL", info.getLinkurl());
            tmpIntent.putExtra("TITLE", info.getTitle());
            tmpIntent.putExtra("img_url",info.getImgsrc());
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
                Intent tmpIntent = new Intent(TravelActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", info.getTitle());

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
     * 下拉刷新界面
     * @param view
     */
    @Override
    public void onRefresh(RefreshableView view) {
        mImgSliderData.clear();//清除
        getLunboData(LUNBOIMG);//轮播
        getFengJingData(FENGJING);//风景
        scrollView.smoothScrollTo(0, 0);
        refreshableView.finishRefresh();
    }

    /**
     * GridView适配器
     */
    public class IGridViewAdapter extends BaseAdapter {
        private ArrayList<ItemInfo> data = new ArrayList<>();
        private LayoutInflater inflater;

        public IGridViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void addData(ArrayList<ItemInfo> mData) {
            this.data = mData;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                v = inflater.inflate(R.layout.travel_gridview_item_layout, null);
                holder.titleTxt = (TextView) v.findViewById(R.id.content_one_right_titletxt);
                holder.moneyTxt = (TextView) v.findViewById(R.id.content_one_right_pricetxt);
                holder.merchantxt = (TextView) v.findViewById(R.id.content_one_right_merchantxt);
                holder.imageView = (ImageView) v.findViewById(R.id.content_one_right_img);
                v.setTag(holder);
            } else {
                v = convertView;
                holder = (Holder) v.getTag();
            }
            ItemInfo itemdata = data.get(position);
            holder.titleTxt.setText(NetUtil.IsubString((String) itemdata.getTitle(), 8));
            holder.moneyTxt.setText(NetUtil.IsubString((String) itemdata.getBak1(), 4));
            holder.merchantxt.setText(NetUtil.IsubString((String) itemdata.getBak2(), 7));
            /*AsyncImageLoader loader = new AsyncImageLoader("zhusu");
            loader.LoadImage(itemdata.getImgsrc(),holder.imageView);*/
           // Hutils.LoadImage(itemdata.getImgsrc(), holder.imageView);
            Picasso.with(TravelActivity.this)
                    .load(itemdata.getImgsrc())
                    .placeholder(R.drawable.default_bg_pic)
                    .into(holder.imageView);
            return v;
        }

        class Holder {
            TextView titleTxt, moneyTxt, merchantxt;
            ImageView imageView;
        }
    }


}
