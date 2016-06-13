package com.ciist.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
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
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class IndexOfOAMainActivity extends AppCompatActivity {
    public static int RESULT_REFREASH = 9;
    private static final int CREATE_NEW_TASK_FLAG = 1;
    private Context context;
    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private ProgressBar waiting;
    private ListView coverlistView;
    private List<ItemInfo> data;
    private View footer;
    private ImageButton btnCreateTask;
    private Toolbar toolbar;


    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private ItemInfo_oaAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case CREATE_NEW_TASK_FLAG:
                    if (resultCode == RESULT_REFREASH) {
                        RefreashData();
                    }
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InternetOfSubject0Thread subjectThread;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:

//                    coverlistView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ItemInfo>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
//                        coverlistView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                        return;
                    }
                    if (result.getChatMsgEntityDatas().size() < 10) {
                        if (coverlistView.getFooterViewsCount() > 0)
                            coverlistView.removeFooterView(footer);
                    } else {
                        if (coverlistView.getFooterViewsCount() <= 0)
                            coverlistView.addFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getSimpleItemObj());
                    if (adapter == null) {
                        int resID = R.layout.listview_item_oa;
                        adapter = new ItemInfo_oaAdapter(context, resID, data);
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

    void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.index_of_oa_main_toolbar);
        toolbar.setTitle("魅力马龙公共管理系统");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_index_oa_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exitOA) {
            SharedPreferences mySharedPreferences = getSharedPreferences("passport", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("identify", "0");
            editor.commit();
            Toast.makeText(context, "请重新登陆", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (item.getItemId() == R.id.action_refreashOA) {
            RefreashData();
            Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.action_changePwdOA){
            //TODO
            Intent _int=new Intent(IndexOfOAMainActivity.this,IndexOfOAChangePwdActivity.class);
            startActivity(_int);
        }

        return super.onOptionsItemSelected(item);
    }

    void RefreashData() {
        try {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_of_oa_main_activity);
        initToolbar();
        waiting = (ProgressBar) findViewById(R.id.waitloading);
        btnCreateTask = (ImageButton) findViewById(R.id.btnNewTaskMake);
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(IndexOfOAMainActivity.this, IndexOfOACreateNewTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUsername);
                bundle.putString("selfids", mSelfids);
                tmpIntent.putExtras(bundle);
                startActivityForResult(tmpIntent, CREATE_NEW_TASK_FLAG);
//                startActivity(tmpIntent);
            }
        });
