package com.ciist.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistSmallTitle;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.IGridView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter;
import com.ciist.toolkits.CiistIGridviewAdapter;
import com.ciist.util.NetUtil;
import com.ciist.util.RefreshableView;
import com.ciist.util.SharedPreferenceHelper;
import com.hw.ciist.util.Hutils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ServiceActivity extends Activity implements RefreshableView.RefreshListener {
    private boolean hasTime = false;  //网页详情是否显示时间
    private GISLocation mCurrentLocation = new GISLocation();
    private boolean mIsDuban = false;

    //网络请求地址
    private String GetInfoPre = "http://211.149.212.154:2015/apps/";
    private static final String APPLICATION_JSON = "application/json";
    //  public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";

    private SharedPreferenceHelper sharedPreferenceHelper;

    private ImageView service_guanggao1;
    private ImageView service_guanggao2;
    private ImageView service_guanggao3;
    private ImageView service_guanggao4;
    private ImageView service_guanggao5;

    /**
     * 个人办事url
     */
    private static final String GE_REN_BAN_SHI_CODE = "F49868E9-0FE3-42B9-9E4A-2947CE5E5CB9";
    /**
     * 企业办事
     */
    private static final String QI_YE_BAN_SHI_CODE = "0ACD0635-2490-4114-95D5-32DFE9C38B68";

    private static final String GUANG_GAO = "04A5C06F-B79C-4A8A-8C73-0F7B8990D33D";

    private Context mContext;

    private RefreshableView service_refre;
    private ScrollView service_scr;

    private String mSubjectCode = "";

    SearchActivity dailog_weikaifa = new SearchActivity();//提示框

    private LinearLayout service_linear;

    private CiistTitleView bank_view;
    private IGridView service_ig_geren;
    private IGridView service_ig_qiye;
    private CiistSmallTitle service_zuixingonggao_title;
    //    private CiistSmallTitle service_redianfuwu_title;
    private CiistSmallTitle service_gerenbanli_title;
    private CiistSmallTitle service_qiyebanli_title;
//    private CiistSmallTitle service_banjiangonggao_title;

    private ScrollListView zuixingonggao_list;
//    private ScrollListView redianfuwu_list;
//    private ScrollListView banjianognggao_list;
//    private ProgressBar service_pro;
    /**
     * List适配器
     */

    private CiistAdapter zuixingonggaoAdapter;
//    private CiistAdapter redianfuwuAdapter;
//    private CiistAdapter banjiangonggaoAdapter;


    /**
     * GirdView适配器
     */
    private CiistIGridviewAdapter girdviewAdapter;
    private CiistIGridviewAdapter girdviewAdapterqiye;


    private static final int MSG_ZUIXIN_SUCCESS = 101;
    //    private static final int MSG_REDIAN_SUCCESS = 102;
//    private static final int MSG_BANJIAN_SUCCESS = 103;
    private static final int MSG_REF_SUCCESS = 104;
    private static final int MSG_SHOUQUAN_S = 105;
    private static final int MSG_SHOUQUAN_F = 106;
    /**
     * 最新公告数据
     */
    private ArrayList<Hutils.Ciist_entity> zuixinData = new ArrayList<>();
    /**
     * 热点服务排行数据
     */
//    private ArrayList<Hutils.Ciist_entity> redianData = new ArrayList<>();
    /**
     * 办件公告数据
     */
//    private ArrayList<Hutils.Ciist_entity> banjianData = new ArrayList<>();
    /**
     * 个人办事数据
     */
    private ArrayList<Hutils.Ciist_entity> gerenbanshiData = new ArrayList<>();
    private boolean isNet;

    private boolean LoadData_ZuixinGonggao = false;
    private boolean LoadData_ReDianfuwu = false;
    private boolean LoadData_BanJiangonggao = false;


    int page = 1;
    int tiaoshu = 5;

    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ZUIXIN_SUCCESS:
                    zuixingonggaoAdapter = new CiistAdapter(ServiceActivity.this, zuixinData);
                    zuixingonggao_list.setAdapter(zuixingonggaoAdapter);
                    service_scr.smoothScrollTo(0, 0);
                    break;
//                case MSG_REDIAN_SUCCESS:
//                    redianfuwuAdapter = new CiistAdapter(ServiceActivity.this, redianData);
//                    redianfuwu_list.setAdapter(redianfuwuAdapter);
//                    service_scr.smoothScrollTo(0, 0);
//                    break;
//                case MSG_BANJIAN_SUCCESS:
//                    banjiangonggaoAdapter = new CiistAdapter(ServiceActivity.this, banjianData);
//                    banjianognggao_list.setAdapter(banjiangonggaoAdapter);
//                    service_scr.smoothScrollTo(0, 0);
//                    break;
                case MSG_REF_SUCCESS:
                    service_refre.finishRefresh();
                    service_scr.smoothScrollTo(0, 0);
                    break;
                case MSG_SHOUQUAN_F:
                    startActivity(new Intent(ServiceActivity.this, SignActivity.class));
                    break;
                case MSG_SHOUQUAN_S:
                    startActivity(new Intent(ServiceActivity.this, PersonalCenterActivity.class));

                    break;

            }

        }

        ;
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        mContext = getApplicationContext();
        NetUtil.hasNetWork(this);  //检查网络，没有网络弹出提示对话框
        initView();
        getDataOfZuixinGongGao();
