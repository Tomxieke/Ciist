package com.ciist.app;

import java.util.ArrayList;

import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchActivity extends Activity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

    private String search_path = "http://211.149.212.154:2015/apps/info/getPublicDataByCondition/ciistkey/0/";

    private static final String TITLE = "提示";
    private static final String CONTENT_NOTDATA = "搜索完毕\r\n您搜索的数据未找到\r\n请重新搜索";
    private static final String CONTENT_NOTCONTENT = "还没有输入内\r\n请继续输入";
    private static final String CONTENT_NO = "搜索内容格式错误\r\n请重新输入";
    private static final String CONTENT_NOTNET = "请连接网络";

    private static final int NODATA = 300;//没有数据
    private static final int DTATSUCCESS = 100;//数据获取成功
    private static final int NOTDATASUCCESS = 200;//数据获取成功但没有数据
    private static final int CODEERROR = 400;//搜索内容异常

    private int index = 1;//页数
    private String searchCode = "";//搜索内容
    private boolean currentNetState = false;//网络状态

    private ArrayList<Ciist_entity> data = new ArrayList<Ciist_entity>();//解析出来的数据集合
    private CiistAdapter adapter;//lv适配器
    private ItemClickedListener listener;

    private XListView searchLv;
    private ImageView searchIv;
    private EditText searchEt;
    private ProgressBar searchPb;
    private ImageView searchBack;
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
        searchIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                searchCode = searchEt.getText() + "";
                if (searchCode.equals("")) {
                    //Toast.makeText(SearchActivity.this, "亲，您还没有输入内容哦！", Toast.LENGTH_SHORT).show();
                    setDialog(TITLE, CONTENT_NOTCONTENT, false, index, true, R.drawable.bg_shape);
                    return;
                }
                if (searchCode.contains(" ")) {
                    //homeSearchShowTv.setText("搜索内容格式错误\r\n请重新输入");
                    searchLv.setVisibility(View.INVISIBLE);
                    //homeSearchShowTv.setVisibility(View.VISIBLE);
                    setDialog(TITLE, CONTENT_NO, false, index, true, R.drawable.bg_shape);
                    return;
                }
                //homeSearchShowTv.setVisibility(View.INVISIBLE);
                searchLv.setVisibility(View.INVISIBLE);
                searchPb.setVisibility(View.VISIBLE);
                data.clear();
                onRefresh();
                //隐藏软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchIv.getWindowToken(), 0);
            }
        });
        //设置置顶位置
        homeSearchPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLv.smoothScrollToPosition(0);
            }
        });
        //监听滑动位置
        searchLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (searchLv.getLastVisiblePosition() >= visibleItemCount * 3) {
                    homeSearchPosition.setVisibility(View.VISIBLE);
                }
                if (searchLv.getLastVisiblePosition() <= visibleItemCount)
                    homeSearchPosition.setVisibility(View.INVISIBLE);
            }
        });
        //退出
        searchBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    // 组合访问地址
                    String urlPath = search_path + searchCode + "/" + index + "/10";
                    // 把获取到的数据添加到集合中
                    String str = Hutils.fromNetgetData(urlPath, true);
                    System.out.println("打印数据" + str);
                    if (Hutils.parseJSONData(str, null).size() != 0) {
                        data.addAll(Hutils.parseJSONData(str, null));
                        handler.sendEmptyMessage(DTATSUCCESS);
                        if (Hutils.parseJSONData(str, null).size() < 10) {
                            handler.sendEmptyMessage(NODATA);
                        }
                    } else {
                        handler.sendEmptyMessage(NOTDATASUCCESS);
                    }
                }
            }).start();
        } else {
            //Toast.makeText(this, "你还没有开启网络哦", Toast.LENGTH_SHORT).show();
            setDialog(TITLE, CONTENT_NOTNET, true
                    , R.mipmap.ciist_icon_prompt_xinhao, true
                    , R.drawable.bg_shape);
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        searchLv = (XListView) findViewById(R.id.homeSearchLv);
        searchIv = (ImageView) findViewById(R.id.homeSearchIv);
        searchEt = (EditText) findViewById(R.id.homeSearchEt);
        searchBack = (ImageView) findViewById(R.id.homeSearchBackIv);
        searchPb = (ProgressBar) findViewById(R.id.homeSearchPb);
        homeSearchPosition = (ImageView) findViewById(R.id.homeSearchPosition);

        searchLv.setXListViewListener(this);
        searchLv.setPullRefreshEnable(false);
        searchLv.setPullLoadEnable(true);
        searchLv.setOnItemClickListener(this);
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
         * 改变事件显示格式
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
                searchPb = (ProgressBar) findViewById(R.id.homeSearchPb);
            }
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        index++;
        loadData();
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        index = 1;
        data.clear();
        loadData();
        //homeSearchShowTv.setVisibility(View.INVISIBLE);
        searchLv.setPullLoadEnable(true);
    }

    /**
     * 刷新完成停止所以加载动作
     */
    private void onLoad() {
        searchLv.stopRefresh();
        searchLv.stopLoadMore();
        searchLv.setRefreshTime("刚刚");
    }

    /**
     * 异步处理
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DTATSUCCESS:
                    if (adapter == null) {
                        adapter = new CiistAdapter(SearchActivity.this,data);
                        searchLv.setAdapter(adapter);
                        searchLv.setVisibility(View.VISIBLE);
                        searchPb.setVisibility(View.INVISIBLE);
                    }
                    if (data != null) {
                        searchLv.setVisibility(View.VISIBLE);
                        searchPb.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    onLoad();
                    break;
                case NOTDATASUCCESS:
                    //homeSearchShowTv.setText("搜索完毕\r\n您搜索的数据未找到\r\n请重新搜索");
                    //homeSearchShowTv.setVisibility(View.VISIBLE);
                    setDialog(TITLE, CONTENT_NOTDATA, false, index, true, R.drawable.bg_shape);
                    searchPb.setVisibility(View.INVISIBLE);
                    searchLv.setPullLoadEnable(false);
                    break;
                case NODATA:
                    searchLv.setPullLoadEnable(false);
                    break;
                case CODEERROR:
                    break;
            }
        }
    };

    /**
     * 设置搜索路径
     *
     * @param searchPath
     */
    public void setSearch_path(String searchPath) {
        this.search_path = searchPath;
    }

    /**
     * 设置提示Dialog
     *
     * @param title      标题
     * @param content    内容描述
     * @param isHaveIcon 是否需要图标
     * @param icon       设置图标
     * @param isOFF      是否点击外部任意地方关闭Dialog
     * @param color      设置背景颜色
     */
    public void setDialog(String title, String content, boolean isHaveIcon
            , int icon, boolean isOFF, int color) {
        NotificationDialog dialog = new NotificationDialog(SearchActivity.this, R.style.add_dialog);
        dialog.setTitle(title);
        dialog.setContent(content);
        if (isHaveIcon) {
            dialog.setIcon(true, icon); //设置图标
        }
        dialog.setCanceledOnTouchOutside(isOFF);//设置点击Dialog外部任意区域关闭Dialog
        dialog.setBgColor(color);  //设置颜色
        dialog.show();
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
            tmpIntent.putExtra("img_url",data.get(position).Image);
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("blank", false);
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {
                Intent tmpIntent = new Intent(SearchActivity.this, ChannelOfCoverStyleActivity.class);
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

        Ciist_entity info = data.get(position);
        if (listener != null){
            listener.onItemClickedListener(info);
        }
    }

    public interface ItemClickedListener{
        void onItemClickedListener(Ciist_entity info);
    }

    public void setIOnItemClickedListener(ItemClickedListener l){
        listener = l;
    }
}
