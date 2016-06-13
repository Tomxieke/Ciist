package com.ciist.app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.entities.ChatMsgEntity;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.ChatMsgViewAdapter;
import com.ciist.toolkits.ItemInfo_oaAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * @author way
 */
public class chatting_item_msg_index_Activity extends Activity implements OnClickListener {

    private int mCurrentBtn = 0;
    private String mTaskState = "0";
    private  String mContentSend="";
    private Button mBtnSend;// 发送btn
    private Button mBtnBack;// 返回btn
    private Button mBtnEndTask;
    private EditText mEditTextContent;
    private ListView mListView;
    private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
    private List<ChatMsgEntity> data;// 消息对象数组
    private TextView mChatTitle;
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_item_msg_index);
        context = getApplicationContext();
        initInputParams();
        initView();// 初始化view
        if (waiting != null) {
            waiting.setVisibility(View.VISIBLE);
        }
        registerListening();
        initData();// 初始化数据


    }

    /**
     * 初始化view
     */
    public void initView() {
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnEndTask = (Button) findViewById(R.id.btn_endtask);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);


        if (mActionType.toUpperCase().equals("zb".toUpperCase())) {
            mBtnSend.setText("汇报");
            mBtnEndTask.setVisibility(View.GONE);
            if (mTaskState.equals("0")) {
                mBtnSend.setVisibility(View.VISIBLE);
                mEditTextContent.setVisibility(View.VISIBLE);
            } else {
                mBtnSend.setVisibility(View.GONE);
                mEditTextContent.setVisibility(View.GONE);
            }
        }
        if (mActionType.toUpperCase().equals("jb".toUpperCase())) {
            mBtnSend.setText("催办");
            if (mTaskState.equals("0")) {
                mBtnSend.setVisibility(View.VISIBLE);
                mBtnEndTask.setVisibility(View.VISIBLE);
                mEditTextContent.setVisibility(View.VISIBLE);
            } else {
                mBtnSend.setVisibility(View.GONE);
                mBtnEndTask.setVisibility(View.GONE);
                mEditTextContent.setVisibility(View.GONE);
            }
        }
        mBtnEndTask.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);

        waiting = (ProgressBar) findViewById(R.id.waitloading);
        footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
        mChatTitle = (TextView) findViewById(R.id.chatTitle);
        mChatTitle.setText(mTitle);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    if (mListView.getFooterViewsCount() <= 0) {
                        return;
                    }
                    int lastItemIndexID = firstVisibleItem + visibleItemCount;
                    if (lastItemIndexID <= 0) return;
                    if (lastItemIndexID == totalItemCount) {
                        View lastItemView = mListView.getChildAt(mListView.getChildCount() - 1);
                        headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                        int bottomChazhi = Math.abs(mListView.getBottom() - lastItemView.getBottom());
                        if (lastItemView != null && bottomChazhi == headerHeight && footer != null) {
                            currentPageInfo.setPageIndex(currentHavePageIndex + 1);
                            initData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void initInputParams() {
        try {
            Intent intent = this.getIntent();
            mIdentify = intent.getStringExtra("identify");
            mUsername = intent.getStringExtra("username");
            mSelfids = intent.getStringExtra("selfids");
            mTitle = intent.getStringExtra("title");
            mAction = intent.getStringExtra("action");
            mActionType = intent.getStringExtra("actiontype");
            mTaskids = intent.getStringExtra("taskids");
            mTaskState = intent.getStringExtra("tkstate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void registerListening() {
        try {

            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    try {
                        if (mListView.getFooterViewsCount() <= 0) {
                            return;
                        }
                        int lastItemIndexID = firstVisibleItem + visibleItemCount;
                        if (lastItemIndexID <= 0) return;
                        if (lastItemIndexID == totalItemCount) {
                            View lastItemView = mListView.getChildAt(mListView.getChildCount() - 1);
                            headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                            int bottomChazhi = Math.abs(mListView.getBottom() - lastItemView.getBottom());
                            if (lastItemView != null && bottomChazhi == headerHeight && footer != null) {
                                currentPageInfo.setPageIndex(currentHavePageIndex + 1);
                                initData();
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

    public void initData() {
        try {
            subjectThread = new InternetOfSubject0Thread();
            subjectThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:// 发送按钮点击事件
                mCurrentBtn = 0;
                send();

                break;
            case R.id.btn_back:// 返回按钮点击事件
                finish();// 结束,实际开发中，可以返回主界面
                break;
            case R.id.btn_endtask:
                mCurrentBtn = 1;
                send();
                break;
        }
    }

    /**
     * 发送消息
     */
    private void send() {
        String contString = mEditTextContent.getText().toString();
        mContentSend=contString;
        if (contString.length() > 0) {
            beginCreate();
//            mDataArrays.add(entity);
//            mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
            mEditTextContent.setText("");// 清空编辑框数据
//            mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
        }
    }

    void beginCreate() {
        try {
            waiting.setVisibility(View.VISIBLE);
            new InternetOfBeginCreateThread().start();
        } catch (Exception e) {
        }
    }

//    ChatMsgEntity entity = new ChatMsgEntity();
//    entity.setName("ciist");
//    entity.setDate(getDate());
//    entity.setMessage(contString);
//    entity.setMsgType(false);
//


    private class InternetOfBeginCreateThread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                String _url = ServerInfo.ServerBKRoot + "/info/runTask";
                //post begin
                HttpPost _post = new HttpPost(_url);
                _post.setHeader("Accept", APPLICATION_JSON);
                _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
                JSONStringer _jsonstrig;
                try {
                    _jsonstrig = new JSONStringer();
                    JSONObject _obj1 = new JSONObject();
                    _obj1.put("Selfids", "CIISTKEY");
                    _obj1.put("Timestamp", "1900-01-01");
                    _obj1.put("Byuids", mSelfids);
                    _obj1.put("Visible", 1);
                    _obj1.put("Guids", "ciistkey");

                    _obj1.put("Taskids", mTaskids);
                    _obj1.put("Fromids", mSelfids);
                    _obj1.put("Fromids_zh", mUsername);
                    _obj1.put("FromDate", "1900-01-01");
                    _obj1.put("Msginfo", mContentSend);
                    _obj1.put("Action", mActionType);
                    _obj1.put("Params", "");

                    JSONObject _header = new JSONObject();
                    _header.put("SKEY", "ciistkey");
                    _header.put("PageIndex", currentPageInfo.getPageIndex());
                    _header.put("PageSize", currentPageInfo.getPageSize());
                    _header.put("Identify", mIdentify);
                    _header.put("SelfIDS", mSelfids);
                    JSONObject sendObj = new JSONObject();

                    sendObj.put("_header", _header);
                    sendObj.put("taskRunning", _obj1);
                    String _atype = "";
                    if (mActionType.toUpperCase().equals("zb".toUpperCase())) {
                        _atype = "zb";
                    }
                    if (mActionType.toUpperCase().equals("jb".toUpperCase())) {
                        if (mCurrentBtn == 0) {
                            _atype = "cb";
                        }
                        if (mCurrentBtn == 1) {
                            _atype = "end";
                        }
                    }

                    sendObj.put("actionType", _atype);


                    StringEntity entity = new StringEntity(sendObj.toString(), HTTP.UTF_8);//需要设置成utf-8否则汉字乱码
                    entity.setContentType(CONTENT_TYPE_TEXT_JSON);
                    entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
                    _post.setEntity(entity);
                    // 向WCF服务发送请求

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(_post);
                    // 判断是否成功
                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                        jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //post end

                int a = jsonString.length();
                if (a == 3 && jsonString.contains("1")) {
                    currentHavePageIndex = 0;
                    currentPageInfo = new PageInfo();
                    if (data != null && data.size() > 0)
                        data.clear();
                    initData();
                    //mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
                    return;
                } else {
                    mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }


    /**
     * 发送消息时，获取当前事件
     *
     * @return 当前时间
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

//my

    private String mTitle;
    private String mActionType;
    private Context context;
    private String mIdentify = "";
    private String mUsername = "";
    private String mSelfids = "";
    private String mTaskids = "";
    private String mAction;
    private ProgressBar waiting;
    private View footer;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_REFREASH = 888;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private int headerHeight = 0;
    private InternetOfSubject0Thread subjectThread;

    private class InternetOfSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            String _url = "";
            try {
                List<ChatMsgEntity> data = new ArrayList<ChatMsgEntity>();
                _url = ServerInfo.ServerBKRoot + "/info/getTaskRunning/ciistkey/" + mActionType + "/" + mIdentify + "/" + mTaskids + "/" + currentPageInfo.getPageIndex() + "/10";
//
//                if (mAction.toUpperCase().equals("zb".toUpperCase())) {
//                    _url = ServerInfo.ServerBKRoot + "/info/getTaskToMe/ciistkey/" + mIdentify + "/" + currentPageInfo.getPageIndex() + "/10";
//                } else if (mAction.toUpperCase().equals("jb".toUpperCase())) {
//                    _url = ServerInfo.ServerBKRoot + "/info/getTaskFromMe/ciistkey/" + mIdentify + "/" + currentPageInfo.getPageIndex() + "/10";
//                }

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
                    ChatMsgEntity tmpIteminfo = new ChatMsgEntity();
                    tmpIteminfo.setDate(jsonObj.getString("FromDate"));
                    if (jsonObj.getString("Fromids").equals(mSelfids)) {
                        tmpIteminfo.setName(jsonObj.getString("Fromids_zh"));
                        tmpIteminfo.setMsgType(false);//自己发送的消息
                    } else {
                        tmpIteminfo.setName(jsonObj.getString("Fromids_zh"));
                        tmpIteminfo.setMsgType(true);//  收到的消息
                    }
                    tmpIteminfo.setMessage(jsonObj.getString("Msginfo"));
                    data.add(tmpIteminfo);
                }

                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setChatMsgEntityDatas(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
//                    if (mListView.getFooterViewsCount() > 0) mListView.removeFooterView(footer);
                    if (data == null)
                        data = new ArrayList<ChatMsgEntity>();
                    if (currentPageInfo == null) {
                        return;
                    } else {
//                        if (mListView.getFooterViewsCount() <= 0) mListView.addFooterView(footer);
                    }
                    ResultInfo result = (ResultInfo) msg.obj;
                    if (result == null || result.getChatMsgEntityDatas() == null || result.getChatMsgEntityDatas().size() <= 0) {
                        return;
                    }
                    if (result.getChatMsgEntityDatas().size() < 10) {
                        if (mListView.getFooterViewsCount() > 0) mListView.removeFooterView(footer);
                    } else {
                        if (mListView.getFooterViewsCount() <= 0) mListView.addFooterView(footer);
                    }
                    currentPageInfo = result.getPageinfo();
                    data.addAll(result.getChatMsgEntityDatas());
                    if (mAdapter == null) {
                        mAdapter = new ChatMsgViewAdapter(context, data);
                        mListView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                    currentHavePageIndex++;
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
//                    mListView.setSelection(mAdapter.getCount() - 1);
                    break;
                case MSG_FAILURE:
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    Toast.makeText(context, "亲,网络不给力呀!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NODATA:
                    if (mListView.getFooterViewsCount() > 0) mListView.removeFooterView(footer);
                    Toast.makeText(context, "亲,没有数据了哦!", Toast.LENGTH_SHORT).show();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };
}