package com.ciist.app;

import java.util.ArrayList;
import java.util.List;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.DataCoreLeftAdapter;
import com.ciist.toolkits.DataCoreRightAdapter;
import com.ciist.util.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;
import com.hw.ciist.view.HListView;
import com.hw.ciist.view.HListView.IXListViewListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class DataCoreActivity extends Activity implements OnClickListener {

    private static final String LISTCODE = "C1969E80-6E5D-4C52-89A2-16AA2E1E9515";
    private static final String TAG = "tag";

    private CiistTitleView datacore_title;
    private static final int DATASUCCESS = 1;// 数据获取成功
    private static final int DATAOVER = 2;// 数据获取结束
    private static final int SUCCESSNODATA = 3;// 数据获取成功但没有数据
    private static final int DATAFAIL = 4;// 数据获取失败

    TextView data_core_num_tv;
    TextView data_core_qushi_tv;
    TextView data_core_bili_tv;
    TextView data_core_liebiao_tv;


    private String itemCode = "";//右边列表编码
    private int L_index = 1;// 左边页数
    private int R_index = 1;// 右边页数
    private int L_num = 10;// 左边条数
    private int R_num = 30;// 右边条数
    private boolean netState = false;// 网络状态

    private DataCoreLeftAdapter leftAdapter = null;
    private DataCoreRightAdapter rightAdapter = null;

    private List<Ciist_entity> leftparseData = new ArrayList<Ciist_entity>();// 左边解析数据集合
    private ArrayList<Ciist_entity> leftData = new ArrayList<Ciist_entity>();// 左边的说有数据集合
    private List<Ciist_entity> rightparseData = new ArrayList<Ciist_entity>();// 右边解析数据集合
    private ArrayList<Ciist_entity> rightData = new ArrayList<Ciist_entity>();// 右边的说有数据集合

    private XListView data_core_llv;
    private XListView data_core_rlv;
    private LineChart data_core_LineChart;
    private BarChart data_core_BarChart;
    private PieChart data_core_PieChart;

    private boolean selectedPos;

    int a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        setContentView(R.layout.datacore_layout);
        initView();// 初始化
        initPullState();// 初始化列表刷新状态
        getData();// 获取列表数据
        TotalListener();// 监听
    }

    private void clearcolor(){
            data_core_num_tv.setBackgroundResource(R.color.datamalongnum);
            data_core_qushi_tv.setBackgroundResource(R.color.datamalongnum);
            data_core_bili_tv.setBackgroundResource(R.color.datamalongnum);
            data_core_liebiao_tv.setBackgroundResource(R.color.datamalongnum);
    }

    private void yanse(int a){

        clearcolor();
        if (a==1){
            data_core_num_tv.setBackgroundResource(R.color.datamalongziti);
        }else if(a==2){
            data_core_qushi_tv.setBackgroundResource(R.color.datamalongziti);
        }else if(a==3){
            data_core_bili_tv.setBackgroundResource(R.color.datamalongziti);
        }else if(a==4){
            data_core_liebiao_tv.setBackgroundResource(R.color.datamalongziti);
        }
    }
    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.data_core_num_tv:
                yanse(1);
                resetRight();
                data_core_BarChart.setVisibility(View.VISIBLE);
                break;
            case R.id.data_core_qushi_tv:
                yanse(2);
                data_core_qushi_tv.getBackground().clearColorFilter();
                resetRight();
                data_core_LineChart.setVisibility(View.VISIBLE);
                break;
            case R.id.data_core_bili_tv:
                yanse(3);
                data_core_bili_tv.getBackground().clearColorFilter();
                resetRight();
                data_core_PieChart.setVisibility(View.VISIBLE);
                break;
            case R.id.data_core_liebiao_tv:
                yanse(4);
                data_core_liebiao_tv.getBackground().clearColorFilter();
                resetRight();
                data_core_rlv.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * 重置显示图形
     */
    private void resetRight() {
        data_core_LineChart.setVisibility(View.INVISIBLE);
        data_core_BarChart.setVisibility(View.INVISIBLE);
        data_core_PieChart.setVisibility(View.INVISIBLE);
        data_core_rlv.setVisibility(View.INVISIBLE);
    }

    /**
     * 所有的监听事件
     */
    private void TotalListener() {
        // 左边列表的刷新加载
        data_core_llv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                L_index++;
                getData();
            }
        });
        // 右边列表的刷新加载
        data_core_rlv.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                R_index++;
                getData();
            }
        });
        leftAdapter = new DataCoreLeftAdapter(DataCoreActivity.this, leftData);
        data_core_llv.setAdapter(leftAdapter);
        // 左边控件的item点击监听
        data_core_llv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                arg2--;
                itemCode = leftData.get(arg2).Remark5;
                rightData.clear();
                getData();

                leftAdapter.setSelectedPosition(arg2);
                datacore_title.setTitle(leftData.get(arg2).Title);
