package com.ciist.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ciist.entities.ItemInfo;
import com.ciist.entities.KeyProjectCountObject;
import com.ciist.entities.KeyValueObject;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.helper.ChartHelper;
import com.ciist.toolkits.ItemInfo_oaAdapter;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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
public class NewOfficeFragmentZhongdianxiangmuLev1 extends android.support.v4.app.Fragment {

    private HorizontalBarChart mChart_Top;
    //    private HorizontalBarChart mChart_Bottom;
    //    private BarChart mChart_Top;
    private View mView;
    private Context context;
    private ViewGroup mContainer;
    private LayoutInflater mInflater;
    private ProgressBar waiting;
    //  private ListView mlistView;
    private Button mBtnCreateTask;
    private List<ItemInfo> data;
    //  private View footer;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_CHART = 801;//
    private static final int MSG_CHART_1 = 802;//
    private static final int MSG_00 = 0x25;

    private ItemInfo_oaAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;

    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mAction;
    private String mTitle;
    private String mCondition = "A";
    private boolean IsLock=false;

    private RadioButton xiangmu_2015;
    private RadioButton xiangmu_2016;
   // private RadioGroup mYearRadio;

    public NewOfficeFragmentZhongdianxiangmuLev1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        mContainer = container;
        mInflater = inflater;
        mAction = "jb";
        mView = inflater.inflate(R.layout.new_office_fragment_zhongdianxiangmu_lev1, container, false);
        initInputParams();
        initCtrls();
        createChartTop();
        return mView;
    }

    /**
     * 得到上一个页面传下来的值
     */
    void initInputParams() {
        try {

            // Bundle data = new Bundle();
            // data.putString("TEXT", "这是Activiy通过Bundle传递过来的值");
            //  fragment1.setArguments(data);//通过Bundle向Activity中传递值
            Bundle data = getArguments();//获得从activity中传递过来的值
            mIdentify = data.getString("identify");
            mUsername = data.getString("username");
            mSelfids = data.getString("selfids");
            mTitle = data.getString("title", "重点项目");
            mAction = data.getString("action", "jb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化控件
     */
    void initCtrls() {
        try {
            waiting = (ProgressBar) mView.findViewById(R.id.waitloading);
            xiangmu_2015 = (RadioButton) mView.findViewById(R.id.xiangmu_2015);
            xiangmu_2016 = (RadioButton) mView.findViewById(R.id.xiangmu_2016);
            //默认显示优先选择2xiangmu_015
            xiangmu_2015.setChecked(true);

            xiangmu_2015.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChart_Top.setVisibility(View.VISIBLE);
                    createChartTop();
                }
            });
            xiangmu_2016.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChart_Top.setVisibility(View.VISIBLE);
                    createChartTop();
                }
            });

            //----------------------


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * handler 处理
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
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
                    //   Log.e("test","---mListview---"+mlistView+"---footer---"+footer);
                    //   mlistView.removeFooterView(footer);
                    Toast.makeText(context, "亲,没有查找到2016年录入的数据!", Toast.LENGTH_SHORT).show();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    mChart_Top.setVisibility(View.GONE);
                    break;
                case MSG_CHART:

                    break;
                case MSG_CHART_1:
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    BarData mPieData_1 = (BarData) msg.obj;
                    ChartHelper.showHBarChart(mChart_Top, mPieData_1, "重点项目");
                    break;
                case MSG_00:
                    mChart_Top.setVisibility(View.INVISIBLE);
                    Toast.makeText(context,"未录取数据",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void setUrl(String keyCode,String year){
        _url = ServerInfo.ServerBKRoot + "/info/getKeyProjectCountByYear/ciistkey/1/" + mIdentify + "/A/" + keyCode+"/"+year;

        //    Log.e("test","----+_url----"+_url);
    }
    private String _url = "";

    /**
     * 获取数据并解析json
     */
    private class InternetOfChartThread extends Thread {

        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            if (xiangmu_2015.isChecked()){
                setUrl("D0C02CD2-BBC4-4DE8-917D-E68E0B434337","2015");
            }else if(xiangmu_2016.isChecked()){
                setUrl("D0C02CD2-BBC4-4DE8-917D-E68E0B434337","2016");
                //    mHandler.sendEmptyMessage(MSG_00);
                //  return;
            }
            try {
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
                //    Log.e("test","----+jsonString----"+jsonString);
                if (jsonString == "") {
                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
                    return;
                }
                JSONArray jsonArray = new JSONArray(jsonString);
                if (jsonArray == null || jsonArray.length() <= 0) {
                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
                    return;
                }
                ArrayList<KeyValueObject> plev1 = new ArrayList<KeyValueObject>();
                ArrayList<KeyValueObject> plev2 = new ArrayList<KeyValueObject>();
                String plev1Str = "";
                String plev2Str = "";
                ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String _tmp = jsonObj.getString("lev");
                    if (_tmp.isEmpty()) _tmp = "未分类";
                    if (!plev1Str.contains(_tmp)) {
                        plev1Str += _tmp + ";";
                        KeyValueObject kvo = new KeyValueObject();
                        kvo.setKeyName(_tmp);
                        plev1.add(kvo);
                        xValues.add(kvo.getKeyName());
                    }
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String _tmp = jsonObj.getString("ptype");
                    if (_tmp.isEmpty()) _tmp = "未分类";
                    if (!plev2Str.contains(_tmp)) {
                        plev2Str += _tmp + ";";
                        KeyValueObject kvo = new KeyValueObject();
                        kvo.setKeyName(_tmp);
                        plev2.add(kvo);
                    }
                }

//                for (int i1 = 0; i1 < plev1.size(); i1++) {
//                    KeyValueObject obj1 = new KeyValueObject();
//                    obj1 = plev1.get(i1);
//                    ArrayList<KeyValueObject> nextObj = new ArrayList<KeyValueObject>();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObj = jsonArray.getJSONObject(i);
//                        String _tmpptype = jsonObj.getString("ptype");
//                        String _tmplev = jsonObj.getString("lev");
//                        int _tmpnum = jsonObj.getInt("cnums");
//                        if (_tmpptype.isEmpty()) _tmpptype = "未分类";
//                        if (_tmplev.isEmpty()) _tmplev = "未分类";
//
//                        if (obj1.getKeyName().equals(_tmplev)) {
//                            KeyValueObject kvo = new KeyValueObject();
//                            kvo.setKeyName(_tmpptype);
//                            kvo.setKeyValues(_tmpnum);
//                            nextObj.add(kvo);
//                        }
//                    }
//                    obj1.setObj(nextObj);
//                }
                ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
                for (int k = 0; k < plev2.size(); k++) {
                    ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
                    int entryIndex = 0;
                    for (int m = 0; m < plev1.size(); m++) {
                        int _tmpnum = getKeyProjectCountValue(jsonArray, plev1.get(m).getKeyName(), plev2.get(k).getKeyName());
                        KeyValueObject kvo = new KeyValueObject();
                        kvo.setUpKeyName(plev1.get(m).getKeyName());
                        kvo.setKeyName(plev2.get(k).getKeyName());
                        kvo.setKeyValues(_tmpnum);
                        BarEntry _en = new BarEntry(_tmpnum, entryIndex++, kvo);
                        yValues.add(_en);
                    }
                    BarDataSet ds = new BarDataSet(yValues, plev2.get(k).getKeyName());
                    int r = (int) (Math.random() * 255);
                    int g = (int) (Math.random() * 255);
                    int b = (int) (Math.random() * 255);
                    ds.setColor(Color.rgb(r, g, b));
                    sets.add(ds);
                }

                BarData d = new BarData(xValues, sets);
                mHandler.obtainMessage(MSG_CHART_1, d).sendToTarget();
                //1


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }



    int getKeyProjectCountValue(JSONArray jsonArray, String lev, String ptype) {
        for (int j = 0; j < jsonArray.length(); j++) {
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(j);
                String _tmpptype = jsonObj.getString("ptype");
                String _tmplev = jsonObj.getString("lev");
                int _tmpnum = jsonObj.getInt("cnums");
                if (_tmpptype.isEmpty()) _tmpptype = "未分类";
                if (_tmplev.isEmpty()) _tmplev = "未分类";
                if (lev.equals(_tmplev) && ptype.equals(_tmpptype)) {
                    return _tmpnum;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    /**
     * 创建Chart图表和顶部等待的内容
     */
    void createChartTop() {
        mChart_Top = (HorizontalBarChart) mView.findViewById(R.id.newOfficeHBarChartZhongdianxiangmu_Top);
        BarData mPieData = getBarData();
        ChartHelper.showHBarChart(mChart_Top, mPieData, "数据加载中");
        mChart_Top.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                mCondition = String.valueOf(entry.getXIndex());
                KeyValueObject kvo = (KeyValueObject) entry.getData();
//                Toast.makeText(context, kvo.getUpKeyName() + "," + kvo.getKeyName(), Toast.LENGTH_SHORT).show();
            //    Log.e("test","---kvo---"+ kvo);
                if (kvo == null){
                    return;
                }
                if (kvo.getKeyValues() > 0 && IsLock==false) {
                    IsLock=true;
                    Intent tmpIntent1 = new Intent(context, NewOfficeFragmentZhongdianxiangmuLev2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("identify", mIdentify);
                    bundle.putString("username", mUsername);
                    bundle.putString("selfids", mSelfids);
                    bundle.putString("title", kvo.getUpKeyName());
                    bundle.putString("action", kvo.getUpKeyName());
                    tmpIntent1.putExtras(bundle);
                    startActivity(tmpIntent1);
                    IsLock=false;
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        new InternetOfChartThread().start();
    }


    /**
     * 得到BarChart的数据
     * @return
     */
    private BarData getBarData() {
        int dataSets = 1;
        int count = 12;
        int range = 20000;
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        for (int i = 0; i < dataSets; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int j = 0; j < count; j++) {
//                BarEntry be=new BarEntry(12,2,entries);
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            BarDataSet ds = new BarDataSet(entries, "第一组");
//            ds.setColors(ColorTemplate.COLORFUL_COLORS);
            ds.setColor(Color.BLUE);
            sets.add(ds);
        }

        for (int i = 0; i < dataSets; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int j = 0; j < count; j++) {
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            BarDataSet ds = new BarDataSet(entries, "第二组");
//            ds.setColors(ColorTemplate.JOYFUL_COLORS);
            ds.setColor(Color.GREEN);
            sets.add(ds);
        }

        for (int i = 0; i < dataSets; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int j = 0; j < count; j++) {
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            BarDataSet ds = new BarDataSet(entries, "第三组");
//            ds.setColors(ColorTemplate.LIBERTY_COLORS);
            ds.setColor(Color.YELLOW);
            sets.add(ds);
        }
        for (int i = 0; i < dataSets; i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            for (int j = 0; j < count; j++) {
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            BarDataSet ds = new BarDataSet(entries, "第四组");
//            ds.setColors(ColorTemplate.PASTEL_COLORS);
            ds.setColor(Color.RED);
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
