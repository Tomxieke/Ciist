package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ciist.entities.ItemInfo;
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

public class NewOfficeFragmentZhongdianxiangmuLev2Activity extends AppCompatActivity {
    private HorizontalBarChart mChart_Top;
    private Context context;
    private ProgressBar waiting;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_CHART = 801;//
    private static final int MSG_CHART_1 = 802;//
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_office_fragment_zhongdianxiangmu_lev2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();
        mAction = "jb";
        initInputParams();
        initCtrls();
        createChartTop();
    }

    void initInputParams() {
        try {
            Intent intent = this.getIntent();
            mIdentify = intent.getStringExtra("identify");
            mUsername = intent.getStringExtra("username");
            mSelfids = intent.getStringExtra("selfids");
            mTitle = intent.getStringExtra("title");
            mAction = intent.getStringExtra("action");
            setTitle(mTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initCtrls() {
        try {
            waiting = (ProgressBar) findViewById(R.id.waitloading);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
                case MSG_CHART:

                    break;
                case MSG_CHART_1:
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    BarData mPieData_1 = (BarData) msg.obj;
                    ChartHelper.showHBarChart(mChart_Top, mPieData_1, mTitle);
                    break;
            }
        }
    };


    private class InternetOfChartThread extends Thread {

        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            String _url = "";
            try {
                _url = ServerInfo.ServerBKRoot + "/info/getKeyProjectCount/ciistkey/2/" + mIdentify + "/" + mAction + "/D0C02CD2-BBC4-4DE8-917D-E68E0B434337";
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

    void createChartTop() {
        mChart_Top = (HorizontalBarChart) findViewById(R.id.newOfficeHBarChartZhongdianxiangmuLev2_Top);
        BarData mPieData = getBarData();
        ChartHelper.showHBarChart(mChart_Top, mPieData, "数据加载中");
        mChart_Top.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                mCondition = String.valueOf(entry.getXIndex());
                KeyValueObject kvo = (KeyValueObject) entry.getData();
                if (kvo.getKeyValues() > 0 && IsLock==false) {
                    IsLock=true;
                    Intent tmpIntent = new Intent(NewOfficeFragmentZhongdianxiangmuLev2Activity.this, NewOfficeFragmentZhongdianxiangmuListActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("subjectname", kvo.getKeyName());
                    bundle.putString("subjectcode", kvo.getKeyName());
                    bundle.putString("identify", mIdentify);
                    bundle.putString("username", mUsername);
                    bundle.putString("selfids", mSelfids);
                    bundle.putString("action", "k");
                    bundle.putString("remark1", kvo.getKeyName());
                    bundle.putString("remark2", mTitle);
                    bundle.putString("remark3", kvo.getUpKeyName());
                    bundle.putString("remark4", "");
                    bundle.putString("remark5", "");
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                    IsLock=false;
                }

            }

            @Override
            public void onNothingSelected() {

            }
        });

        new InternetOfChartThread().start();
    }


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