//        getDataOfRedianfuwu();
//        getDataOfBanjiangonggao();
        onInitAfterLogic();
        onClickButton();
        getGuangGao(GUANG_GAO);
        sharedPreferenceHelper = SharedPreferenceHelper.getInstance(this);
    }


    /**
     * 刷新数据
     */
    @Override
    public void onRefresh(RefreshableView view) {
        if (zuixinData != null) zuixinData.clear();
//        if(redianData!=null)redianData.clear();
//        if(banjianData!=null)banjianData.clear();
        service_scr.smoothScrollTo(0, 0);
//        getQiYeBanshi(QI_YE_BAN_SHI_CODE);
//        getGerenBanshi(GE_REN_BAN_SHI_CODE);
        getDataOfZuixinGongGao();
//        getDataOfRedianfuwu();
//        getDataOfBanjiangonggao();
        service_scr.smoothScrollTo(0, 0);


    }


    private void onInitAfterLogic() {

        zuixinData = new ArrayList<Hutils.Ciist_entity>();
//        redianData = new ArrayList<Hutils.Ciist_entity>();
//        banjianData = new ArrayList<Hutils.Ciist_entity>();
    }

    private void isRefreashCompleted() {
        service_scr.smoothScrollTo(0, 0);
        if (LoadData_ZuixinGonggao == false) {
            mhandler.sendEmptyMessageDelayed(MSG_REF_SUCCESS, 3000);
            service_scr.smoothScrollTo(0, 0);
        }
    }

    /**
     * 初始化ID
     */
    private void initView() {

        service_linear = (LinearLayout) findViewById(R.id.service_linear);
//        service_pro=(ProgressBar)findViewById(R.id.service_pro);
        bank_view = (CiistTitleView) findViewById(R.id.service_titleview);

        service_gerenbanli_title = (CiistSmallTitle) findViewById(R.id.service_gerenbanli_title);
        service_qiyebanli_title = (CiistSmallTitle) findViewById(R.id.service_qiyebanli_title);

//        service_banjiangonggao_title = (CiistSmallTitle) findViewById(R.id.service_banjiangonggao_title);
//        service_banjiangonggao_title.setRightOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent tmpIntent = new Intent(ServiceActivity.this, CoverStyleActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
//                bundle.putString("subjectname2", "办件公告");
//                bundle.putString("subjectname", "办件公告");
//                bundle.putString("subjectcode", "D6FD86E3-92A7-4BBF-8F0D-87979F6A81E5");
//                bundle.putBoolean("full", true);
//                tmpIntent.putExtras(bundle);
//                startActivity(tmpIntent);
//            }
//        });


//        service_redianfuwu_title = (CiistSmallTitle) findViewById(R.id.service_redianfuwupaihang_title);

        service_zuixingonggao_title = (CiistSmallTitle) findViewById(R.id.service_zuixingonggao_title);
        service_zuixingonggao_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(ServiceActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
                bundle.putString("subjectname2", "最新公告");
                bundle.putString("subjectname", "最新公告");
                bundle.putString("subjectcode", "A8978B6C-E0C4-4CB2-B8A3-BD473369C61E");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        /**
         * 广告模式
         */

        service_guanggao1 = (ImageView) findViewById(R.id.service_guanggao1);


        service_guanggao2 = (ImageView) findViewById(R.id.service_guanggao2);


        service_guanggao3 = (ImageView) findViewById(R.id.service_guanggao3);


        service_guanggao4 = (ImageView) findViewById(R.id.service_guanggao4);


        service_guanggao5 = (ImageView) findViewById(R.id.service_guanggao5);


        zuixingonggao_list = (ScrollListView) findViewById(R.id.service_zuixingonggao_list);
//        redianfuwu_list = (ScrollListView) findViewById(R.id.service_redianfuwupaihang_list);
//        banjianognggao_list = (ScrollListView) findViewById(R.id.service_banjiangongao_list);

        service_ig_geren = (IGridView) findViewById(R.id.service_Igrid_geren);
        girdviewAdapter = new CiistIGridviewAdapter(this);
        service_ig_geren.setAdapter(girdviewAdapter);
        getGerenBanshi(GE_REN_BAN_SHI_CODE);
        service_ig_geren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo itemInfo = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(itemInfo);
            }
        });

        service_ig_qiye = (IGridView) findViewById(R.id.service_Igrid_qiye);
        girdviewAdapterqiye = new CiistIGridviewAdapter(this);
        service_ig_qiye.setAdapter(girdviewAdapterqiye);
        getQiYeBanshi(QI_YE_BAN_SHI_CODE);
        service_ig_qiye.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo itemInfo = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(itemInfo);
            }
        });


        service_refre = (RefreshableView) findViewById(R.id.service_refresh);
        service_refre.setRefreshListener(this);
        service_scr = (ScrollView) findViewById(R.id.service_scr);

