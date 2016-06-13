package com.ciist.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.ciist.customview.xlistview.CiistSmallTitle;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter;
import com.ciist.util.NetUtil;
import com.ciist.util.RefreshableView;
import com.hw.ciist.util.Hutils;

import java.util.ArrayList;

public class TradingHallActivity extends Activity implements RefreshableView.RefreshListener{

    private Context mContext;

    private RefreshableView trading_refre;
    private ScrollView trading_scr;

    private CiistTitleView trading_bank_view;

    private CiistSmallTitle trading_zhengfucaigou_title;
    private CiistSmallTitle trading_gongchengjianshe_title;
    private CiistSmallTitle trading_chanquanjiaoyi_title;
    private CiistSmallTitle trading_tudishenpi_title;
    private CiistSmallTitle trading_caikuangshenpi_title;

    private ScrollListView zhengfucaigou_list;
    private ScrollListView gongchengjianshe_list;
    private ScrollListView chanquanjiaoyi_list;
    private ScrollListView tudishenpi_list;
    private ScrollListView caikuangshenpi_list;

    private CiistAdapter zhengfucaigou_Adapter;
    private CiistAdapter gongchengjianshe_Adapter;
    private CiistAdapter chanquanjiaoyi_Adapter;
    private CiistAdapter tudishenpi_Adapter;
    private CiistAdapter caikuangshenpi_Adapter;

    private static final int MSG_ZFCG_SUCCESS = 101;
    private static final int MSG_GCJS_SUCCESS = 102;
    private static final int MSG_CQJY_SUCCESS = 103;
    private static final int MSG_TDSP_SUCCESS = 104;
    private static final int MSG_CKSP_SUCCESS = 105;
    private static final int MSG_REF_SUCCESS = 106;



    /**
     * 政府采购数据
     */
    private ArrayList<Hutils.Ciist_entity> zhengfucaigouData = new ArrayList<>();

    /**
     * 工程建设数据
     */
    private ArrayList<Hutils.Ciist_entity> gongchengjiansheData = new ArrayList<>();

    /**
     * 产权交易数据
     */
    private ArrayList<Hutils.Ciist_entity> chanquanjiaoyiData = new ArrayList<>();

    /**
     * 土地审批数据
     */
    private ArrayList<Hutils.Ciist_entity> tudishenpiData = new ArrayList<>();

    /**
     * 采矿审批数据
     */
    private ArrayList<Hutils.Ciist_entity> caikuangshenpiData = new ArrayList<>();

    private boolean isNet;

    private boolean LoadData_zhengfucaigou = false;


    Handler mhandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_ZFCG_SUCCESS:
                    zhengfucaigou_Adapter=new CiistAdapter(TradingHallActivity.this,zhengfucaigouData);
                    zhengfucaigou_list.setAdapter(zhengfucaigou_Adapter);

                    trading_scr.smoothScrollTo(0,0);
                    break;

                case  MSG_GCJS_SUCCESS:
                    gongchengjianshe_Adapter=new CiistAdapter(TradingHallActivity.this,gongchengjiansheData);
                    gongchengjianshe_list.setAdapter(gongchengjianshe_Adapter);

                    trading_scr.smoothScrollTo(0,0);
                    break;



                case MSG_CQJY_SUCCESS:
                    chanquanjiaoyi_Adapter=new CiistAdapter(TradingHallActivity.this,chanquanjiaoyiData);
                    chanquanjiaoyi_list.setAdapter(chanquanjiaoyi_Adapter);

                    trading_scr.smoothScrollTo(0,0);
                    break;



                case MSG_TDSP_SUCCESS:
                    tudishenpi_Adapter=new CiistAdapter(TradingHallActivity.this,tudishenpiData);
                    tudishenpi_list.setAdapter(tudishenpi_Adapter);

                    trading_scr.smoothScrollTo(0,0);
                    break;



                case MSG_CKSP_SUCCESS:
                    caikuangshenpi_Adapter=new CiistAdapter(TradingHallActivity.this,caikuangshenpiData);
                    caikuangshenpi_list.setAdapter(caikuangshenpi_Adapter);

                    trading_scr.smoothScrollTo(0,0);
                    break;


                case MSG_REF_SUCCESS:
                    trading_refre.finishRefresh();
                    trading_scr.smoothScrollTo(0,0);


