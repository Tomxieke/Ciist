package com.ciist.app;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CIIST on 2015/11/8 0008.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ChannelOfNewsInfoStyleListFragment extends ListFragment {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private int mNum;
    private String mSubjectCode = "";
    public ArrayList<String> list = new ArrayList<String>();

    private int headerHeight = 0;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    //    private ProgressDialog loading;
    private ProgressBar waiting;
    private List<ItemInfo> data;
    private ItemInfoAdapter adapter;
    public PageInfo currentPageInfo = new PageInfo();
    public int currentHavePageIndex = 0;
    private View footer;
    private Context context;
    private InternetOfTabsSubject0Thread subjectThread;


    /**
     * 创建一个计算Fragment页面的实例，将怒num作为一个参数。
     * （Create a new instance of
     * CountingFragment, providing "num" as an argument.）
     */
    public static ChannelOfNewsInfoStyleListFragment newInstance(int num, String subjectcode) {

        ChannelOfNewsInfoStyleListFragment f = new ChannelOfNewsInfoStyleListFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("subjectsode", subjectcode);
        f.setArguments(args);
        return f;
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_SUCCESS:
                        if (getListView().getFooterViewsCount() > 0) {
                            getListView().removeFooterView(footer);
                        }
                        if (data == null)
                            data = new ArrayList<ItemInfo>();
                        if (currentPageInfo == null) {
                            return;
                        } else {
                            if (getListView().getFooterViewsCount() <= 0) {
                                getListView().addFooterView(footer);
                            }
                        }
                        ResultInfo result = (ResultInfo) msg.obj;
                        if (result == null || result.getSimpleItemObj() == null || result.getSimpleItemObj().size() <= 0) {
                            return;
                        }
                        currentPageInfo = result.getPageinfo();
                        data.addAll(result.getSimpleItemObj());
                        if (adapter == null) {
                            int resID = R.layout.listview_item;
                            adapter = new ItemInfoAdapter(context, resID, data);
                            setListAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        currentHavePageIndex++;
                        if (result.getSimpleItemObj().size() < 10) {
                            if (getListView().getFooterViewsCount() > 0) {
                                getListView().removeFooterView(footer);
                            }
                        }
//                        loading.dismiss();
                        if (waiting != null) {
                            waiting.setVisibility(View.GONE);
                        }
                        break;
                    case MSG_FAILURE:
//                        loading.dismiss();
                        if (waiting != null) {
                            waiting.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(), "亲,网络不给力呀!", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NODATA:
                        getListView().removeFooterView(footer);
//                        loading.dismiss();
                        if (waiting != null) {
                            waiting.setVisibility(View.GONE);
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    };

    void LoadDatas() {
        try {
            subjectThread = new InternetOfTabsSubject0Thread();
            subjectThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private class InternetOfTabsSubject0Thread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.GetInfoPre + mSubjectCode + "/" + currentPageInfo.getPageIndex() + "/10";
//                HttpPost _post = new HttpPost(ServerInfo.requestDataUrlRoot);
//                _post.setHeader("Accept", APPLICATION_JSON);
//                _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
//                JSONStringer _jsonstrig;
//                try {
//                    _jsonstrig = new JSONStringer()
//                            .object()
//                            .key("_header")
//                            .object()
//                            .key("SKEY").value("CIISTKEY")
//                            .key("PageIndex").value(currentPageInfo.getPageIndex())
//                            .key("PageSize").value(currentPageInfo.getPageSize())
//                            .key("Identify").value("54fffff444")
//                            .endObject()
//                            .key("MenuIDS").value(mSubjectCode)
//                            .endObject();
//
//                    StringEntity entity = new StringEntity(_jsonstrig.toString(), HTTP.UTF_8);//需要设置成utf-8否则汉字乱码
//                    entity.setContentType(CONTENT_TYPE_TEXT_JSON);
//                    entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
//                    _post.setEntity(entity);
//                    // 向WCF服务发送请求
//
//                    HttpClient httpClient = new DefaultHttpClient();
//                    HttpResponse response = httpClient.execute(_post);
//                    // 判断是否成功
//                    if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
//                        jsonString = EntityUtils.toString(response.getEntity(), "UTF-8");
//                    }
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (NullPointerException ex) {
//                    ex.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                //json
//                String jsonString = "";
//                URL url = new URL(_url);
//                ByteArrayOutputStream os = new ByteArrayOutputStream();
//                byte[] datacache = new byte[1024];
//                int len = 0;
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                InputStream is = conn.getInputStream();
//                while ((len = is.read(datacache)) != -1) {
//                    os.write(datacache, 0, len);
//                }
//                is.close();
//                jsonString = new String(os.toByteArray());
//                conn.disconnect();

//                System.out.println("************************* 1 *******************");
//
//                CloseableHttpClient httpclient = HttpClients.createDefault();
//                System.out.println("************************* 2 *******************");
//
//                try {
//                    HttpGet httpGet = new HttpGet(_url);
//                    CloseableHttpResponse response1 = httpclient.execute(httpGet);
//                    try {
////                        System.out.println(response1.getStatusLine());
//                        if (response1.getStatusLine().getStatusCode() != 200) {
//                            mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
//                            response1.close();
//                            return;
//                        }
//                        HttpEntity entity1 = response1.getEntity();
//                        jsonString = EntityUtils.toString(entity1);
//                        EntityUtils.consume(entity1);
//                    } finally {
//                        response1.close();
//                    }
//                } finally {
//                    httpclient.close();
//                }


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
                    int _visitecounter = jsonObj.getInt("VisitCount");
                    tmpIteminfo.setVisitCount(_visitecounter);
                    data.add(tmpIteminfo);
                }
                //json
                currentPageInfo.setPageCount(0);
                currentPageInfo.setTotalCount(0);
                result.setPageinfo(currentPageInfo);
                result.setSimpleItemObj(data);
                result.setResultCode("102");
                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mSubjectCode = getArguments() != null ? getArguments().getString("subjectsode") : "CIIST";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.channel_of_newsinfo_style__listfragment, container, false);
        waiting = (ProgressBar) view.findViewById(R.id.waitloading);
//        try {
//            if (loading == null)
//                loading = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
//            loading.show();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniview();
        LoadDatas();
//        setListAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, getData()));
    }


    void iniview() {
        try {
            footer = getActivity().getLayoutInflater().inflate(R.layout.listview_footer, null);
            if (footer == null) {
                return;
            }
            getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    try {
                        int footerCount = getListView().getFooterViewsCount();
                        if (footerCount <= 0) {
                            return;
                        }
                        int lastItemIndexID = firstVisibleItem + visibleItemCount;
                        if (lastItemIndexID <= 0) return;

                        if (lastItemIndexID == totalItemCount) {
                            View lastItemView = getListView().getChildAt(getListView().getChildCount() - 1);
                            headerHeight = 0;// mHorizontalScrollView.getHeight() + temp_line_Layout.getHeight();
                            int bottomChazhi = Math.abs(getListView().getBottom() - lastItemView.getBottom());
                            if (lastItemView != null && bottomChazhi == headerHeight && footer != null) {
                                currentPageInfo.setPageIndex(currentHavePageIndex + 1);
                                LoadDatas();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
//            Intent tmpIntent = new Intent(context, WebActivity.class);
            if (id == -1) {
                LoadDatas();
                return;
            }
            View _view = v.findViewById(R.id.simpleTextView1);
            TextView tmp = (TextView) _view;
            ItemInfo tmpobj = (ItemInfo) tmp.getTag();
//            tmpIntent.putExtra("URL", tmpobj.getLinkurl());
//            tmpIntent.putExtra("TITLE", tmpobj.getTitle());
//            tmpIntent.putExtra("PUBDATE", tmpobj.getPubdate());
//            tmpIntent.putExtra("source", tmpobj.getInfosource());
//            tmpIntent.putExtra("infotype", tmpobj.getInfotype());
//            tmpIntent.putExtra("author", tmpobj.getInfoauthor());
//            tmpIntent.putExtra("ROOT", "#Content");
//            startActivity(tmpIntent);
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
                tmpIntent.putExtra("blank", false);
                startActivity(tmpIntent);
            } else {
                if (linkType != null && linkType == "ciist1") {
//                    Intent tmpIntent = new Intent(context, ChannelOfCoverStyleActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("subjectname", tmpobj.getTitle());
//                    bundle.putString("subjectcode", tmpobj.getBak5());
//                    tmpIntent.putExtras(bundle);
//                    startActivity(tmpIntent);
                } else {
                    Intent tmpIntent = new Intent(context, ChannelOfCoverStyleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subjectname", tmpobj.getTitle());
                    bundle.putString("subjectcode", tmpobj.getBak5());
                    tmpIntent.putExtras(bundle);
                    startActivity(tmpIntent);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
