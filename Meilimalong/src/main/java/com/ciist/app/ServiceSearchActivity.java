package com.ciist.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter;
import com.ciist.util.NotificationDialog;
import com.hw.ciist.util.Hutils;

import java.util.ArrayList;

public class ServiceSearchActivity extends Activity implements XListView.IXListViewListener, AdapterView.OnItemClickListener {

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

    private ArrayList<Hutils.Ciist_entity> data = new ArrayList<Hutils.Ciist_entity>();//解析出来的数据集合
    private CiistAdapter adapter;//lv适配器
    private ItemClickedListener listener;

    private XListView searchlv;
    private ImageView searchimage;
    private EditText searchet;
    private ProgressBar searchpb;
    private ImageView searchback;
    private ImageView searchposition;



    /**
     * 异步处理
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DTATSUCCESS:
                    if (adapter == null) {
                        adapter = new CiistAdapter(ServiceSearchActivity.this,data);
                        searchlv.setAdapter(adapter);
                        searchlv.setVisibility(View.VISIBLE);
                        searchpb.setVisibility(View.INVISIBLE);
                    }
                    if (data != null) {
                        searchlv.setVisibility(View.VISIBLE);
                        searchpb.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    onLoad();
                    break;
                case NOTDATASUCCESS:
                    //homeSearchShowTv.setText("搜索完毕\r\n您搜索的数据未找到\r\n请重新搜索");
                    //homeSearchShowTv.setVisibility(View.VISIBLE);
                    setDialog(TITLE, CONTENT_NOTDATA, false, index, true, R.drawable.bg_shape);
                    searchpb.setVisibility(View.INVISIBLE);
                    searchlv.setPullLoadEnable(false);
                    break;
                case NODATA:
                    searchlv.setPullLoadEnable(false);
                    break;
                case CODEERROR:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_search);
        initview();
        preparebtn();
    }

    private void initview() {
        searchlv = (XListView) findViewById(R.id.service_search_lv);
        searchimage = (ImageView) findViewById(R.id.serice_search_imag);
        searchet = (EditText) findViewById(R.id.service_search_et);
        searchback = (ImageView) findViewById(R.id.service_search_back);
        searchpb = (ProgressBar) findViewById(R.id.service_search_pb);
        searchposition = (ImageView) findViewById(R.id.service_search_position);

        searchlv.setXListViewListener(this);
        searchlv.setPullRefreshEnable(false);
        searchlv.setPullLoadEnable(true);
        searchlv.setOnItemClickListener(this);
    }

    private void preparebtn() {
        searchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                searchCode = searchet.getText() + "";
                if (searchCode.equals("")) {
                    //Toast.makeText(SearchActivity.this, "亲，您还没有输入内容哦！", Toast.LENGTH_SHORT).show();
                    setDialog(TITLE, CONTENT_NOTCONTENT, false, index, true, R.drawable.bg_shape);
                    return;
                }
                if (searchCode.contains(" ")) {
                    //homeSearchShowTv.setText("搜索内容格式错误\r\n请重新输入");
                    searchlv.setVisibility(View.INVISIBLE);
                    //homeSearchShowTv.setVisibility(View.VISIBLE);
                    setDialog(TITLE, CONTENT_NO, false, index, true, R.drawable.bg_shape);
                    return;
                }
                //homeSearchShowTv.setVisibility(View.INVISIBLE);
                searchlv.setVisibility(View.INVISIBLE);
                searchpb.setVisibility(View.VISIBLE);
                data.clear();
                onRefresh();
                //隐藏软件盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchlv.getWindowToken(), 0);
            }
        });
        //设置置顶位置
        searchposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchlv.smoothScrollToPosition(0);
            }
        });
        //监听滑动位置
        searchlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (searchlv.getLastVisiblePosition() >= visibleItemCount * 3) {
                    searchposition.setVisibility(View.VISIBLE);
                }
                if (searchlv.getLastVisiblePosition() <= visibleItemCount)
                    searchposition.setVisibility(View.INVISIBLE);
            }
        });
        //退出
        searchback.setOnClickListener(new View.OnClickListener() {
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


    @Override
    public void onRefresh() {
        index = 1;
        data.clear();
        loadData();
        //homeSearchShowTv.setVisibility(View.INVISIBLE);
        searchlv.setPullLoadEnable(true);
    }
    /**
     * 设置提示Dialog
     */
    public void setDialog(String title, String content, boolean isHaveIcon
            , int icon, boolean isOFF, int color) {
        NotificationDialog dialog = new NotificationDialog(ServiceSearchActivity.this, R.style.add_dialog);
        dialog.setTitle(title);
        dialog.setContent(content);
        if (isHaveIcon) {
            dialog.setIcon(true, icon); //设置图标
        }
        dialog.setCanceledOnTouchOutside(isOFF);//设置点击Dialog外部任意区域关闭Dialog
        dialog.setBgColor(R.color.servicesearchdialog);  //设置颜色
        dialog.show();
    }
    /**
     * 刷新完成停止所以加载动作
     */
    private void onLoad() {
        searchlv.stopRefresh();
        searchlv.stopLoadMore();
        searchlv.setRefreshTime("刚刚");
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        index++;
        loadData();
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
                Intent tmpIntent = new Intent(ServiceSearchActivity.this, ChannelOfCoverStyleActivity.class);
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

        Hutils.Ciist_entity info = data.get(position);
        if (listener != null){
            listener.onItemClickedListener(info);
        }
    }

    public interface ItemClickedListener{
        void onItemClickedListener(Hutils.Ciist_entity info);
    }

}