                    break;

            }



        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_hall);
        mContext = getApplicationContext();
        NetUtil.hasNetWork(this);  //检查网络，没有网络弹出提示对话框
        initview();
        onInitAfterLogic();
        getDataOfZhengfuCaigou();
        getDataOfGongchengJianshe();
        getDataOfChanquanJiaoyi();
        getDataOfTudiShenpi();
        getDataOfCaikuangShenpi();

    }

    private void onInitAfterLogic() {
        zhengfucaigouData = new ArrayList<Hutils.Ciist_entity>();
        gongchengjiansheData = new ArrayList<Hutils.Ciist_entity>();
        chanquanjiaoyiData = new ArrayList<Hutils.Ciist_entity>();
        tudishenpiData = new ArrayList<Hutils.Ciist_entity>();
        caikuangshenpiData = new ArrayList<Hutils.Ciist_entity>();
    }

    /**
     * 初始化
     */
    private void initview() {

        trading_refre=(RefreshableView)findViewById(R.id.trading_refresh);
        trading_scr=(ScrollView)findViewById(R.id.trading_scr);
        trading_bank_view=(CiistTitleView)findViewById(R.id.trading_titleview);
        trading_bank_view.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        trading_bank_view.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TradingHallActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        trading_zhengfucaigou_title=(CiistSmallTitle)findViewById(R.id.trad_zhengfucaigou_title);
        trading_zhengfucaigou_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(TradingHallActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "政府采购");
                bundle.putString("subjectname", "政府采购");
                bundle.putString("subjectcode", "A1A7525B-BA1F-43FA-B897-6617CB82E4C2");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        trading_gongchengjianshe_title=(CiistSmallTitle)findViewById(R.id.trad_gongchengjianshe_title);
        trading_gongchengjianshe_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(TradingHallActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "工程建设");

                bundle.putString("subjectname", "工程建设");
                bundle.putString("subjectcode", "ED06BA04-CEDC-4918-B8CF-5636EFD40AFF");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        trading_chanquanjiaoyi_title=(CiistSmallTitle)findViewById(R.id.trad_chanquanjiaoyi_title);
        trading_chanquanjiaoyi_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(TradingHallActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "产权交易");

                bundle.putString("subjectname", "产权交易");
                bundle.putString("subjectcode", "CB24A395-E31E-4E42-A2F6-388DE892D8E7");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        trading_tudishenpi_title=(CiistSmallTitle)findViewById(R.id.trad_tudishenpi_title);
        trading_tudishenpi_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(TradingHallActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "土地审批结果公告");

                bundle.putString("subjectname", "土地审批结果公告");
                bundle.putString("subjectcode", "5DFCA3CA-8F84-4738-8092-16B1EE42D46C");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        trading_caikuangshenpi_title=(CiistSmallTitle)findViewById(R.id.trad_caikaungshenpi_title);
        trading_caikuangshenpi_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(TradingHallActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "采矿权审批结果公告");

                bundle.putString("subjectname", "采矿权审批结果公告");
                bundle.putString("subjectcode", "E6B324E2-6452-459E-A5D7-31173769B155");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });


        zhengfucaigou_list=(ScrollListView)findViewById(R.id.trad_zhengfucaigou_list);

        gongchengjianshe_list=(ScrollListView)findViewById(R.id.trad_gongchengjianshe_list);

        chanquanjiaoyi_list=(ScrollListView)findViewById(R.id.trad_chanquanjiaoyi_list);

        tudishenpi_list=(ScrollListView)findViewById(R.id.trad_tudishenpi_list);

        caikuangshenpi_list=(ScrollListView)findViewById(R.id.trad_caikaungshenpi_list);


        zhengfucaigou_list.setOnItemClickListener(OC);
        gongchengjianshe_list.setOnItemClickListener(OC);
        chanquanjiaoyi_list.setOnItemClickListener(OC);
        tudishenpi_list.setOnItemClickListener(OC);
        caikuangshenpi_list.setOnItemClickListener(OC);


    }

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
                    bundle.putInt("colorgird", getResources().getColor(R.color.tradinghall));
                    bundle.putString("subjectname2",  ce.Title);

                    bundle.putString("subjectname", ce.Title);
                    bundle.putString("subjectcode", ce.Remark5);
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        }


    };

    /**
     * 获取政府采购数据
     */
    private void getDataOfZhengfuCaigou() {
        LoadData_zhengfucaigou=true;
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "A1A7525B-BA1F-43FA-B897-6617CB82E4C2" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        zhengfucaigouData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    LoadData_zhengfucaigou=false;
                    isRefreashCompleted();
                    mhandler.sendEmptyMessage(MSG_ZFCG_SUCCESS);


                } catch (Exception e) {
                    LoadData_zhengfucaigou=false;
                    isRefreashCompleted();
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取工程建设数据
     */
    private void getDataOfGongchengJianshe() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "ED06BA04-CEDC-4918-B8CF-5636EFD40AFF" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        gongchengjiansheData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_GCJS_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取产权交易数据
     */
    private void getDataOfChanquanJiaoyi() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "CB24A395-E31E-4E42-A2F6-388DE892D8E7" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        chanquanjiaoyiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_CQJY_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取土地审批数据
     */
    private void getDataOfTudiShenpi() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "5DFCA3CA-8F84-4738-8092-16B1EE42D46C" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        tudishenpiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_TDSP_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取采矿审批数据
     */
    private void getDataOfCaikuangShenpi() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "E6B324E2-6452-459E-A5D7-31173769B155" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        caikuangshenpiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_CKSP_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    @Override
    public void onRefresh(RefreshableView view) {
        if(zhengfucaigouData!=null)zhengfucaigouData.clear();
        trading_scr.smoothScrollTo(0,0);
        getDataOfZhengfuCaigou();
        trading_scr.smoothScrollTo(0, 0);
    }

    private void isRefreashCompleted() {
        trading_scr.smoothScrollTo(0, 0);
        if (LoadData_zhengfucaigou==false) {
            mhandler.sendEmptyMessageDelayed(MSG_REF_SUCCESS, 3000);
            trading_scr.smoothScrollTo(0, 0);
        }
    }
}