//        btnRefreashTask = (Button) findViewById(R.id.btnRefreashTask);
//        btnRefreashTask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (waiting != null) {
//                    waiting.setVisibility(View.VISIBLE);
//                }
//                LoadDatas();
//            }
//        });
        Intent intent = this.getIntent();
        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");
        context = getApplicationContext();
        coverlistView = (ListView) findViewById(R.id.coverlist);

        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        initView();
        LoadDatas();
    }

    void initView() {
        try {
            footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
            if (footer == null) {
                return;
            }
            coverlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {

                        if (id == -1) {
                            return;
                        }
                        View _view = view.findViewById(R.id.list_view_oa_default_text);
                        TextView tmp = (TextView) _view;
                        ItemInfo tmpobj = (ItemInfo) tmp.getTag();
                        String source = tmpobj.getSource();
                        String _infoType=tmpobj.getInfotype();
                        if (source.equals("zb")) {
                            Intent tmpIntent = new Intent(IndexOfOAMainActivity.this, IndexOfOAListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("identify", mIdentify);
                            bundle.putString("username", mUsername);
                            bundle.putString("selfids", mSelfids);
                            bundle.putString("title", tmpobj.getTitle());
                            bundle.putString("action", tmpobj.getSource());
                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        } else if (source.equals("jb")) {
                            Intent tmpIntent = new Intent(IndexOfOAMainActivity.this, IndexOfOAListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("identify", mIdentify);
                            bundle.putString("username", mUsername);
                            bundle.putString("selfids", mSelfids);
                            bundle.putString("title", tmpobj.getTitle());
                            bundle.putString("action", tmpobj.getSource());
                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        }
                        else if (_infoType.equals("CCC")) {
                            Intent tmpIntent = new Intent(IndexOfOAMainActivity.this, IndexOfOACoverStyleActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("identify", mIdentify);
                            bundle.putString("username", mUsername);
                            bundle.putString("selfids", mSelfids);
                            bundle.putString("title", tmpobj.getTitle());
                            bundle.putString("action", tmpobj.getSource());
                            bundle.putString("subjectname", tmpobj.getTitle());
                            bundle.putString("subjectcode", tmpobj.getLinkurl());
                            tmpIntent.putExtras(bundle);
                            startActivity(tmpIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            coverlistView.setOnScrollListener(new AbsListView.OnScrollListener()

            {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                     int totalItemCount) {
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
//            System.out.println(e.getMessage().toString());
        }
    }

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.ServerBKRoot + "/core/TaskLoutlineAboutme/ciistkey/" + mIdentify;
//                http get begin
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
                    tmpIteminfo.setTitle("交办工作");
                    tmpIteminfo.setInfotype("AAA");
                    tmpIteminfo.setBak1(String.valueOf(jsonObj.getInt("jbnums")));
                    tmpIteminfo.setBak2(String.valueOf(jsonObj.getInt("jbendnums")));
                    tmpIteminfo.setBak3(String.valueOf(jsonObj.getInt("jbnewnums")));
                    tmpIteminfo.setSource("jb");
                    data.add(tmpIteminfo);
                    ItemInfo tmpIteminfo2 = new ItemInfo();
                    tmpIteminfo2.setTitle("承办工作");
                    tmpIteminfo2.setInfotype("AAA");
                    tmpIteminfo2.setBak1(String.valueOf(jsonObj.getInt("zbnums")));
                    tmpIteminfo2.setBak2(String.valueOf(jsonObj.getInt("zbendnums")));
                    tmpIteminfo2.setBak3(String.valueOf(jsonObj.getInt("zbnewnums")));
                    tmpIteminfo2.setSource("zb");
                    data.add(tmpIteminfo2);
                    ItemInfo tmpIteminfo3=new ItemInfo();
                    tmpIteminfo3.setTitle("重点项目");
                    tmpIteminfo3.setInfotype("CCC");
                    tmpIteminfo3.setBak1("");
                    tmpIteminfo3.setBak2("");
                    tmpIteminfo3.setBak3("");
                    tmpIteminfo3.setSource("zdxm");
                    tmpIteminfo3.setImgsrc(String.valueOf(R.drawable.home_list1));
                    tmpIteminfo3.setLinkurl("D0C02CD2-BBC4-4DE8-917D-E68E0B434337");
                    data.add(tmpIteminfo3);
                    ItemInfo tmpIteminfo4=new ItemInfo();
                    tmpIteminfo4.setTitle("拟签约项目");
                    tmpIteminfo4.setInfotype("CCC");
                    tmpIteminfo4.setBak1("");
                    tmpIteminfo4.setBak2("");
                    tmpIteminfo4.setBak3("");
                    tmpIteminfo4.setSource("nqyxm");
                    tmpIteminfo4.setImgsrc(String.valueOf( R.drawable.home_list2));
                    tmpIteminfo4.setLinkurl("5F4487B7-13FF-4F55-B0F3-5E8AE57AC031");
                    data.add(tmpIteminfo4);
                    ItemInfo tmpIteminfo5=new ItemInfo();
                    tmpIteminfo5.setTitle("已签约项目");
                    tmpIteminfo5.setInfotype("CCC");
                    tmpIteminfo5.setBak1("");
                    tmpIteminfo5.setBak2("");
                    tmpIteminfo5.setBak3("");
                    tmpIteminfo5.setSource("yqyxm");
                    tmpIteminfo5.setImgsrc(String.valueOf(R.drawable.home_list3));
                    tmpIteminfo5.setLinkurl("82679EB8-419C-46D0-99EA-67876486FB23");
                    data.add(tmpIteminfo5);
                    ItemInfo tmpIteminfo6=new ItemInfo();
                    tmpIteminfo6.setTitle("重点跟进项目");
                    tmpIteminfo6.setInfotype("CCC");
                    tmpIteminfo6.setBak1("");
                    tmpIteminfo6.setBak2("");
                    tmpIteminfo6.setBak3("");
                    tmpIteminfo6.setSource("zdgjxm");
                    tmpIteminfo6.setImgsrc(String.valueOf(R.drawable.home_list4));
                    tmpIteminfo6.setLinkurl("557C6885-8D79-40CA-A0DD-E4BE0DD7023B");
                    data.add(tmpIteminfo6);
                    ItemInfo tmpIteminfo7=new ItemInfo();
                    tmpIteminfo7.setTitle("已剔除项目");
                    tmpIteminfo7.setInfotype("CCC");
                    tmpIteminfo7.setBak1("");
                    tmpIteminfo7.setBak2("");
                    tmpIteminfo7.setBak3("");
                    tmpIteminfo7.setSource("ytcxm");
                    tmpIteminfo7.setImgsrc(String.valueOf(R.drawable.home_list5));
                    tmpIteminfo7.setLinkurl("081D3059-A8FE-45B3-A2C4-FBD8E683F9CC");
                    data.add(tmpIteminfo7);
                    ItemInfo tmpIteminfo8=new ItemInfo();
                    tmpIteminfo8.setTitle("重点工作");
                    tmpIteminfo8.setInfotype("CCC");
                    tmpIteminfo8.setBak1("");
                    tmpIteminfo8.setBak2("");
                    tmpIteminfo8.setBak3("");
                    tmpIteminfo8.setSource("zdgz");
                    tmpIteminfo8.setImgsrc(String.valueOf(R.drawable.home_list6));
                    tmpIteminfo8.setLinkurl("B613057F-01F5-40FF-8FA1-45F42923BCCB");
                    data.add(tmpIteminfo8);
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

}
