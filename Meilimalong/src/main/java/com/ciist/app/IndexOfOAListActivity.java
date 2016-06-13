package com.ciist.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.ItemInfoAdapter;
import com.ciist.toolkits.ItemInfo_oaAdapter;

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

public class IndexOfOAListActivity extends AppCompatActivity {

    private String mTitle;
    private Context context;
    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mAction;
    private ProgressBar waiting;
    private Toolbar toolbar;
    private ListView mlistView;
    private String mUrl;

    private List<ItemInfo> data;
    private View footer;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private ItemInfo_oaAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;
    private InternetOfSubject0Thread subjectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_of_oalist_activity);
        context = getApplicationContext();
        initInputParams();
        initCtrls();
        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        registerListening();
        LoadDatas();
    }

    void initInputParams() {
        try {
            Intent intent = this.getIntent();
            mIdentify = intent.getStringExtra("identify");
            mUsername = intent.getStringExtra("username");
            mSelfids = intent.getStringExtra("selfids");
            mTitle = intent.getStringExtra("title");
            mAction = intent.getStringExtra("action");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initCtrls() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(mTitle);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mlistView = (ListView) findViewById(R.id.coverlist);
            waiting = (ProgressBar) findViewById(R.id.waitloading);
            footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void registerListening() {
        try {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int a = item.getItemId();
                    return false;
                }
            });

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
                        View _view = view.findViewById(R.id.list_view_oa_default_text);
                        TextView tmp = (TextView) _view;
                        ItemInfo tmpobj = (ItemInfo) tmp.getTag();

                        Intent tmpIntent1 = new Intent(IndexOfOAListActivity.this, chatting_item_msg_index_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("identify", mIdentify);
                        bundle.putString("username", mUsername);
                        bundle.putString("selfids", mSelfids);
                        bundle.putString("title", tmpobj.getTitle());
                        bundle.putString("action", tmpobj.getBak5());
                        bundle.putString("actiontype", mAction);
                        bundle.putString("tkstate",tmpobj.getAuthor());

                        bundle.putString("taskids", tmpobj.getLinkurl());
                        tmpIntent1.putExtras(bundle);
                        startActivity(tmpIntent1);


//                        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
////                            TODO
//
//                            //test
//                            Intent tmpIntent1 = new Intent(IndexOfOAListActivity.this, chatting_item_msg_index_Activity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("subjectname", tmpobj.getTitle());
//                            bundle.putString("subjectcode", tmpobj.getBak5());
//                            tmpIntent1.putExtras(bundle);
//                            startActivity(tmpIntent1);
//                            return;
//
//                            //test
//
////
////                            Intent tmpIntent = new Intent(context, WebActivity.class);
////                            tmpIntent.putExtra("URL", tmpobj.getLinkurl());
////                            tmpIntent.putExtra("TITLE", tmpobj.getTitle());
////                            tmpIntent.putExtra("PUBDATE", tmpobj.getPubdate());
////                            tmpIntent.putExtra("source", tmpobj.getInfosource());
////                            tmpIntent.putExtra("author", tmpobj.getInfoauthor());
////                            tmpIntent.putExtra("infotype", tmpobj.getInfotype());
////                            tmpIntent.putExtra("ROOT", "#Content");
////                            tmpIntent.putExtra("blank", true);
////                            tmpIntent.putExtra("telnum", tmpobj.getTelnum());
////                            tmpIntent.putExtra("latidue_e", tmpobj.getLatidue());
////                            tmpIntent.putExtra("longtidue_e", tmpobj.getLongtidue());
////                            tmpIntent.putExtra("latidue_b", "0");
////                            tmpIntent.putExtra("longtidue_b", "0");
////                            startActivity(tmpIntent);
//                        } else {
//                            if (linkType != null && linkType == "ciist1") {
//
//                            } else {
////                                TODO
//                                Intent tmpIntent = new Intent(IndexOfOAListActivity.this, IndexOfOAListActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putString("subjectname", tmpobj.getTitle());
//                                bundle.putString("subjectcode", tmpobj.getBak5());
//                                tmpIntent.putExtras(bundle);
//                                startActivity(tmpIntent);
//                            }
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
                        int resID = R.layout.listview_item_oa;
                        adapter = new ItemInfo_oaAdapter(context, resID, data);
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
                    Toast.makeText(context, "亲,还没有数据呢，开始使用吧!", Toast.LENGTH_SHORT).show();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            String _url = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                if (mAction.toUpperCase().equals("zb".toUpperCase())) {
                    _url = ServerInfo.ServerBKRoot + "/info/getTaskToMe/ciistkey/" + mIdentify + "/" + currentPageInfo.getPageIndex() + "/10";
                } else if (mAction.toUpperCase().equals("jb".toUpperCase())) {
                    _url = ServerInfo.ServerBKRoot + "/info/getTaskFromMe/ciistkey/" + mIdentify + "/" + currentPageInfo.getPageIndex() + "/10";
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
                    ItemInfo tmpIteminfo = new ItemInfo();
                    if (mAction.toUpperCase().equals("zb".toUpperCase())) {
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
                    } else if (mAction.toUpperCase().equals("jb".toUpperCase())) {
                        tmpIteminfo.setTitle(jsonObj.getString("Task_zh"));
                        tmpIteminfo.setPubdate(jsonObj.getString("Tkpubdate"));
                        tmpIteminfo.setLinkurl(jsonObj.getString("Selfids"));
                        tmpIteminfo.setInfosource(jsonObj.getString("Tktype"));
                        tmpIteminfo.setInfoauthor(jsonObj.getString("Tkpuber"));
                        tmpIteminfo.setInfotype("BBB");
                        tmpIteminfo.setBak1("汇报" + jsonObj.getInt("exenums") + "次，催办" + jsonObj.getInt("urgenums") + "次");
                        int _rday = jsonObj.getInt("rundays");
                        if (_rday == 0) {
                            tmpIteminfo.setBak2("今日到期");
                        } else if (_rday > 0) {
                            tmpIteminfo.setBak2("还剩" + _rday + "天");
                        } else {
                            tmpIteminfo.setBak2("超期" + Math.abs(_rday) + "天");
                        }
                        tmpIteminfo.setBak3(jsonObj.getString("Tkbegindate") + "至" + jsonObj.getString("Tkenddate"));
                        tmpIteminfo.setBak4("");
                        tmpIteminfo.setBak5("");
                        tmpIteminfo.setSource("");
                        tmpIteminfo.setAuthor(String.valueOf(jsonObj.getInt("Tkstate")));
                        if (jsonObj.getInt("exenewnums") > 0) {
                            tmpIteminfo.setVisitCount(0);
                        } else {
                            tmpIteminfo.setVisitCount(1);
                        }
                        tmpIteminfo.setTelnum(jsonObj.getString("Selfids"));
                        tmpIteminfo.setLatidue(0);
                        tmpIteminfo.setLongtidue(0);
                    }

                    data.add(tmpIteminfo);
                }
                //json
//            String zixunUrl = "http://149.212.154:2015/meilimalong/search/index/" + mSubjectCode + "," + currentPageInfo.getPageIndex() + ",10";
//            Document doc;
//            String RootNode = "#list";
//            try {
//                doc = Jsoup.connect(zixunUrl).timeout(9000).post();
//                Document content = Jsoup.parse(doc.toString());
//                if (content.toString().contains("No Data")) {
//                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
//                    return;
//                }
//                Elements divs = content.select(RootNode);
//                Document divcontions = Jsoup.parse(divs.toString());
//                Elements element = divcontions.getElementsByTag("li");
//                for (Element links : element) {
//                    String title = links.select("a").text();
//                    if (title == "") continue;
//                    String link = links.select("a").attr("href").trim();
//                    String linkUrl = "http://.149.212.154:2015" + link;
//                    String linkDate = links.select("a").attr("createdate").trim();
//                    String _source = links.select("a").attr("sourse").trim();
//                    String _author = links.select("a").attr("author").trim();
//                    String _image = links.select("a").attr("image").trim();
//                    String _type = links.select("a").attr("type").trim();
//                    String _bak1 = links.select("a").attr("Remark1").trim();
//                    String _bak2 = links.select("a").attr("Remark2").trim();
//                    String _bak3 = links.select("a").attr("Remark3").trim();
//                    String _bak4 = links.select("a").attr("Remark4").trim();
//                    String _bak5 = links.select("a").attr("Remark5").trim();
//                    ContentValues values = new ContentValues();
//                    values.put("Title", title);
//                    values.put("Url", linkUrl);
//                    ItemInfo tmpIteminfo = new ItemInfo();
//                    tmpIteminfo.setTitle(title);
//                    tmpIteminfo.setPubdate(linkDate);
//                    tmpIteminfo.setLinkurl(linkUrl);
//                    tmpIteminfo.setImgsrc(_image);
//                    tmpIteminfo.setInfosource(_source);
//                    tmpIteminfo.setInfoauthor(_author);
//                    tmpIteminfo.setInfotype(_type);
//                    tmpIteminfo.setBak1(_bak1);
//                    tmpIteminfo.setBak2(_bak2);
//                    tmpIteminfo.setBak3(_bak3);
//                    tmpIteminfo.setBak4(_bak4);
//                    tmpIteminfo.setBak5(_bak5);
//                    tmpIteminfo.setSource(_source);
//                    tmpIteminfo.setAuthor(_author);
//                    data.add(tmpIteminfo);
//                }
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


}