//        redianfuwu_list.setOnItemClickListener(OC);
        zuixingonggao_list.setOnItemClickListener(OC1);
//        banjianognggao_list.setOnItemClickListener(OC2);
        bank_view.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        bank_view.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new shouquanThread().start();
                startActivity(new Intent(ServiceActivity.this,ServiceSearchActivity.class));
            }
        });
    }


    /**
     * 热点服务排行监听
     */
    AdapterView.OnItemClickListener OC = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Hutils.Ciist_entity ce = (Hutils.Ciist_entity) parent.getAdapter().getItem(position);
            String linkType = ce.Remark4;
            String subjectLink = ce.Remark5;
            if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                Intent tmpIntent = new Intent(mContext, WebActivity.class);
                tmpIntent.putExtra("URL", ServerInfo.InfoDetailPath + ce.Info_Ids);
                tmpIntent.putExtra("TITLE", ce.Title);
                tmpIntent.putExtra("PUBDATE", ce.pubDate);
                tmpIntent.putExtra("source", ce.Sourse);
                tmpIntent.putExtra("author", ce.Author);
                tmpIntent.putExtra("img_url",ce.Image);
                tmpIntent.putExtra("infotype", ce.Type);
                tmpIntent.putExtra("ROOT", "#Content");
                tmpIntent.putExtra("hasTime",hasTime);
                tmpIntent.putExtra("blank", false);
                startActivity(tmpIntent);

            } else {
                if (linkType != null && linkType == "ciist1") {
                } else {

                    Intent tmpIntent = new Intent(mContext,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", ce.Title);

                    bundle.putString("subjectname", ce.Title);
                    bundle.putString("subjectcode", ce.Remark5);
                    bundle.putBoolean("hasTime",hasTime);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        }


    };
    /**
     * 最新公告排行监听
     */
    AdapterView.OnItemClickListener OC1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Hutils.Ciist_entity ce = (Hutils.Ciist_entity) parent.getAdapter().getItem(position);
            String linkType = ce.Remark4;
            String subjectLink = ce.Remark5;
            if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                Intent tmpIntent = new Intent(mContext, WebActivity.class);
                tmpIntent.putExtra("URL", ServerInfo.InfoDetailPath + ce.Info_Ids);
                tmpIntent.putExtra("TITLE", ce.Title);
                tmpIntent.putExtra("PUBDATE", ce.pubDate);
                tmpIntent.putExtra("source", ce.Sourse);
                tmpIntent.putExtra("img_url",ce.Image);
                tmpIntent.putExtra("author", ce.Author);
                tmpIntent.putExtra("infotype", ce.Type);
                tmpIntent.putExtra("ROOT", "#Content");
                tmpIntent.putExtra("blank", false);
                startActivity(tmpIntent);

            } else {
                if (linkType != null && linkType == "ciist1") {
                } else {

                    Intent tmpIntent = new Intent(mContext,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", ce.Title);


                    bundle.putString("subjectname", ce.Title);
                    bundle.putString("subjectcode", ce.Remark5);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        }
    };
    /**
     * 办件公告监听
     */
    AdapterView.OnItemClickListener OC2 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Hutils.Ciist_entity ce = (Hutils.Ciist_entity) parent.getAdapter().getItem(position);
            String linkType = ce.Remark4;
            String subjectLink = ce.Remark5;
            if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                Intent tmpIntent = new Intent(mContext, WebActivity.class);
                tmpIntent.putExtra("URL", ServerInfo.InfoDetailPath + ce.Info_Ids);
                tmpIntent.putExtra("TITLE", ce.Title);
                tmpIntent.putExtra("PUBDATE", ce.pubDate);
                tmpIntent.putExtra("img_url",ce.Image);
                tmpIntent.putExtra("source", ce.Sourse);
                tmpIntent.putExtra("author", ce.Author);
                tmpIntent.putExtra("infotype", ce.Type);
                tmpIntent.putExtra("ROOT", "#Content");
                tmpIntent.putExtra("blank", false);
                startActivity(tmpIntent);

            } else {
                if (linkType != null && linkType == "ciist1") {
                } else {

                    Intent tmpIntent = new Intent(mContext,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird",getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", ce.Title);

                    bundle.putString("subjectname", ce.Title);
                    bundle.putString("subjectcode", ce.Remark5);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        }
    };

    /**
     * 获取最新公告数据
     */
    private void getDataOfZuixinGongGao() {
        LoadData_ZuixinGonggao = true;
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (isNet) {

                        String InformationPath = ServerInfo.GetInfoPre
                                + "A8978B6C-E0C4-4CB2-B8A3-BD473369C61E" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        zuixinData.addAll(Hutils.parseJSONData(TodayMalong, null));
                    }
                    LoadData_ZuixinGonggao = false;
                    isRefreashCompleted();
                    mhandler.sendEmptyMessage(MSG_ZUIXIN_SUCCESS);

                } catch (Exception e) {
                    LoadData_ZuixinGonggao = false;
                    isRefreashCompleted();

                }

            }
        }).start();


    }

    /**
     * 获取热点服务排行数据
     */
