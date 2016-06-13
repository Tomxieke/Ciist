package com.ciist.app;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.helper.MathHelper;
import com.ciist.toolkits.ItemInfoAdapter_sjml;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
public class NewOfficeFragmentshuju extends android.support.v4.app.Fragment {

    private LineChart mChart;
    //    private BarChart mChart;
    private View mView;
    private Context context;
    private ViewGroup mContainer;
    private LayoutInflater mInflater;
    private ProgressBar waiting;
    private ListView mlistView;
    private List<ItemInfo> data;
    private View footer;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_CHART = 801;//
    private ItemInfoAdapter_sjml adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;
    private InternetOfSubject0Thread subjectThread;
    private android.support.v4.app.FragmentManager mManager;
    private String mSubjectCode = "";
    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mAction;
    private String mTitle;
    private String mCondition = "A";

    private EditText etContent;
    private ImageView search;
    private FrameLayout fl;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
                    mlistView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ItemInfo>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
                        mlistView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                        return;
                    }
                    if (result.getSimpleItemObj().size() < 10) {
                        mlistView.removeFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getSimpleItemObj());
                    if (adapter == null) {
                        //int resID = R.layout.listview_item;
                        //adapter = new ItemInfoAdapter(context, resID, data);
                        adapter = new ItemInfoAdapter_sjml(data);
                        mlistView.setAdapter(adapter);
                        LineData mLineData = getLineData(adapter.getCount(), 100);
                        showChart(mChart, mLineData, Color.rgb(114, 188, 223));

                    } else {
                        adapter.notifyDataSetChanged();
                        LineData mLineData = getLineData(adapter.getCount(), 100);
                        showChart(mChart, mLineData, Color.rgb(114, 188, 223));
                    }
                    currentHavePageIndex++;
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
//                    loading.dismiss();
                    break;
                case MSG_FAILURE:
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    Toast.makeText(context, "亲,网络不给力呀!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NODATA:
                    mlistView.removeFooterView(footer);
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
                case 999:
                    //mChart.setVisibility(View.GONE);
                    fl.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public NewOfficeFragmentshuju() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        mContainer = container;
        mInflater = inflater;
        mAction = "sj";
        mView = inflater.inflate(R.layout.new_office_fragment_shuju, container, false);
        initInputParams();
        initCtrls();
        registerListening();
        LoadDatas();
        return mView;
    }

    void initInputParams() {
        try {
            Bundle data = getArguments();//获得从activity中传递过来的值
            mSubjectCode = data.getString("subjectcode");
            mIdentify = data.getString("identify");
            mUsername = data.getString("username");
            mSelfids = data.getString("selfids");
            mTitle = data.getString("title", "数据马龙");
            mAction = data.getString("action", "sj");
            //Log.i("TAG",mSubjectCode+"-------------------------1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initCtrls() {
        try {
            fl = (FrameLayout) mView.findViewById(R.id.newOfficeFl);
            mChart = (LineChart) mView.findViewById(R.id.newOfficeLineChartshuju);
            mlistView = (ListView) mView.findViewById(R.id.newofficeshujulist);
            waiting = (ProgressBar) mView.findViewById(R.id.waitloadingshuju);
            etContent = (EditText) mView.findViewById(R.id.newOfficeShujuEt);
            search = (ImageView) mView.findViewById(R.id.newOfficeShujuSearch);
            footer = mInflater.inflate(R.layout.listview_footer, null);
            if (mManager == null) {
                mManager = getFragmentManager();
            }
            LinearLayout title = (LinearLayout) mView.findViewById(R.id.newOfficeShujuTitle);//隐藏标题
            title.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //listViewAll监听
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

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (id == -1) {
                            LoadDatas();
                            return;
                        }
                        if (!data.get(position).getBak5().equals("")) {
                            Intent intent = new Intent(getActivity(), NewOfficeshujuActivity.class);
                            intent.putExtra("subjectcode", data.get(position).getBak5());
                            intent.putExtra("subjectname",data.get(position).getTitle());
                            getActivity().startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etContent.getText().equals("")) {
                        return;
                    }
                    Intent intent = new Intent(getActivity(), NewOfficeshujuActivity.class);
                    intent.putExtra("searchCode", etContent.getText()+"");
                    getActivity().startActivity(intent);
                }
            });


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

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                //getDataASC
                String _url = ServerInfo.ServerBKRoot+"/info/getDataASC/ciistkey/" + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/20";
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
//                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

    /**
     * 生成一个数据
     *
     * @param count 表示图表中有多少个坐标点
     * @param range 用来生成range以内的随机数
     * @return
     */
    private LineData getLineData(int count, float range) {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add("" + i);
        }

        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        int temp = 0;
        for (int i = 0; i < count; i++) {
            String num = data.get(i).getAuthor();
            if (data.get(i).getAuthor().equals(".")) {
                num = "0";
                float value = (float) Integer.parseInt(num);
                yValues.add(new Entry(value, i));
                temp++;
            } else {
                float value = MathHelper.StringToFloat(num); //(float) Integer.parseInt(num);
                yValues.add(new Entry(value, i));
            }
            if (temp == data.size()) {
                mHandler.sendEmptyMessage(999);
            }

        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, "数据统计" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }

    // 设置显示的样式
    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框
        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("亲，这里没有数据哦！");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//

        lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineChart.setData(lineData); // 设置数据

        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(2500); // 立即执行的动画,x轴
    }

}
