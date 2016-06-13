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

public class MerchantsActivity extends Activity implements RefreshableView.RefreshListener{

    private Context mContext;

    private RefreshableView merchants_refre;
    private ScrollView merchants_src;

    private CiistTitleView merchants_bank_view;

    private CiistSmallTitle merchants_touzizhinan_title;
    private CiistSmallTitle merchants_touzifuwu_title;
    private CiistSmallTitle merchants_zhaoshangxiangmu_title;
    private CiistSmallTitle merchants_touzipingtai_title;
    private CiistSmallTitle merchants_touzijigou_title;

    private ScrollListView touzizhinan_list;
    private ScrollListView touzifuwu_list;
    private ScrollListView zhaoshangxiangmu_list;
    private ScrollListView touzipingtai_list;
    private ScrollListView touzijigou_list;

    private CiistAdapter touzizhinan_Adapter;
    private CiistAdapter touzifuwu_Adapter;
    private CiistAdapter zhaoshangxiangmu_Adapter;
    private CiistAdapter touzipingtai_Adapter;
    private CiistAdapter touzijigou_Adapter;

    private static final int MSG_TZZN_SUCCESS = 101;
    private static final int MSG_TZFW_SUCCESS = 102;
    private static final int MSG_ZSXM_SUCCESS = 103;
    private static final int MSG_TZPT_SUCCESS = 104;
    private static final int MSG_TZJG_SUCCESS = 105;
    private static final int MSG_REF_SUCCESS = 106;



    /**
     * 投资指南数据
     */
    private ArrayList<Hutils.Ciist_entity> touzizhinanData = new ArrayList<>();

    /**
     * 投资服务数据
     */
    private ArrayList<Hutils.Ciist_entity> touzifuwuData = new ArrayList<>();

    /**
     * 招商项目数据
     */
    private ArrayList<Hutils.Ciist_entity> zhaoshangxiangmuData = new ArrayList<>();

    /**
     * 投资平台数据
     */
    private ArrayList<Hutils.Ciist_entity> touzipingtaiData = new ArrayList<>();

    /**
     * 投资机构数据
     */
    private ArrayList<Hutils.Ciist_entity> touzijigouData = new ArrayList<>();

    private boolean isNet;

    private boolean LoadData_touzizhinan = false;


    Handler mhandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TZZN_SUCCESS:
                    touzizhinan_Adapter=new CiistAdapter(MerchantsActivity.this,touzizhinanData);
                    touzizhinan_list.setAdapter(touzizhinan_Adapter);
                    merchants_src.smoothScrollTo(0,0);
                    break;

                case  MSG_TZFW_SUCCESS:
                    touzifuwu_Adapter=new CiistAdapter(MerchantsActivity.this,touzifuwuData);
                    touzifuwu_list.setAdapter(touzifuwu_Adapter);
                    merchants_src.smoothScrollTo(0,0);
                    break;

                case MSG_ZSXM_SUCCESS:
                    zhaoshangxiangmu_Adapter=new CiistAdapter(MerchantsActivity.this,zhaoshangxiangmuData);
                    zhaoshangxiangmu_list.setAdapter(zhaoshangxiangmu_Adapter);
                    merchants_src.smoothScrollTo(0,0);
                    break;

                case MSG_TZPT_SUCCESS:
                    touzipingtai_Adapter=new CiistAdapter(MerchantsActivity.this,touzipingtaiData);
                    touzipingtai_list.setAdapter(touzipingtai_Adapter);
                    merchants_src.smoothScrollTo(0,0);
                    break;

                case MSG_TZJG_SUCCESS:
                    touzijigou_Adapter=new CiistAdapter(MerchantsActivity.this,touzijigouData);
                    touzijigou_list.setAdapter(touzijigou_Adapter);
                    merchants_src.smoothScrollTo(0,0);
                    break;

