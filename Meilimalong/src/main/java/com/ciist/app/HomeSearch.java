package com.ciist.app;

import java.util.ArrayList;

import com.ciist.entities.ServerInfo;
import com.ciist.util.NotificationDialog;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;
import com.hw.ciist.view.HListView;
import com.hw.ciist.view.HListView.IXListViewListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomeSearch extends Activity implements IXListViewListener, AdapterView.OnItemClickListener {

    private String searchPath = "http://211.149.212.154:2015/apps/info/getPublicDataByCondition/ciistkey/0/";

    private static final int NODATA = 300;
    private static final int DTATSUCCESS = 100;
    private static final int NOTDATASUCCESS = 200;
    private static final int CODEERROR = 400;

    private int mIndex = 1;
    private String mSearchCode = "";

    private boolean currentNetState = false;
    private ArrayList<Ciist_entity> data = new ArrayList<Ciist_entity>();
    private HwLvAdapter adapter;

    private HListView homeSearchLv;
    private ImageView homeSearchIv;
    private EditText homeSearchEt;
    private ProgressBar homeSearchPb;
    private LinearLayout homeSearchBack;
    private ImageView homeSearchPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home_search);
        initView();
        prepareBtn();
    }

    /**
     * 按钮监听
     */
    private void prepareBtn() {
        homeSearchIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchCode = homeSearchEt.getText() + "";
                mIndex = 1;
                if (mSearchCode.equals("")) {
                    NotificationDialog dialog = new NotificationDialog(HomeSearch.this, R.style.add_dialog);
                    dialog.setTitle("提示");
                    dialog.setContent("还没有输入内容");
                    dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                    dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
                    dialog.show();
                    return;
                }
                if (mSearchCode.contains(" ")) {
                    NotificationDialog dialog = new NotificationDialog(HomeSearch.this, R.style.add_dialog);
                    dialog.setTitle("提示");
                    dialog.setContent("搜索内容格式错误\r\n请重新输入");
                    dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                    dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
                    dialog.show();
                    homeSearchLv.setVisibility(View.INVISIBLE);
                    return;
                }
                homeSearchLv.setVisibility(View.INVISIBLE);
                homeSearchPb.setVisibility(View.VISIBLE);
                data.clear();
                loadData();
                //隐藏软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(homeSearchIv.getWindowToken(),0);
            }
        });
        homeSearchPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                homeSearchLv.smoothScrollToPosition(0);
            }
        });

        homeSearchLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (homeSearchLv.getLastVisiblePosition() >= visibleItemCount*3){
                    homeSearchPosition.setVisibility(View.VISIBLE);
                }
                if (homeSearchLv.getLastVisiblePosition() <= visibleItemCount)
                    homeSearchPosition.setVisibility(View.INVISIBLE);
            }
        });

    }

    /**
     * 加载数据
     */
    private void loadData() {
        currentNetState = Hutils.getNetState(this);
        if (currentNetState) {
            new Thread(new Runnable() {
                public void run() {
                    String urlPath = searchPath + mSearchCode + "/" + mIndex + "/10";
                    String str = Hutils.fromNetgetData(urlPath, true);
                    if (Hutils.parseJSONData(str, null).size() != 0) {
                        data.addAll(Hutils.parseJSONData(str, null));
                        if (Hutils.parseJSONData(str, null).size() < 10) {
                            handler.sendEmptyMessage(NODATA);
                        } else {
                            handler.sendEmptyMessage(DTATSUCCESS);
                        }
                    } else {
                        handler.sendEmptyMessage(NOTDATASUCCESS);
                    }
                }
            }).start();
        } else {
            NotificationDialog dialog = new NotificationDialog(HomeSearch.this, R.style.add_dialog);
            dialog.setTitle("提示");
            dialog.setContent("请连接网络");
            dialog.setIcon(true, R.mipmap.ciist_icon_prompt_xinhao); //设置图标
            dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
            dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
            dialog.show();
            homeSearchPb.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 异步处理
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DTATSUCCESS:
                    if (adapter == null) {
                        adapter = new HwLvAdapter();
                        homeSearchLv.setAdapter(adapter);
                    }else {
                        adapter.notifyDataSetChanged();
                        homeSearchLv.setVisibility(View.VISIBLE);
                    }

                    homeSearchLv.setVisibility(View.VISIBLE);
                    homeSearchPb.setVisibility(View.INVISIBLE);
                    homeSearchLv.setPullLoadEnable(true);

                    if (mIndex == 3){
                        homeSearchPosition.setVisibility(View.VISIBLE);
                    }
                    break;
                case NOTDATASUCCESS:
                    if(data.size()>0){
                    }else {
                        NotificationDialog dialog = new NotificationDialog(HomeSearch.this, R.style.add_dialog);
                        dialog.setTitle("提示");
                        dialog.setContent("搜索完毕\r\n您搜索的数据未找到\r\n请重新搜索");
                        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                        dialog.setBgColor(R.drawable.bg_shape);  //设置颜色
                        dialog.show();
                    }
                    homeSearchPb.setVisibility(View.INVISIBLE);
                    if (adapter == null) {
                        return;
                    }
                    homeSearchLv.setPullLoadEnable(false);
                    adapter.notifyDataSetChanged();
                    break;
                case NODATA:
                    if (adapter == null) {
                        return;
                    }
                    homeSearchLv.setPullLoadEnable(false);
                    adapter.notifyDataSetChanged();
                    break;
                case CODEERROR:
                    break;
            }
        }
    };

    /**
     * 初始化控件
     */
    private void initView() {
        homeSearchLv = (HListView) findViewById(R.id.homeSearchLv);
        homeSearchIv = (ImageView) findViewById(R.id.homeSearchIv);
        homeSearchEt = (EditText) findViewById(R.id.homeSearchEt);
        homeSearchPb = (ProgressBar) findViewById(R.id.homeSearchPb);
        homeSearchPosition = (ImageView) findViewById(R.id.homeSearchPosition);

        homeSearchPosition.setVisibility(View.INVISIBLE);
        homeSearchLv.setXListViewListener(this);
        homeSearchLv.setOnItemClickListener(this);
        homeSearchLv.setPullLoadEnable(true);
        backListener();
    }

    /**
     * 返回监听
     */
    private void backListener(){
        findViewById(R.id.homeSearchBackIv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeSearch.this.finish();
            }
        });
    }

    @Override
    //点击每项监听
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position--;
        String linkType = data.get(position).Remark4;
        String subjectLink = data.get(position).Remark5;
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(this, WebActivity.class);
            tmpIntent.putExtra("URL", ServerInfo.InfoDetailPath + data.get(position).Info_Ids);
            tmpIntent.putExtra("TITLE", data.get(position).Title);
            tmpIntent.putExtra("PUBDATE", data.get(position).pubDate);
            tmpIntent.putExtra("source", data.get(position).Sourse);
            tmpIntent.putExtra("author", data.get(position).Author);
            tmpIntent.putExtra("infotype", data.get(position).Type);
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("blank", false);
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {
                Intent tmpIntent = new Intent(HomeSearch.this, ChannelOfCoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subjectname", data.get(position).Title);
                bundle.putString("subjectcode", data.get(position).Remark5);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            } else {
                Intent tmpIntent = new Intent(this, ChannelOfCoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subjectname", data.get(position).Title);
                bundle.putString("subjectcode", data.get(position).Remark5);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        }
    }

    /**
     * 进入下一个activity
     */
    void nextEnterActivity(){

    }


    /**
     * ListView适配器
     */
    private class HwLvAdapter extends BaseAdapter {

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
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_ciist_lv, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            String str = data.get(position).Image;
            if (str.length() > 50) {
                Hutils.LoadImage(data.get(position).Image, holder.ciistNewsLvItemImg1);
            }
            holder.ciistNewsLvItemContent1.setText(data.get(position).Title);
            holder.ciistNewsLvItemTime1.setText(formatTime(data.get(position).pubDate));
            holder.ciistNewsLvItemNum1.setText(data.get(position).VisitCount + "");
            return convertView;
        }

        /**
         * �ı��¼���ʾ��ʽ
         *
         * @param time
         * @return time
         */
        public String formatTime(String time) {
            String strs[] = time.split(" ");
            for (int i = 0; i < strs.length; i++) {
                System.out.println(strs[i]);
            }
            String[] b = strs[0].split("-");
            for (int i = 0; i < b.length; i++) {
                System.out.println(b[i]);
            }
            return b[1] + "-" + b[2];
        }

        class Holder {
            TextView ciistNewsLvItemContent1, ciistNewsLvItemTime1, ciistNewsLvItemNum1;
            ImageView ciistNewsLvItemImg1;

            public Holder(View v) {
                ciistNewsLvItemContent1 = (TextView) v.findViewById(R.id.ciistNewsLvItemContent1);
                ciistNewsLvItemTime1 = (TextView) v.findViewById(R.id.ciistNewsLvItemTime1);
                ciistNewsLvItemNum1 = (TextView) v.findViewById(R.id.ciistNewsLvItemNum1);
                ciistNewsLvItemImg1 = (ImageView) v.findViewById(R.id.ciistNewsLvItemImg1);
                homeSearchPb = (ProgressBar) findViewById(R.id.homeSearchPb);
            }
        }
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onLoadMore() {
        mIndex++;
        loadData();
        onLoad();
    }

    /**
     * 上拉加载
     */
    @Override
    public void onRefresh() {
        mIndex = 1;
        data.clear();
        loadData();
        homeSearchPosition.setVisibility(View.INVISIBLE);
        homeSearchLv.setPullLoadEnable(true);
        onLoad();
    }

    /**
     * 刷新完成
     */
    private void onLoad() {
        homeSearchLv.stopRefresh();
        homeSearchLv.stopLoadMore();
        homeSearchLv.setRefreshTime("刚刚");
    }
}