//    private void getDataOfRedianfuwu() {
////        LoadData_ReDianfuwu=true;
//        isNet = Hutils.getNetState(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (isNet) {
//
//
//                        String InformationPath = ServerInfo.GetInfoPre
//                                + "AA153C94-AA8F-4A7A-AB60-5409BCED15C3" + "/" + 1 + "/"
//                                + 5;
//                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);
//
//                        redianData.addAll(Hutils.parseJSONData(TodayMalong, null));
//
//
//                    }
////                    LoadData_ReDianfuwu=false;
////                    isRefreashCompleted();
//                    mhandler.sendEmptyMessage(MSG_REDIAN_SUCCESS);
//
//
//                } catch (Exception e) {
////                    LoadData_ReDianfuwu=false;
////                    isRefreashCompleted();
//                    e.printStackTrace();
//                }
//            }
//        }) {
//
//
//        }.start();
//
//
//    }

    /**
     * 获取办件公告数据
     */
//    private void getDataOfBanjiangonggao() {
////        LoadData_BanJiangonggao=true;
//        isNet = Hutils.getNetState(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (isNet) {
//
//
//                        String InformationPath = ServerInfo.GetInfoPre
//                                + "D6FD86E3-92A7-4BBF-8F0D-87979F6A81E5" + "/" + 1 + "/"
//                                + 5;
//                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);
//
//                        banjianData.addAll(Hutils.parseJSONData(TodayMalong, null));
//
//
//                    }
////                    LoadData_BanJiangonggao=false;
////                    isRefreashCompleted();
//                    mhandler.sendEmptyMessage(MSG_BANJIAN_SUCCESS);
//
//
//                } catch (Exception e) {
////                    LoadData_BanJiangonggao=false;
////                    isRefreashCompleted();
//                    e.printStackTrace();
//                }
//            }
//        }) {
//
//
//        }.start();
//
//
//    }

    /**
     * 获取个人办事数据
     */
    private void getGerenBanshi(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/10";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mContext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                ItemInfo itemInfo = null;
                try {
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

                    girdviewAdapter.setData(itemList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 获取广告数据
     */
    private void getGuangGao(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/5";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mContext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
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

                    for (int i2 = 0; i2 < itemList.size(); i2++) {
                        Log.e("text", "----------" + itemList.get(i2).getTitle());

                    }


                    cleanGuangGao();
                    //设置广告位置
                    for (i = 0; i < itemList.size(); i++) {
                        String position = itemList.get(i).getBak1();
                        if (position.equals("1")) {
                            Hutils.LoadImage(itemList.get(i).getImgsrc(), service_guanggao1);
                            service_guanggao1.setVisibility(View.VISIBLE);
                        } else if (position.equals("2")) {
                            Hutils.LoadImage(itemList.get(i).getImgsrc(), service_guanggao2);
                            service_guanggao2.setVisibility(View.VISIBLE);
                        } else if (position.equals("3")) {
                            Hutils.LoadImage(itemList.get(i).getImgsrc(), service_guanggao3);
                            service_guanggao3.setVisibility(View.VISIBLE);
                        } else if (position.equals("4")) {
                            Hutils.LoadImage(itemList.get(i).getImgsrc(), service_guanggao4);
                            service_guanggao4.setVisibility(View.VISIBLE);
                        } else if (position.equals("5")) {
                            Hutils.LoadImage(itemList.get(i).getImgsrc(), service_guanggao5);
                            service_guanggao5.setVisibility(View.VISIBLE);
                        }
                    }
                    enterGuangGao(itemList);
                    service_scr.smoothScrollTo(0, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 广告跳转
     */
    private void enterGuangGao(final List<ItemInfo> data) {
        for (int i = 0; i < data.size(); i++) {
            final int positon = i;
            service_guanggao1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            service_guanggao2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            service_guanggao3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            service_guanggao4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
            service_guanggao5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNextAct(data.get(positon));
                }
            });
        }


    }


    private void cleanGuangGao() {
        service_guanggao1.setVisibility(View.GONE);
        service_guanggao2.setVisibility(View.GONE);
        service_guanggao3.setVisibility(View.GONE);
        service_guanggao4.setVisibility(View.GONE);
        service_guanggao5.setVisibility(View.GONE);
    }


    /**
     * 获取企业办事数据
     */
    private void getQiYeBanshi(String subjectCode) {
        String _url = ServerInfo.GetInfoPre + subjectCode + "/" + "1" + "/20";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mContext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                ItemInfo itemInfo = null;
                try {
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
//                    for (ItemInfo o : itemList) {
//                        Log.d("test", o.getTitle());
//                        Log.d("test", o.getImgsrc());
//                    }

                    girdviewAdapterqiye.setData(itemList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }




    /**
     * 跳转页面
     */
    private void startNextAct(ItemInfo info) {
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(mContext, WebActivity.class);
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
                Intent tmpIntent = new Intent(ServiceActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
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
     * 监听事件
     */
    private void onClickButton() {
        try {
            RelativeLayout service_xinxigongkai = (RelativeLayout) findViewById(R.id.service_xinxigongkai_rela);
            service_xinxigongkai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(ServiceActivity.this, LeaderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", "信息公开");
                    bundle.putString("subjectname", "信息公开服务");
                    bundle.putString("subjectcode", "4602DB61-F580-4FE7-8E33-E4A7492F0EC2");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",hasTime);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            });

//            RelativeLayout service_wangshangshenqing = (RelativeLayout) findViewById(R.id.service_wangshangshenqing_rela);
//            service_wangshangshenqing.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(ServiceActivity.this, MenuActivity.class));
//                }
//            });

//            RelativeLayout service_wangshangyuyue = (RelativeLayout) findViewById(R.id.service_wangshangyuyue_rela);
//            service_wangshangyuyue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NotificationDialog dialog = new NotificationDialog(ServiceActivity.this, R.style.add_dialog);
//                    dialog.setTitle("提示");
//                    dialog.setContent("此服务已关闭");
//                    dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
//                    dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
//                    dialog.show();
//                    startActivity(new Intent(ServiceActivity.this,AppointmentActivity.class));
//                }
//            });

//            RelativeLayout service_wangshangzixun = (RelativeLayout) findViewById(R.id.service_net_zixun_rela);
//            service_wangshangzixun.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NotificationDialog dialog = new NotificationDialog(ServiceActivity.this, R.style.add_dialog);
//                    dialog.setTitle("提示");
//                    dialog.setContent("此服务已关闭");
//                    dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
//                    dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
//                    dialog.show();
//                    startActivity(new Intent(ServiceActivity.this,CousultActivity.class));
//                }
//            });

//            RelativeLayout service_banjianchaxun = (RelativeLayout) findViewById(R.id.service_banjianchaxun_rela);
//            service_banjianchaxun.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(ServiceActivity.this, BanjianChaxunActivity.class));
//                }
//            });

            RelativeLayout service_search=(RelativeLayout)findViewById(R.id.service_layout_search);
            service_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ServiceActivity.this, ServiceSearchActivity.class));
                }
            });

            RelativeLayout service_yijianzhengji = (RelativeLayout) findViewById(R.id.service_yijianzhengji_rela);
            service_yijianzhengji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ServiceActivity.this, YiJianActivity.class

                    ));
                }
            });

