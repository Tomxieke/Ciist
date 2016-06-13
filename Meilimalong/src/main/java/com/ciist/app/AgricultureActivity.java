package com.ciist.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
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
import com.hw.ciist.util.Hutils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.scxh.slider.library.SliderLayout;
import com.scxh.slider.library.SliderTypes.BaseSliderView;
import com.scxh.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AgricultureActivity extends Activity implements RefreshableView.RefreshListener {

    /**
     * 农业按钮url
     */
    private static final String GE_NONGYE_CODE = "EA881484-5A36-48A6-A40C-1FC9C9B6E3C9";
    private Context mContext;
    private boolean mIsDuban = false;


    private GISLocation mCurrentLocation = new GISLocation();

    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";

    private ScrollView agriculture_src;
    private ProgressBar agriculturesrc_pro;
    private RefreshableView agriculturesrc_refre;

    private IGridView agriculturesrc_grid;

    private CiistIGridviewAdapter girdviewAdapter;

    private CiistTitleView agriculturesrc_titleview;

    private CiistSmallTitle agriculturesrc_nongyekuaixun;
    private CiistSmallTitle agriculturesrc_tesechanye;
    private CiistSmallTitle agriculturesrc_zhengcefuchi;
    private CiistSmallTitle agriculturesrc_nongchanpinggongying;
    private CiistSmallTitle agriculturesrc_nongyejishu;
    private CiistSmallTitle agriculturesrc_chanyedongtai;

    private ScrollListView nongyekuaixun_list;
    private ScrollListView tesechanye_list;
    private ScrollListView zhengcefuchi_list;
    private ScrollListView nongchanpinggongying_list;
    private ScrollListView nongyejishu_list;
    private ScrollListView chanyedongtai_list;

    private CiistAdapter nongyekuaixun_Adapter;
    private CiistAdapter tesechanye_Adapter;
    private CiistAdapter zhengcefuchi_Adapter;
    private CiistAdapter nongchanpinggongying_Adapter;
    private CiistAdapter nongyejishu_Adapter;
    private CiistAdapter chanyedongtai_Adapter;

    private static final int MSG_NYKX_SUCCESS = 107;//农业快讯
    private static final int MSG_TSCY_SUCCESS = 101;//特色产业
    private static final int MSG_ZCFC_SUCCESS = 102;//政策扶持
    private static final int MSG_NCPGY_SUCCESS = 103;//农产品供应
    private static final int MSG_NYJSCS_SUCCESS = 104;//农业技术措施
    private static final int MSG_CPDT_SUCCESS = 105;//产业动态
    private static final int MSG_REF_SUCCESS = 106;//刷新成功


    private boolean LoadData_tesechanye = false;
    private boolean isNet = false;


    /**
     * 农业快讯数据
     */
    private ArrayList<Hutils.Ciist_entity> nongyekuaixunData = new ArrayList<>();
    /**
     * 特色产业数据
     */
    private ArrayList<Hutils.Ciist_entity> tesechanyeData = new ArrayList<>();

    /**
     * 政策扶持数据
     */
    private ArrayList<Hutils.Ciist_entity> zhengcefuchiData = new ArrayList<>();

    /**
     * 农产品供应数据
     */
    private ArrayList<Hutils.Ciist_entity> nongchanpinggongyingData = new ArrayList<>();

    /**
     * 农业技术措施数据
     */
    private ArrayList<Hutils.Ciist_entity> nongyejishuData = new ArrayList<>();

    /**
     * 产业动态数据
     */
    private ArrayList<Hutils.Ciist_entity> chanpingdongtaiData = new ArrayList<>();


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TSCY_SUCCESS:
                    tesechanye_Adapter = new CiistAdapter(AgricultureActivity.this, tesechanyeData);
                    tesechanye_list.setAdapter(tesechanye_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);
                    break;

                case MSG_ZCFC_SUCCESS:
                    zhengcefuchi_Adapter = new CiistAdapter(AgricultureActivity.this, zhengcefuchiData);
                    zhengcefuchi_list.setAdapter(zhengcefuchi_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);
                    break;

                case MSG_NCPGY_SUCCESS:
                    nongchanpinggongying_Adapter = new CiistAdapter(AgricultureActivity.this, nongchanpinggongyingData);
                    nongchanpinggongying_list.setAdapter(nongchanpinggongying_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);
                    break;

                case MSG_NYJSCS_SUCCESS:
                    nongyejishu_Adapter = new CiistAdapter(AgricultureActivity.this, nongyejishuData);
                    nongyejishu_list.setAdapter(nongyejishu_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);

                    break;

                case MSG_CPDT_SUCCESS:
                    chanyedongtai_Adapter = new CiistAdapter(AgricultureActivity.this, chanpingdongtaiData);
                    chanyedongtai_list.setAdapter(chanyedongtai_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);
                    break;

                case MSG_NYKX_SUCCESS:
                    nongyekuaixun_Adapter = new CiistAdapter(AgricultureActivity.this, nongyekuaixunData);
                    nongyekuaixun_list.setAdapter(nongyekuaixun_Adapter);
                    agriculture_src.smoothScrollTo(0, 0);
                    break;

                case MSG_REF_SUCCESS:
                    agriculturesrc_refre.finishRefresh();
                    agriculture_src.smoothScrollTo(0, 0);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agriculture);
        mContext = getApplicationContext();
        NetUtil.hasNetWork(this);
        initview();
        getDataOfScrollAgricultureNews();
        onInitAfterLogic();
        getDataOfTeseChanye();
        getDataOfZhengceFuchi();
        getDataOfNongchanpingGongying();
        getDataOfNongyeJishu();
        getDataOfChanpingdongtai();
        getDataOfNongyeKuaixun();
    }

    /**
     * 控件初始化之后的逻辑操作
     */
    private void onInitAfterLogic() {
        tesechanyeData = new ArrayList<Hutils.Ciist_entity>();
        zhengcefuchiData = new ArrayList<Hutils.Ciist_entity>();
        nongchanpinggongyingData = new ArrayList<Hutils.Ciist_entity>();
        nongyejishuData = new ArrayList<Hutils.Ciist_entity>();
        chanpingdongtaiData = new ArrayList<Hutils.Ciist_entity>();
        nongyekuaixunData = new ArrayList<Hutils.Ciist_entity>();
    }

    /**
     * 初始化控件
     */
    private void initview() {

        agriculturesrc_refre = (RefreshableView) findViewById(R.id.agriculture_refresh);
        agriculture_src = (ScrollView) findViewById(R.id.agriculture_scr);
        agriculturesrc_pro = (ProgressBar) findViewById(R.id.agriculture_wait_Slider_pb);
        agriculturesrc_grid = (IGridView) findViewById(R.id.agriculture_Igrid_nongye);
        girdviewAdapter = new CiistIGridviewAdapter(this);
        girdviewAdapter.setType(CiistIGridviewAdapter.VERTICAL_TYPE);
        agriculturesrc_grid.setAdapter(girdviewAdapter);
        getGerenBanshi(GE_NONGYE_CODE);
        agriculturesrc_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemInfo itemInfo = (ItemInfo) parent.getAdapter().getItem(position);
                startNextAct(itemInfo);
            }
        });
        agriculturesrc_titleview = (CiistTitleView) findViewById(R.id.agriculture_titleview);
        agriculturesrc_titleview.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agriculturesrc_titleview.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgricultureActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        agriculturesrc_nongyekuaixun = (CiistSmallTitle) findViewById(R.id.agriculture_nongyekuaixun_title);
        agriculturesrc_nongyekuaixun.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "农业快讯");

                bundle.putString("subjectname", "农业快讯");
                bundle.putString("subjectcode", "265593FE-3F07-4165-A44A-CA8C788DC6B9");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        agriculturesrc_tesechanye = (CiistSmallTitle) findViewById(R.id.agriculture_tesechanye_title);
        agriculturesrc_tesechanye.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "特色产业");

                bundle.putString("subjectname", "特色产业");
                bundle.putString("subjectcode", "531433C2-3808-47D4-8990-13996225667F");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        agriculturesrc_zhengcefuchi = (CiistSmallTitle) findViewById(R.id.agriculture_zhengcefuchi_title);
        agriculturesrc_zhengcefuchi.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "政策扶持");

                bundle.putString("subjectname", "政策扶持");
                bundle.putString("subjectcode", "22D18FA3-9970-47D4-9A23-CFBEFF77C482");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });


        agriculturesrc_nongchanpinggongying = (CiistSmallTitle) findViewById(R.id.agriculture_nongchanpinggongying_title);
        agriculturesrc_nongchanpinggongying.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "农产品供应");
                bundle.putString("subjectname", "农产品供应");
                bundle.putString("subjectcode", "5D03B1E2-AFFC-4ED6-BBAF-8BA8CBA8005B");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });


        agriculturesrc_nongyejishu = (CiistSmallTitle) findViewById(R.id.agriculture_nongyejishu_title);
        agriculturesrc_nongyejishu.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "农业技术措施");
                bundle.putString("subjectname", "农业技术措施");
                bundle.putString("subjectcode", "BF822BD9-19EE-48AC-B8F0-FEEF1DABE11C");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });


        agriculturesrc_chanyedongtai = (CiistSmallTitle) findViewById(R.id.agriculture_chanpingdongtai_title);
        agriculturesrc_chanyedongtai.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(AgricultureActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                bundle.putString("subjectname2", "产业动态");
                bundle.putString("subjectname", "产业动态");
                bundle.putString("subjectcode", "39964517-4909-4505-8BE2-4F5EF0E8FEA0");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });
        nongyekuaixun_list = (ScrollListView) findViewById(R.id.agriculture_nongyekuaixun_list);
        tesechanye_list = (ScrollListView) findViewById(R.id.agriculture_tesechanye_list);
        zhengcefuchi_list = (ScrollListView) findViewById(R.id.agriculture_zhengcefuchi_list);
        nongchanpinggongying_list = (ScrollListView) findViewById(R.id.agriculture_nongchanpinggongying_list);
        nongyejishu_list = (ScrollListView) findViewById(R.id.agriculture_nongyejishu_list);
        chanyedongtai_list = (ScrollListView) findViewById(R.id.agriculture_chanpingdongtai_list);

        nongyekuaixun_list.setOnItemClickListener(OC);
        tesechanye_list.setOnItemClickListener(OC);
        zhengcefuchi_list.setOnItemClickListener(OC);
        nongchanpinggongying_list.setOnItemClickListener(OC);
        nongyejishu_list.setOnItemClickListener(OC);
        chanyedongtai_list.setOnItemClickListener(OC);

    }

    /**
     * 列表监听
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
                    bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
                    bundle.putString("subjectname2",ce.Title);


                    bundle.putString("subjectname", ce.Title);
                    bundle.putString("subjectcode", ce.Remark5);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        }


    };


    /**
     * 滚动监听
     */
    private void getViewHeadOfScrollAgricultureNews(ArrayList<ItemInfo> list) {
        SliderLayout mSliderLayout = (SliderLayout) agriculture_src.findViewById(R.id.agriculture_image_slider_layout);
        int length = list.size();
        mSliderLayout.removeAllSliders(); //移除原有数据
        for (int i = 0; i < length; i++) {
            final ItemInfo info = list.get(i);
            TextSliderView sliderView = new TextSliderView(AgricultureActivity.this);   //向SliderLayout中添加控件
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
                    tmpIntent.putExtra("ROOT", "#Content");
                    tmpIntent.putExtra("img_url",info.getImgsrc());
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
     * 获取滚动数据
     */
    private void getDataOfScrollAgricultureNews() {
        String urlPath = ServerInfo.GetInfoPre + "6EA8EB3F-BF9E-4CB7-8F8B-832378A77B73/1/10";  //获取地址
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlPath, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(AgricultureActivity.this, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
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
                    getViewHeadOfScrollAgricultureNews(listDate);
                    agriculturesrc_pro.setVisibility(View.GONE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 获取农业GirdView数据
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
//                    for (ItemInfo o : itemList) {
//                        Log.d("test", o.getTitle());
//                        Log.d("test", o.getImgsrc());
//                    }

                    girdviewAdapter.setData(itemList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 农业GirdView跳转
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
                Intent tmpIntent = new Intent(AgricultureActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird", getResources().getColor(R.color.lvsetitle));
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
     * 获取农业快讯数据
     */
    private void getDataOfNongyeKuaixun() {
        agriculture_src.smoothScrollTo(0, 0);
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {
                        String InformationPath = ServerInfo.GetInfoPre
                                + "265593FE-3F07-4165-A44A-CA8C788DC6B9" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);
                        nongyekuaixunData.addAll(Hutils.parseJSONData(TodayMalong, null));
                    }
                    mhandler.sendEmptyMessage(MSG_NYKX_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }


    /**
     * 获取特色产业数据
     */
    private void getDataOfTeseChanye() {
        LoadData_tesechanye = true;
        agriculture_src.smoothScrollTo(0, 0);
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "531433C2-3808-47D4-8990-13996225667F" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        tesechanyeData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    LoadData_tesechanye = false;
                    isRefreashCompleted();
                    agriculture_src.smoothScrollTo(0, 0);
                    mhandler.sendEmptyMessage(MSG_TSCY_SUCCESS);


                } catch (Exception e) {
                    LoadData_tesechanye = false;
                    isRefreashCompleted();
                    agriculture_src.smoothScrollTo(0, 0);
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }

    /**
     * 获取政策扶持数据
     */
    private void getDataOfZhengceFuchi() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "22D18FA3-9970-47D4-9A23-CFBEFF77C482" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        zhengcefuchiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_ZCFC_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }

    /**
     * 获取农产品供应数据
     */
    private void getDataOfNongchanpingGongying() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "5D03B1E2-AFFC-4ED6-BBAF-8BA8CBA8005B" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        nongchanpinggongyingData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_NCPGY_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }

    /**
     * 获取农业技术措施数据
     */
    private void getDataOfNongyeJishu() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "BF822BD9-19EE-48AC-B8F0-FEEF1DABE11C" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        nongyejishuData.addAll(Hutils.parseJSONData(TodayMalong, null));
                        for (int i = 0; i < nongyejishuData.size(); i++) {
                            Log.e("test", nongyejishuData.get(i).Title);

                        }

                    }
                    mhandler.sendEmptyMessage(MSG_NYJSCS_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }

    /**
     * 获取产业动态数据
     */
    private void getDataOfChanpingdongtai() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "39964517-4909-4505-8BE2-4F5EF0E8FEA0" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        chanpingdongtaiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_CPDT_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }) {


        }.start();


    }

    public void isRefreashCompleted() {
        agriculture_src.smoothScrollTo(0, 0);
        if (LoadData_tesechanye == false) {
            mhandler.sendEmptyMessageDelayed(MSG_REF_SUCCESS, 3000);
            agriculture_src.smoothScrollTo(0, 0);
        }
    }

    @Override
    public void onRefresh(RefreshableView view) {
        if (tesechanyeData != null) tesechanyeData.clear();
        agriculture_src.smoothScrollTo(0, 0);
        getDataOfTeseChanye();
        agriculture_src.smoothScrollTo(0, 0);
    }
}