                case MSG_REF_SUCCESS:
                    merchants_refre.finishRefresh();
                    merchants_src.smoothScrollTo(0,0);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants);
        mContext = getApplicationContext();
        NetUtil.hasNetWork(this);  //检查网络，没有网络弹出提示对话框
        initview();
        onInitAfterLogic();
        getDataOfTouziZhinan();
        getDataOfTouziFuwu();
        getDataOfZhaoshangXiangmu();
        getDataOfTouziPingtai();
        getDataOfTouziJigou();

    }

    private void onInitAfterLogic() {
        touzizhinanData = new ArrayList<Hutils.Ciist_entity>();
        touzifuwuData = new ArrayList<Hutils.Ciist_entity>();
        zhaoshangxiangmuData = new ArrayList<Hutils.Ciist_entity>();
        touzipingtaiData = new ArrayList<Hutils.Ciist_entity>();
        touzijigouData = new ArrayList<Hutils.Ciist_entity>();
    }

    /**
     * 初始化
     */
    private void initview() {

        merchants_refre=(RefreshableView)findViewById(R.id.merchants_refre);
        merchants_src=(ScrollView)findViewById(R.id.merchants_scr);
        merchants_bank_view=(CiistTitleView)findViewById(R.id.merchants_titleview);
        merchants_bank_view.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        merchants_bank_view.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MerchantsActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        merchants_touzizhinan_title=(CiistSmallTitle)findViewById(R.id.merchants_touzizhinan_title);
        merchants_touzizhinan_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MerchantsActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2", "投资指南");
                bundle.putString("subjectname", "投资指南");
                bundle.putString("subjectcode", "5E98C9A4-1473-42E9-85C9-F085DFC3B8D3");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
//                tmpIntent.putExtra("subjectname", "投资指南");
//                tmpIntent.putExtra("subjectcode", "5E98C9A4-1473-42E9-85C9-F085DFC3B8D3");
                startActivity(tmpIntent);
            }
        });

        merchants_touzifuwu_title=(CiistSmallTitle)findViewById(R.id.merchants_touzifuwu_title);
        merchants_touzifuwu_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MerchantsActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2","投资服务");

                bundle.putString("subjectname", "投资服务");
                bundle.putString("subjectcode", "420BA722-5B56-46EE-A539-4317D1BC97EA");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        merchants_zhaoshangxiangmu_title=(CiistSmallTitle)findViewById(R.id.merchants_zhaoshangxiangmu_title);
        merchants_zhaoshangxiangmu_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MerchantsActivity.this,CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2","招商项目");

                bundle.putString("subjectname", "招商项目");
                bundle.putString("subjectcode", "5D8FC8C9-786E-4E02-8103-23D0D43AADEE");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        merchants_touzipingtai_title=(CiistSmallTitle)findViewById(R.id.merchants_touzipingtai_title);
        merchants_touzipingtai_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MerchantsActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2","投资平台");

                bundle.putString("subjectname", "投资平台");
                bundle.putString("subjectcode", "74D4E9D1-BA37-4EC1-B271-7F294F72945D");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });

        merchants_touzijigou_title=(CiistSmallTitle)findViewById(R.id.merchants_touzijigou_title);
        merchants_touzijigou_title.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MerchantsActivity.this, CoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                bundle.putString("subjectname2","投资服务机构");

                bundle.putString("subjectname", "投资服务机构");
                bundle.putString("subjectcode", "CA926E85-076F-48C3-B8DE-3A22FFB10250");
                bundle.putBoolean("full", true);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        });


        touzizhinan_list=(ScrollListView)findViewById(R.id.merchants_touzizhinan_list);

        touzifuwu_list=(ScrollListView)findViewById(R.id.merchants_touzifuwu_list);

        zhaoshangxiangmu_list=(ScrollListView)findViewById(R.id.merchants_zhaoshangxiangmu_list);

        touzipingtai_list=(ScrollListView)findViewById(R.id.merchants_touzipingtai_list);

        touzijigou_list=(ScrollListView)findViewById(R.id.merchants_touzijigou_list);


        touzizhinan_list.setOnItemClickListener(OC);
        touzifuwu_list.setOnItemClickListener(OC);
        zhaoshangxiangmu_list.setOnItemClickListener(OC);
        touzipingtai_list.setOnItemClickListener(OC);
        touzijigou_list.setOnItemClickListener(OC);


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
                    bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
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
     * 获取投资指南数据
     */
    private void getDataOfTouziZhinan() {
        LoadData_touzizhinan=true;
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "5E98C9A4-1473-42E9-85C9-F085DFC3B8D3" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        touzizhinanData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    LoadData_touzizhinan=false;
                    isRefreashCompleted();
                    mhandler.sendEmptyMessage(MSG_TZZN_SUCCESS);


                } catch (Exception e) {
                    LoadData_touzizhinan=false;
                    isRefreashCompleted();
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取投资服务数据
     */
    private void getDataOfTouziFuwu() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "420BA722-5B56-46EE-A539-4317D1BC97EA" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        touzifuwuData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_TZFW_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取招商项目数据
     */
    private void getDataOfZhaoshangXiangmu() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "5D8FC8C9-786E-4E02-8103-23D0D43AADEE" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        zhaoshangxiangmuData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_ZSXM_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取投资平台数据
     */
    private void getDataOfTouziPingtai() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "74D4E9D1-BA37-4EC1-B271-7F294F72945D" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        touzipingtaiData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_TZPT_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    /**
     * 获取投资机构数据
     */
    private void getDataOfTouziJigou() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "CA926E85-076F-48C3-B8DE-3A22FFB10250" + "/" + 1 + "/"
                                + 5;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        touzijigouData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }
                    mhandler.sendEmptyMessage(MSG_TZJG_SUCCESS);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }

    @Override
    public void onRefresh(RefreshableView view) {
        if(touzizhinanData!=null)touzizhinanData.clear();
        merchants_src.smoothScrollTo(0,0);
        getDataOfTouziZhinan();
        merchants_src.smoothScrollTo(0, 0);
    }

    private void isRefreashCompleted() {
        merchants_src.smoothScrollTo(0, 0);
        if (LoadData_touzizhinan==false) {
            mhandler.sendEmptyMessageDelayed(MSG_REF_SUCCESS, 3000);
            merchants_src.smoothScrollTo(0, 0);
        }
    }
}
