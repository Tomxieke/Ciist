package com.ciist.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.ItemInfoAdapter;

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

public class NewOfficeFragmentZhongdianxiangmuListActivity extends AppCompatActivity {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static final int CREATE_NEW_TASK_FLAG = 1;

    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mRemark1 = "";
    private String mRemark2 = "";
    private String mRemark3 = "";
    private String mRemark4 = "";
    private String mRemark5 = "";
    private String mAction = "";


    private String mSubjectCode = "";
    private String mSubjectName = "";

    private boolean isFullScreen = false;
    private int headerHeight = 0;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_GPS = 90;//

    private ProgressBar waiting;
    private ListView coverlistView;
    private List<ItemInfo> data;

    private ItemInfoAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private View footer;
    private Context context;
    private InternetOfSubject0Thread subjectThread;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
                    coverlistView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ItemInfo>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
                        coverlistView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                        return;
                    }
                    if (result.getSimpleItemObj().size() < 10) {
                        coverlistView.removeFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getSimpleItemObj());
                    if (adapter == null) {
                        int resID = R.layout.listview_item;
                        adapter = new ItemInfoAdapter(context, resID, data);
                        coverlistView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
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
                    coverlistView.removeFooterView(footer);
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_office_fragment_zhongdianxiangmu_list_activity);
        waiting = (ProgressBar) findViewById(R.id.waitloading);
        Intent intent = this.getIntent();
        mSubjectCode = intent.getStringExtra("subjectcode");
        mSubjectName = intent.getStringExtra("subjectname");

        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");
        mRemark1 = intent.getStringExtra("remark1");
        mRemark2 = intent.getStringExtra("remark2");
        mRemark3 = intent.getStringExtra("remark3");
        mRemark4 = intent.getStringExtra("remark4");
        mRemark5 = intent.getStringExtra("remark5");
        mAction = intent.getStringExtra("action");

        isFullScreen = intent.getBooleanExtra("full", false);
        context = getApplicationContext();

        if (!isFullScreen) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(mRemark2+" "+mRemark3+" "+ mSubjectName);
            toolbar.setVisibility(View.VISIBLE);
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            toolbar.setBackgroundColor(Color.rgb(r, g, b));
            setSupportActionBar(toolbar);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }

        coverlistView = (ListView) findViewById(R.id.newoffice_zhongdianxiangmu_activity_listview);
        coverlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if (id == -1) {
                        LoadDatas();
                        return;
                    }
                    View _view = view.findViewById(R.id.simpleTextView1);
                    TextView tmp = (TextView) _view;
                    ItemInfo tmpobj = (ItemInfo) tmp.getTag();
                    String linkType = tmpobj.getBak4();
                    String subjectLink = tmpobj.getBak5();
                    if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                        Intent tmpIntent = new Intent(context, WebActivity.class);
                        tmpIntent.putExtra("URL", tmpobj.getLinkurl());
                        tmpIntent.putExtra("TITLE", tmpobj.getTitle());
                        tmpIntent.putExtra("PUBDATE", tmpobj.getPubdate());
                        tmpIntent.putExtra("source", tmpobj.getInfosource());
                        tmpIntent.putExtra("author", tmpobj.getInfoauthor());
                        tmpIntent.putExtra("infotype", tmpobj.getInfotype());
                        tmpIntent.putExtra("ROOT", "#Content");
                        tmpIntent.putExtra("blank", true);
                        tmpIntent.putExtra("telnum", tmpobj.getTelnum());
                        tmpIntent.putExtra("latidue_e", tmpobj.getLatidue());
                        tmpIntent.putExtra("longtidue_e", tmpobj.getLongtidue());
                        tmpIntent.putExtra("latidue_b",0);
                        tmpIntent.putExtra("longtidue_b", 0);

                        tmpIntent.putExtra("IsDuban",true);
                        tmpIntent.putExtra("depcode",tmpobj.getDepcode());
                        tmpIntent.putExtra("identify",mIdentify);
                        tmpIntent.putExtra("username",mUsername);
                        tmpIntent.putExtra("selfids",mSelfids);

                        startActivity(tmpIntent);
                    } else {
                        if (linkType != null && linkType == "ciist1") {

                        } else {
                            Intent tmpIntent = new Intent(NewOfficeFragmentZhongdianxiangmuListActivity.this, NewOfficeFragmentZhongdianxiangmuListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("subjectname", tmpobj.getTitle());
                            bundle.putString("subjectcode", tmpobj.getBak5());

                            bundle.putString("identify", mIdentify);
                            bundle.putString("username", mUsername);
                            bundle.putString("selfids", mSelfids);

                            bundle.putString("action", "n");
                            bundle.putString("remark1", mRemark1);
                            bundle.putString("remark2", mRemark2);
                            bundle.putString("remark3", mRemark3);
                            bundle.putString("remark4", mRemark4);
                            bundle.putString("remark5", mRemark5);


                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        iniview();
        LoadDatas();
    }

    void iniview() {
        try {
            footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
            if (footer == null) {
                return;
            }
            coverlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    try {
                        if (coverlistView.getFooterViewsCount() <= 0) {
                            return;
                        }
                        int lastItemIndexID = firstVisibleItem + visibleItemCount;
                        if (lastItemIndexID <= 0) return;
                        if (lastItemIndexID == totalItemCount) {
                            View lastItemView = coverlistView.getChildAt(coverlistView.getChildCount() - 1);
                            headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                            int bottomChazhi = Math.abs(coverlistView.getBottom() - lastItemView.getBottom());
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
                String _url="";
                if(mAction.equals("n")) {
                    _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/10";
                }else if(mAction.equals("k")){
                    if(mRemark1.isEmpty())mRemark1="1";
                    if(mRemark2.isEmpty())mRemark2="1";
                    if(mRemark3.isEmpty())mRemark3="1";
                    if(mRemark4.isEmpty())mRemark4="1";
                    if(mRemark5.isEmpty())mRemark5="1";
                    _url = ServerInfo.ServerBKRoot+"/info/getKeyProjectData/ciistkey/"+mAction+ "/" + currentPageInfo.getPageIndex() + "/10/"+mRemark1+"/"+mRemark2+"/"+mRemark3+"/"+mRemark4+"/"+mRemark5;
                }
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
                    tmpIteminfo.setInfotype("noticesimplemode");
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
                    tmpIteminfo.setDepcode(jsonObj.getString("depcode"));
                    tmpIteminfo.setIspub(jsonObj.getInt("ispub"));
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
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }







}