//				arg1.setSelected(true);
            }
        });
    }

    /**
     * 初始化列表刷新状态
     */
    private void initPullState() {
        data_core_llv.setPullRefreshEnable(false);
        data_core_llv.setPullLoadEnable(false);
        data_core_rlv.setPullRefreshEnable(false);
        data_core_rlv.setPullLoadEnable(false);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        datacore_title = (CiistTitleView) findViewById(R.id.data_core_titleview);
        data_core_LineChart = (LineChart) findViewById(R.id.data_core_LineChart);
        data_core_BarChart = (BarChart) findViewById(R.id.data_core_BarChart);
        data_core_PieChart = (PieChart) findViewById(R.id.data_core_PieChart);
        data_core_llv = (XListView) findViewById(R.id.data_core_llv);
        data_core_rlv = (XListView) findViewById(R.id.data_core_rlv);

        data_core_num_tv = (TextView) findViewById(R.id.data_core_num_tv);
        data_core_num_tv.setOnClickListener(this);

        data_core_qushi_tv = (TextView) findViewById(R.id.data_core_qushi_tv);
        data_core_qushi_tv.setOnClickListener(this);

        data_core_bili_tv = (TextView) findViewById(R.id.data_core_bili_tv);
        data_core_bili_tv.setOnClickListener(this);

        data_core_liebiao_tv = (TextView) findViewById(R.id.data_core_liebiao_tv);
        data_core_liebiao_tv.setOnClickListener(this);
        // 初始化右边默认显示样式
        resetRight();
        data_core_BarChart.setVisibility(View.VISIBLE);
        datacore_title.setOnLiftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /**
     * 获得左边的列表数据
     */
    private void getData() {
        netState = Hutils.getNetState(this);
        if (netState) {
            AsyncGetData();
        } else {
            Toast.makeText(this, "请打开网络", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 异步取数据
     */
    private void AsyncGetData() {
        new Thread(new Runnable() {
            public void run() {
                if (itemCode.equals("")) {
                    String path = ServerInfo.GetInfoPre + LISTCODE + "/" + L_index + "/" + L_num;
                    String data = Hutils.fromNetgetData(path);
                    Log.i(TAG, data + "-----------------------");
                    leftparseData = Hutils.parseJSONData(data, null);
                    leftData.addAll(leftparseData);
                } else {
                    String path = ServerInfo.GetInfoPre + itemCode + "/" + R_index + "/" + R_num;
                    String data = Hutils.fromNetgetData(path);
                    Log.i(TAG, itemCode + "aa-----------------------");
                    Log.i(TAG, data + "bb-----------------------");
                    rightparseData = Hutils.parseJSONData(data, null);
                    rightData.addAll(rightparseData);
                }
                mHandler.sendEmptyMessage(DATASUCCESS);
            }
        }).start();
    }

    /**
     * 刷新完成停止所以加载动作
     */
    private void onLoad() {
        data_core_llv.stopRefresh();
        data_core_llv.stopLoadMore();
        data_core_llv.setRefreshTime("刚刚");
    }

    /**
     * handler处理
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DATASUCCESS:
                    if (itemCode.equals("")) {
                        if (leftparseData.size() < L_num) {
                            data_core_llv.setPullLoadEnable(false);
                        } else {
                            data_core_llv.setPullLoadEnable(true);
                        }
                        if (leftAdapter == null) {
                            leftAdapter = new DataCoreLeftAdapter(DataCoreActivity.this, leftData);
                            data_core_llv.setAdapter(leftAdapter);
                        } else {
                            leftAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (rightparseData.size() < R_num) {
                            data_core_rlv.setPullLoadEnable(false);
                        } else {
                            data_core_rlv.setPullLoadEnable(true);
                        }
                        if (rightAdapter == null) {
                            rightAdapter = new DataCoreRightAdapter(DataCoreActivity.this, rightData);
                            data_core_rlv.setAdapter(rightAdapter);
                        } else {
                            rightAdapter.notifyDataSetChanged();
                        }
                        itemCode = "";
                        // 设置折线图
                        LineData lineData = ChartUtils.makeLineData(rightData.size(), rightData, "数据中心");
                        ChartUtils.setChartStyle(data_core_LineChart, lineData, Color.TRANSPARENT);
                        // 设置柱形图
                        BarData barData = ChartUtils.getBarData(rightData.size(), rightData, "数据中心");
                        ChartUtils.setBarChart(data_core_BarChart, barData);
                        // 设置饼状图
                        PieData pieData = ChartUtils.getPieData(rightData.size(), rightData);
                        ChartUtils.setPieChart(data_core_PieChart, pieData, "数据中心");
                    }
                    onLoad();// 加载完毕
                    break;
            }
        }
    };

}
