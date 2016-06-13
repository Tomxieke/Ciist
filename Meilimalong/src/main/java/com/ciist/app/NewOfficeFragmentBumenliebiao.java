package com.ciist.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * 部门列表相关
 */
public class NewOfficeFragmentBumenliebiao extends Fragment implements AdapterView.OnItemClickListener, XListView.IXListViewListener {
    private boolean hasTime = false;
    private XListView mBumes_lsit;
    private ListView mListView;
    private ItemListAdapter mAdapter_Type1;  //右边
    private BumenAdapter mAdapter_Type2;  //左边
    private TextView nameText; //部门名称
    private String mUrl;  //网络连接
    private String mIdentify="";
    private String mUserName="";
    private String mSelfids="";
    private String mDepCode;
    private int pageNo = 1;
    private int pageSize = 15;
    private int tatolPage = 4;
    private ProgressBar wait_pb; // 等待加载
    private ProgressBar main_pb;
    private RelativeLayout layout;  //右边列表
    private GISLocation mCurrentLocation = new GISLocation();


    public NewOfficeFragmentBumenliebiao() {
        // Required empty public constructor
    }

    public static NewOfficeFragmentBumenliebiao newInstance(String param,String identify,String depCode,String _username,String _selfids) {

        Bundle args = new Bundle();
        args.putString("PARAM", param);
        args.putString("identify", identify);
        args.putString("depCode", depCode);
        args.putString("username",_username);
        args.putString("selfids",_selfids);
        NewOfficeFragmentBumenliebiao fragment = new NewOfficeFragmentBumenliebiao();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString("PARAM");
            mIdentify = getArguments().getString("identify");
            mUserName = getArguments().getString("username");
            mSelfids = getArguments().getString("selfids");
            mDepCode = getArguments().getString("depCode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_office_fragment_bumenliebiao, container, false);
        layout = (RelativeLayout) v.findViewById(R.id.bumen_relativelayout);
        wait_pb = (ProgressBar) v.findViewById(R.id.waitint_progress);
        main_pb = (ProgressBar) v.findViewById(R.id.bumen_mainProgressBar);
        nameText = (TextView) v.findViewById(R.id.bumen_nameTxt);
        nameText.getBackground().setAlpha(150);
        mListView = (ListView) v.findViewById(R.id.benmen_list);
        //    Log.d("test", "---mListView---" + mListView.getId());
        mAdapter_Type1 = new ItemListAdapter(inflater);
        mListView.setAdapter(mAdapter_Type1);
        mListView.setOnItemClickListener(this);
        mBumes_lsit = (XListView) v.findViewById(R.id.bumens_list);
        mBumes_lsit.getBackground().setAlpha(180);
        mAdapter_Type2 = new BumenAdapter(inflater);
        mBumes_lsit.setAdapter(mAdapter_Type2);
        mBumes_lsit.setPullRefreshEnable(false);
        mBumes_lsit.setOnItemClickListener(this);
        mBumes_lsit.setXListViewListener(this);
        getDataFromInternet();
        return v;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.benmen_list:
                ItemInfo iteminfo = (ItemInfo) parent.getAdapter().getItem(position);
                String linkType = iteminfo.getBak4();
                String subjectLink = iteminfo.getBak5();

                if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                    Intent tmpIntent = new Intent(getActivity(), WebActivity.class);
                    tmpIntent.putExtra("URL", iteminfo.getLinkurl());
                    tmpIntent.putExtra("TITLE", iteminfo.getTitle());
                    tmpIntent.putExtra("PUBDATE", iteminfo.getPubdate());
                    tmpIntent.putExtra("source", iteminfo.getInfosource());
                    tmpIntent.putExtra("author", iteminfo.getInfoauthor());
                    tmpIntent.putExtra("infotype", iteminfo.getInfotype());

                    tmpIntent.putExtra("IsDuban",true);
                    tmpIntent.putExtra("depcode",iteminfo.getDepcode());
                    tmpIntent.putExtra("identify",mIdentify);
                    tmpIntent.putExtra("username",mUserName);
                    tmpIntent.putExtra("selfids",mSelfids);
                    tmpIntent.putExtra("hasTime",hasTime);
                    tmpIntent.putExtra("ROOT", "#Content");
                    tmpIntent.putExtra("blank", true);
                    tmpIntent.putExtra("telnum", iteminfo.getTelnum());
                    tmpIntent.putExtra("latidue_e", iteminfo.getLatidue());
                    tmpIntent.putExtra("longtidue_e", iteminfo.getLongtidue());
                    tmpIntent.putExtra("latidue_b", mCurrentLocation.getLatitude());
                    tmpIntent.putExtra("longtidue_b", mCurrentLocation.getLongitude());

                    startActivity(tmpIntent);
                } else {
                    if (linkType != null && linkType == "ciist1") {


                    } else {
                        Intent tmpIntent = new Intent(getActivity(), LeaderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("colorgird",getResources().getColor(R.color.tradinghall));
                        bundle.putString("subjectname2", iteminfo.getTitle());


                        bundle.putString("subjectname", iteminfo.getTitle());
                        bundle.putString("subjectcode", iteminfo.getBak5());

                        bundle.putBoolean("IsDuban",true);
                        bundle.putString("depcode", iteminfo.getDepcode());
                        bundle.putString("identify", mIdentify);
                        bundle.putString("username", mUserName);
                        bundle.putString("selfids", mSelfids);
                        bundle.putBoolean("hasTime",hasTime);

                        tmpIntent.putExtras(bundle);
                        startActivity(tmpIntent);
                    }
                }
                break;
            case R.id.bumens_list:
                ItemInfo info = (ItemInfo) parent.getAdapter().getItem(position);
                layout.setVisibility(View.VISIBLE);
                nameText.setText(info.getTitle());
                if (waiting_falg) {
                    wait_pb.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
                getItemData(info);
        }

    }

    private boolean waiting_falg = false;

    /**
     * 连接网络获取数据  部门列表数据
     */
    void getDataFromInternet() {
        String _url = ServerInfo.ServerBKRoot+"/info/getDepList/ciistkey/" + mIdentify+"/" + pageNo + "/" + pageSize;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(getActivity(), "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                //   Log.d("test", s);
                ArrayList<ItemInfo> listData = new ArrayList<ItemInfo>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
                    mBumes_lsit.setPullLoadEnable(true);  //加载更多开启
                    //     mBumes_lsit.stopRefresh();
                    mBumes_lsit.stopLoadMore();
                    if (jsonArray.length() < pageSize) {   //已经是最有一页
                        Log.e("test", "-----------这是最后一页了。-----");
                        mBumes_lsit.setPullLoadEnable(false);  //加载更多开启
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        itemInfo = new ItemInfo();
                        JSONObject object = jsonArray.getJSONObject(j);
                        itemInfo.setBak5(object.getString("Selfids"));
                        itemInfo.setTitle(object.getString("Dep_zh"));
                        listData.add(itemInfo);
                        itemInfo = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (1 == pageNo) {
                    mAdapter_Type2.refreshData(listData);
                    ItemInfo info = listData.get(0);
                    getItemData(info);
                    nameText.setText(info.getTitle());
                    wait_pb.setVisibility(View.GONE);
                    main_pb.setVisibility(View.GONE);
                    nameText.setVisibility(View.VISIBLE);
                    mBumes_lsit.setVisibility(View.VISIBLE);
                } else {
                    mAdapter_Type2.addData(listData);
                }

                waiting_falg = true;

            }
        });

    }

