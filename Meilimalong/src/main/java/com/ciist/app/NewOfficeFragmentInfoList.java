package com.ciist.app;


import android.content.ContentValues;
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


/**
 * Created by 中联软科 on 2015/12/15
 */
public class NewOfficeFragmentInfoList extends android.support.v4.app.Fragment {


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
    private ItemInfoAdapter adapter;
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
    private String mSubjectCode;
    private String mSubjectName;
    private boolean isFullScreen = false;

    public NewOfficeFragmentInfoList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        mContainer = container;
        mInflater = inflater;
        mAction = "jb";
        mView = inflater.inflate(R.layout.new_office_fragment_infolist, container, false);
        initInputParams();
        initCtrls();
        registerListening();
        LoadDatas();
        return mView;
    }

    void initInputParams() {
        try {
            Bundle data = getArguments();//获得从activity中传递过来的值
            mIdentify = data.getString("identify");
            mUsername = data.getString("username");
            mSelfids = data.getString("selfids");
            mTitle = data.getString("title", "办公系统");
            mAction = data.getString("action", "jb");
            mSubjectCode = data.getString("subjectcode");
            mSubjectName = data.getString("subjectname");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initCtrls() {
        try {
            mlistView = (ListView) mView.findViewById(R.id.newofficeinfolist);
            waiting = (ProgressBar) mView.findViewById(R.id.waitloading);
            footer = mInflater.inflate(R.layout.listview_footer, mContainer, false);
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

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            tmpIntent.putExtra("latidue_b", 0);
                            tmpIntent.putExtra("longtidue_b", 0);
                            startActivity(tmpIntent);
                        } else {
                            if (linkType != null && linkType == "ciist1") {

                            } else {
                                Intent tmpIntent = new Intent(context, IndexOfOACoverStyleActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("subjectname", tmpobj.getTitle());
                                bundle.putString("subjectcode", tmpobj.getBak5());

                                bundle.putString("identify",mIdentify);
                                bundle.putString("username",mUsername);
                                bundle.putString("selfids",mSelfids);
                                tmpIntent.putExtras(bundle);
                                startActivity(tmpIntent);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
//                    System.out.println(e.getMessage().toString());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void LoadDatas() {
        try {
            if (waiting != null) {
                waiting.setVisibility(View.VISIBLE);
            }
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
                        int resID = R.layout.listview_item;
                        adapter = new ItemInfoAdapter(context, resID, data);
                        try {
                            mlistView.setAdapter(adapter);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
                    Toast.makeText(context, "亲,目前还没有数据!", Toast.LENGTH_SHORT).show();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };

    void RefreashData(String _condtion) {
        try {
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
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/10";

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


}
