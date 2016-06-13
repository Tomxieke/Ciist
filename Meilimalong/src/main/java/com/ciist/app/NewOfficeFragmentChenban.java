package com.ciist.app;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.helper.ChartHelper;
import com.ciist.swipelistview.BaseSwipeListViewListener;
import com.ciist.swipelistview.SwipeListView;
import com.ciist.toolkits.ItemInfo_oaAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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


/**
 * Created by 中联软科 on 2015/12/15
 */
public class NewOfficeFragmentChenban extends android.support.v4.app.Fragment {

    private PieChart mChart;
    //    private BarChart mChart;
    private View mView;
    private Context context;
    private ViewGroup mContainer;
    private LayoutInflater mInflater;
    private ProgressBar waiting;
    private SwipeListView mlistView;
    private Button mBtnCreateTask;
    private List<ItemInfo> data;
    private View footer;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_CHART = 801;//
    private ItemInfo_oaAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;
    private InternetOfSubject0Thread subjectThread;
    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mAction;
    private String mTitle;
    private String mCondition = "A";

    public NewOfficeFragmentChenban() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        mContainer = container;
        mInflater = inflater;
        mAction = "zb";
        mView = inflater.inflate(R.layout.new_office_fragment_chenban, container, false);
        initInputParams();
        initCtrls();
        registerListening();
        LoadDatas();
        createChart();
        return mView;
    }

    void initInputParams() {
        try {

//            Bundle data = new Bundle();
//            data.putString("TEXT", "这是Activiy通过Bundle传递过来的值");
//            fragment1.setArguments(data);//通过Bundle向Activity中传递值

            Bundle data = getArguments();//获得从activity中传递过来的值
            mIdentify = data.getString("identify");
            mUsername = data.getString("username");
            mSelfids = data.getString("selfids");
            mTitle = data.getString("title", "承办工作");
            mAction = data.getString("action", "zb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将dp单位数值转化为px单位数值
     * @param context
     * @param pxValue
     * @return
     */
    public  int dp2px(Context context, float pxValue){
        Resources resource = context.getResources();
        float scale = resource.getDisplayMetrics().density;
        return (int) (pxValue * scale + 0.5f);
    }

    void initCtrls() {
        try {
            //获取屏幕宽度，单位为px
            WindowManager windowManager = getActivity().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            mlistView = (SwipeListView) mView.findViewById(R.id.newofficechenbanlist);
            mlistView.setOffsetLeft(screenWidth-dp2px(context,60));
            waiting = (ProgressBar) mView.findViewById(R.id.waitloading);
            footer = mInflater.inflate(R.layout.listview_footer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void registerListening() {
        try {

            mlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    try {
                        if (mlistView.getFooterViewsCount() <= 0) {
                            return;
                        }
                        int lastItemIndexID = firstVisibleItem + visibleItemCount;
                        if (lastItemIndexID <= 0) return;
                        if (lastItemIndexID == totalItemCount) {
                            View lastItemView = mlistView.getChildAt(mlistView.getChildCount() - 1);
                            headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                            int bottomChazhi = Math.abs(mlistView.getBottom() - lastItemView.getBottom());
                            if (lastItemView != null && bottomChazhi == headerHeight && footer != null) {
                                currentPageInfo.setPageIndex(currentHavePageIndex + 1);
                                LoadDatas();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        System.out.println(e.getMessage().toString());
                    }
                }
            });

            //swiplistview设置item监听
            mlistView.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onClickFrontView(int position) {
                    try {
                        if (position == -1) {
                            LoadDatas();
                            return;
                        }
                        ItemInfo tmpobj = adapter.getItem(position);
                        Intent tmpIntent1 = new Intent(context, chatting_item_msg_index_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("identify", mIdentify);
                        bundle.putString("username", mUsername);
                        bundle.putString("selfids", mSelfids);
                        bundle.putString("title", tmpobj.getTitle());
                        bundle.putString("action", tmpobj.getBak5());
                        bundle.putString("actiontype", mAction);
                        bundle.putString("tkstate", tmpobj.getAuthor());
                        bundle.putString("taskids", tmpobj.getLinkurl());
                        tmpIntent1.putExtras(bundle);
                        startActivity(tmpIntent1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onListChanged() {
                    mlistView.closeOpenedItems();
                }
            });

           /* mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {

                        if (id == -1) {
                            LoadDatas();
                            return;
                        }
                        View _view = view.findViewById(R.id.list_view_oa_default_text);
                        TextView tmp = (TextView) _view;
                        ItemInfo tmpobj = (ItemInfo) tmp.getTag();

                        Intent tmpIntent1 = new Intent(context, chatting_item_msg_index_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("identify", mIdentify);
                        bundle.putString("username", mUsername);
                        bundle.putString("selfids", mSelfids);
                        bundle.putString("title", tmpobj.getTitle());
                        bundle.putString("action", tmpobj.getBak5());
                        bundle.putString("actiontype", mAction);
                        bundle.putString("tkstate", tmpobj.getAuthor());

                        bundle.putString("taskids", tmpobj.getLinkurl());
                        tmpIntent1.putExtras(bundle);
                        startActivity(tmpIntent1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void LoadDatas() {
        try {

            subjectThread = new InternetOfSubject0Thread();
            subjectThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
//                    mlistView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ItemInfo>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
//                        mlistView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                        return;
                    }
                    if (result.getSimpleItemObj().size() < 10) {
//                        mlistView.removeFooterView(footer);
                        if (mlistView.getFooterViewsCount() > 0)
                            mlistView.removeFooterView(footer);
                    } else {
                        if (mlistView.getFooterViewsCount() <= 0)
                            mlistView.addFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getSimpleItemObj());
                    if (adapter == null) {
                        int resID = R.layout.listview_item_oa;
                        adapter = new ItemInfo_oaAdapter(context, resID, data,mlistView,mIdentify);
                        mlistView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    currentHavePageIndex++;
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
                case MSG_FAILURE:
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    Toast.makeText(context, "亲,网络不给力呀!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NODATA:
                    mlistView.removeFooterView(footer);
                    Toast.makeText(context, "亲,目前还没有给您交办任何工作!", Toast.LENGTH_SHORT).show();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
                case MSG_CHART:
                    PieData mPieData = (PieData) msg.obj;
                    ChartHelper.showPieChart(mChart, mPieData, "承办工作");
                    break;
            }
        }
    };

    void RefreashData(String _condtion) {
        try {
            if (waiting != null) {
                waiting.setVisibility(View.VISIBLE);
            }

            mCondition = _condtion;
            currentHavePageIndex = 0;
            currentPageInfo = new PageInfo();
            if (data != null && data.size() > 0) {
                data.clear();
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            LoadDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            String _url = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                _url = ServerInfo.ServerBKRoot + "/task/getTaskToMeCondition/ciistkey/" + mIdentify + "/" + currentPageInfo.getPageIndex() + "/10/" + mCondition;
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
                    ItemInfo tmpIteminfo = new ItemInfo();

                    tmpIteminfo.setTitle(jsonObj.getString("Task_zh"));
                    tmpIteminfo.setPubdate(jsonObj.getString("Tkpubdate"));
                    tmpIteminfo.setLinkurl(jsonObj.getString("Selfids"));
                    tmpIteminfo.setInfosource(jsonObj.getString("Tktype"));
                    tmpIteminfo.setInfoauthor(jsonObj.getString("Tkpuber"));
                    tmpIteminfo.setInfotype("BBB");
                    tmpIteminfo.setBak1("交办人：" + jsonObj.getString("User_zh"));
                    int _rday = jsonObj.getInt("rundays");
                    if (_rday == 0) {
                        tmpIteminfo.setBak2("今日到期");
                    } else if (_rday > 0) {
                        tmpIteminfo.setBak2("只有" + _rday + "天");
                    } else {
                        tmpIteminfo.setBak2("超期" + Math.abs(_rday) + "天");
                    }
                    tmpIteminfo.setBak3(jsonObj.getString("Tkbegindate") + "至" + jsonObj.getString("Tkenddate"));
                    tmpIteminfo.setBak4("");
                    tmpIteminfo.setBak5("");
                    tmpIteminfo.setSource("");
                    tmpIteminfo.setAuthor(String.valueOf(jsonObj.getInt("Tkstate")));
                    tmpIteminfo.setVisitCount(jsonObj.getInt("Isread"));
                    tmpIteminfo.setTelnum(jsonObj.getString("Selfids"));
                    tmpIteminfo.setLatidue(0);
                    tmpIteminfo.setLongtidue(0);

                    data.add(tmpIteminfo);
                }
                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setSimpleItemObj(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

    private class InternetOfChartThread extends Thread {

        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            String _url = "";
            try {
                _url = ServerInfo.ServerBKRoot + "/task/TaskChenbanOutline/ciistkey/" + mIdentify;
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

                    //1
                    ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
                    xValues.add("进行中");
                    xValues.add("已完成");
                    xValues.add("超期");
                    xValues.add("新到工作");


                    ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据
                    // 饼图数据
                    /**
                     * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
                     * 所以 14代表的百分比就是14%
                     */
                    int jbchaoqinums = jsonObj.getInt("jbchaoqinums");
                    int jbcuibannums = jsonObj.getInt("jbcuibannums");
                    int jbjinxingzhongnums = jsonObj.getInt("jbjinxingzhongnums");
                    int jbsuoyounums = jsonObj.getInt("jbsuoyounums");
                    int jbweihuibaonums = jsonObj.getInt("jbweihuibaonums");
                    int jbxinhuifunums = jsonObj.getInt("jbxinhuifunums");
                    int jbyiwanchengnums = jsonObj.getInt("jbyiwanchengnums");
                    int allnums = jbchaoqinums + jbcuibannums + jbjinxingzhongnums + jbweihuibaonums + jbxinhuifunums + jbyiwanchengnums;
                    float quarterly_jinxingzhong = Float.valueOf(jbjinxingzhongnums) / Float.valueOf(allnums);
                    float quarterly_yiwancheng = Float.valueOf(jbyiwanchengnums) / Float.valueOf(allnums);
                    float quarterly_chaoqi = Float.valueOf(jbchaoqinums) / Float.valueOf(allnums);
                    float quarterly_weihuibao = Float.valueOf(jbweihuibaonums) / Float.valueOf(allnums);
                    float quarterly_cuiban = Float.valueOf(jbcuibannums) / Float.valueOf(allnums);
                    float quarterly_xinhuibao = Float.valueOf(jbxinhuifunums) / Float.valueOf(allnums);
//        float quarterly4 = 38;
                    yValues.add(new Entry(quarterly_jinxingzhong, 0));
                    yValues.add(new Entry(quarterly_yiwancheng, 1));
                    yValues.add(new Entry(quarterly_chaoqi, 2));
                    yValues.add(new Entry(quarterly_xinhuibao, 3));
//        yValues.add(new Entry(quarterly4, 3));
                    //y轴的集合
                    PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
                    pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
                    ArrayList<Integer> colors = new ArrayList<Integer>();
                    // 饼图颜色
                    colors.add(Color.rgb(205, 205, 205));
                    colors.add(Color.rgb(114, 188, 223));
                    colors.add(Color.rgb(255, 123, 124));
                    colors.add(Color.rgb(57, 135, 200));
                    pieDataSet.setColors(colors);
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    float px = 5 * (metrics.densityDpi / 160f);
                    pieDataSet.setSelectionShift(px); // 选中态多出的长度
                    PieData pieData = new PieData(xValues, pieDataSet);
                    mHandler.obtainMessage(MSG_CHART, pieData).sendToTarget();
                    //1

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

    void createChart() {


        mChart = (PieChart) mView.findViewById(R.id.newOfficePieChartChenban);
        PieData mPieData = getPieData(6, 100);
        ChartHelper.showPieChart(mChart, mPieData, "承办工作");
//        BarData barData = getBarData();
//        ChartHelper.showBarChart(mChart, barData);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {

                String _info = "";
                if (entry.getXIndex() == 0) {
                    _info = "正在加载进行中的工作";
                } else if (entry.getXIndex() == 1) {
                    _info = "正在加载已完成的工作";
                } else if (entry.getXIndex() == 2) {
                    _info = "正在加载超期的工作";
                } else if (entry.getXIndex() == 3) {
                    _info = "正在加载新到的工作";
                }
//                Toast.makeText(context, _info, Toast.LENGTH_SHORT).show();
                mCondition = String.valueOf(entry.getXIndex());
                RefreashData(mCondition);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        new InternetOfChartThread().start();
    }

    private PieData getPieData(int count, float range) {
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
//        for (int i = 0; i < count; i++) {
//            xValues.add("Quarterly" + (i + 1));  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
//        }
        xValues.add("进行中");
        xValues.add("已完成");
        xValues.add("超期");
        xValues.add("新到工作");


        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据
        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        float quarterly1 = 40;
        float quarterly2 = 52;
        float quarterly3 = 10;
        float quarterly4 = 10;

//        float quarterly4 = 38;
        yValues.add(new Entry(quarterly1, 0));
        yValues.add(new Entry(quarterly2, 1));
        yValues.add(new Entry(quarterly3, 2));
        yValues.add(new Entry(quarterly4, 3));

//        yValues.add(new Entry(quarterly4, 3));
        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色
        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));
        colors.add(Color.rgb(255, 123, 124));
        colors.add(Color.rgb(57, 135, 200));

        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        PieData pieData = new PieData(xValues, pieDataSet);
        return pieData;
    }

    private BarData getBarData() {
        int dataSets = 1;
        int count = 12;
        int range = 20000;
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        for (int i = 0; i < dataSets; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int j = 0; j < count; j++) {
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            BarDataSet ds = new BarDataSet(entries, "");
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xValues.add("第" + (i + 1) + "季度");
        }
        BarData d = new BarData(xValues, sets);
        return d;
    }


}