//            RelativeLayout service_wangshangtoushu = (RelativeLayout) findViewById(R.id.service_wangshangtoushu_rela);
//            service_wangshangtoushu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(ServiceActivity.this, OnlineComplaintsActivity.class));
//                }
//            });

            RelativeLayout service_banshizhinan = (RelativeLayout) findViewById(R.id.service_banshizhinan_rela);
            service_banshizhinan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tmpIntent = new Intent(ServiceActivity.this,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", "办事指南");

                    bundle.putString("subjectname", "政务服务频道");
                    bundle.putString("subjectcode", "7321B837-347C-4AF0-8C67-75DBF56D4CC7");
                    bundle.putBoolean("full", true);
                    bundle.putBoolean("hasTime",hasTime);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);

                }
            });

            RelativeLayout service_falv = (RelativeLayout) findViewById(R.id.service_falv_rela);
            service_falv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent tmpIntent = new Intent(ServiceActivity.this,CoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("colorgird", getResources().getColor(R.color.hongsetitle));
                    bundle.putString("subjectname2", "政策法规");

                    bundle.putString("subjectname", "政策法规");
                    bundle.putString("subjectcode", "E13B65FA-9D38-4B50-9C24-CFBCC857280C");
                    bundle.putBoolean("full", true);
                    tmpIntent.putExtras(bundle);
                    bundle.putBoolean("hasTime",hasTime);
                    startActivity(tmpIntent);


                }
            });