    /**
     * 获取每个部门的子项目
     */
    void getItemData(ItemInfo itemInfo) {
        //    ArrayList<ItemInfo> itemList = new ArrayList<>();
        String _url = ServerInfo.ServerBKRoot+"/info/getDepWorkInfoList/ciistkey/" + mIdentify+"/"  + "9C4330D3-2851-4D82-9EC4-5CE15D0DB67A" + "/" +itemInfo.getBak5() +"/"+ 1 + "/" + pageSize;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(_url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(getActivity(), "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                ArrayList<ItemInfo> itemList = new ArrayList<>();
                try {
                    ItemInfo itemInfo = null;
                    JSONArray jsonArray = new JSONArray(s);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        itemInfo = new ItemInfo();
                        JSONObject object = jsonArray.getJSONObject(j);
                        String link = ServerInfo.InfoDetailPath;
                        itemInfo.setTitle(object.getString("Title"));
                        itemInfo.setPubdate(object.getString("pubDate"));
                        itemInfo.setLinkurl(link + object.getString("Info_Ids"));
                        itemInfo.setImgsrc(link + object.getString("Image"));
                        itemInfo.setInfosource(object.getString("Sourse"));
                        itemInfo.setInfoauthor(object.getString("Author"));
                        itemInfo.setInfotype(object.getString("Type"));
                        itemInfo.setBak1(object.getString("Remark1"));
                        itemInfo.setBak2(object.getString("Remark2"));
                        itemInfo.setBak3(object.getString("Remark3"));
                        itemInfo.setBak4(object.getString("Remark4"));
                        itemInfo.setBak5(object.getString("Remark5"));
                        itemInfo.setSource(object.getString("Sourse"));
                        itemInfo.setAuthor(object.getString("Author"));
                        itemInfo.setTelnum(object.getString("contracttel"));
                        itemInfo.setLongtidue(object.getDouble("longtidue"));
                        itemInfo.setLatidue(object.getDouble("latidue"));
                        itemInfo.setVisitCount(object.getInt("VisitCount"));

                        itemInfo.setDepcode(object.getString("depcode"));
                        itemInfo.setIspub(object.getInt("ispub"));
                        itemList.add(itemInfo);
                    }

                   /* for (ItemInfo o : itemList) {
                        Log.d("test", o.getTitle());
                    }*/
                    mAdapter_Type1.setData(itemList);
                    mListView.setVisibility(View.VISIBLE);
                    wait_pb.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    //刷新
    public void onRefresh() {
        pageNo = 1;
        getDataFromInternet();
    }

    @Override
    //加载更多
    public void onLoadMore() {
        ++pageNo;
        getDataFromInternet();
    }

    /**
     * 子列表适配器
     */
    private class ItemListAdapter extends BaseAdapter {
        ArrayList<ItemInfo> listData = new ArrayList<>();
        LayoutInflater inflater;

        public ItemListAdapter(LayoutInflater mInflater) {
            this.inflater = mInflater;
        }

        public void setData(ArrayList<ItemInfo> list) {
            this.listData = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView txt = (TextView) convertView;
            ItemInfo info = (ItemInfo) getItem(position);
            txt.setText(info.getTitle());
            return convertView;
        }
    }

    /**
     * 适配器
     */
    public class BumenAdapter extends BaseAdapter {
        ArrayList<ItemInfo> listData = new ArrayList<>();
        LayoutInflater inflater;

        public BumenAdapter(LayoutInflater mInflater) {
            this.inflater = mInflater;

        }

        public void refreshData(ArrayList<ItemInfo> data) {
            this.listData = data;
            notifyDataSetChanged();
        }

        public void addData(ArrayList<ItemInfo> data) {
            this.listData.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            Hider hider = null;
            if (convertView == null) {
                hider = new Hider();
                view = inflater.inflate(R.layout.item2_bumen_layout, null);
                hider.itemTxt = (TextView) view.findViewById(R.id.bumen_item2name_txt);
                view.setTag(hider);
            } else {
                view = convertView;
                hider = (Hider) view.getTag();
            }

            ItemInfo info = (ItemInfo) getItem(position);
            hider.itemTxt.setText(info.getTitle());
            hider.itemTxt.setTag(info);


            return view;
        }

        class Hider {
            TextView itemTxt;
        }
    }

}