//            RelativeLayout service_dianzijiancha = (RelativeLayout) findViewById(R.id.service_dianzijiancha_rela);
//            service_dianzijiancha.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    NotificationDialog dialog = new NotificationDialog(ServiceActivity.this, R.style.add_dialog);
//                    dialog.setTitle("提示");
//                    dialog.setContent("此服务已关闭");
//                    dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
//                    dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
//                    dialog.show();
//
//
//                }
//            });


//            ImageView kuaisutongdao = (ImageView) findViewById(R.id.service_kuaisufuwutongdao_img);
//            kuaisutongdao.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 用户授权信息判断
     */
    private class shouquanThread extends Thread {
        String resultStr = ""; //上传是否成功返回的结果

        @Override
        public void run() {
            HttpClient client = null;
            //    ResultInfo result = new ResultInfo();
            String _url = GetInfoPre + "ca/CheckPassport/ciistkey/" + getPasspotIdentif(sharedPreferenceHelper.getPassport("_passport"));

            Log.e("test", "-用户登录-_url---" + _url);
            //  RequestHeader _header, string phone,string validcode,string pwd,string nickname
            client=new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
//            _post.setHeader("Accept", APPLICATION_JSON);
//            _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
//            JSONObject jsonObject = new JSONObject();
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    resultStr = EntityUtils.toString(entity);
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
            if (resultStr == null || resultStr.equals( "")) {
                mhandler.sendEmptyMessage(MSG_SHOUQUAN_F);
            }



            else {
                mhandler.sendEmptyMessage(MSG_SHOUQUAN_S);

            }
            Log.e("test", "-用户登录-result---" + resultStr + "请求结束");

            //    Toast.makeText(MainActivity.this,"请求结束",Toast.LENGTH_SHORT).show();
           /* if (resultStr.substring(1,3).equalsIgnoreCase("OK")){
                mHandler.obtainMessage(POST_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(POST_FAILURE,result).sendToTarget();
            }*/

        }
    }

    private String getPasspotIdentif(String s) {
        String identify = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            identify = jsonObject.getString("identify");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return identify;

    }

    private String myIndetify;
    public void setIndetify(String indetify){
        this.myIndetify = indetify;

    }


}
