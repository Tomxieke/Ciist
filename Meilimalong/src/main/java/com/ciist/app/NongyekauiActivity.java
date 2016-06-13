package com.ciist.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CiistAdapter2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.scxh.slider.library.SliderLayout;
import com.scxh.slider.library.SliderTypes.BaseSliderView;
import com.scxh.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NongyekauiActivity extends Fragment implements XListView.IXListViewListener,AdapterView.OnItemClickListener  {

    private Context mcontext;

    private static final String GUNDONG = "6EA8EB3F-BF9E-4CB7-8F8B-832378A77B73";
    private static final String LISTCODE = "265593FE-3F07-4165-A44A-CA8C788DC6B9";

    private static final String TAG_PARMA = "param";
    private String mParam;

    private GISLocation mCurrentLocation = new GISLocation();
    private boolean mIsDuban = false;
    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";

    private int index = 1;//页数
    private int num = 10; //条目

    private View mHeadView;//listView头部
    private XListView xListView;
    private ProgressBar slider_pb, information_pb;
    private ImageView scrollPosition;

    private CiistAdapter2 adapter;
//    private CiistTitleView information_bank;//标题

    private ArrayList<ItemInfo> listDate_l = new ArrayList<>();//列表集合
    private ArrayList<ItemInfo> listDate_g = new ArrayList<>();//滚动集合

    public static NongyekauiActivity newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TAG_PARMA, title);
        NongyekauiActivity docfragment = new NongyekauiActivity();
        docfragment.setArguments(args);
        return docfragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(TAG_PARMA);
        }
        mcontext = getActivity();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nongyekaui, container, false);
        //绑定id
        xListView = (XListView) view.findViewById(R.id.nongye_lv);
        scrollPosition = (ImageView) view.findViewById(R.id.nongye_position);
        information_pb = (ProgressBar) view.findViewById(R.id.nongye_pb);
//        information_bank = (CiistTitleView) view.findViewById(R.id.information_titleview);
        mHeadView = LayoutInflater.from(mcontext).inflate(R.layout.slider_img_layout, null);
        slider_pb = (ProgressBar) mHeadView.findViewById(R.id.slider_pb);
        xListView.addHeaderView(mHeadView);
        //设置监听
        xListView.setOnItemClickListener(this);
        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(false);
        xListView.setPullRefreshEnable(false);
        getGunDongData();//滚动
        getListData();//列表
        TotalListener();//监听

        return  view;
    }


    /**
     * 所有控件的点击监听
     */
    private void TotalListener() {
        //确定位置
        scrollPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xListView.smoothScrollToPosition(0);
            }
        });
        //滑动监听
        xListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (xListView.getLastVisiblePosition() >= visibleItemCount * 3) {
                    scrollPosition.setVisibility(View.VISIBLE);
                }
                if (xListView.getLastVisiblePosition() <= visibleItemCount)
                    scrollPosition.setVisibility(View.INVISIBLE);
            }
        });
//        //标题返回
//        information_bank.setOnLiftClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//            }
//        });
//        //标题收缩跳转
//        information_bank.setOnRightClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mcontext,SearchActivity.class));
//            }
//        });
    }

    /**
     * 获取滚动数据
     */
    private void getGunDongData() {
        String urlPath = ServerInfo.GetInfoPre + GUNDONG + "/1/6";  //获取地址
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlPath, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mcontext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        return;
                    }
                    onLoad();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(j);
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
                        listDate_g.add(tmpIteminfo);
                    }
                    slider_pb.setVisibility(View.INVISIBLE);
                    inflterGunDongDataAndListener(listDate_g);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 获取列表数据
     */
    private void getListData() {
        String urlPath = ServerInfo.GetInfoPre + LISTCODE + "/" + index + "/" + num;  //获取地址
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlPath, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(mcontext, "亲，您网络状态不好哟", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    onLoad();
                    if (jsonArray == null || jsonArray.length() <= 0) {
                        return;
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(j);
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
                        listDate_l.add(tmpIteminfo);
                    }
                    if(adapter == null){
                        adapter = new CiistAdapter2(mcontext,listDate_l);
                        xListView.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    if (jsonArray.length() < num){
                        xListView.setPullLoadEnable(false);
                    }
                    information_pb.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 填充滚动数据并监听
     */
    private void inflterGunDongDataAndListener(List<ItemInfo> list) {
        SliderLayout mSliderLayout = (SliderLayout) mHeadView.findViewById(R.id.slider_img);
        int length = list.size();
        mSliderLayout.removeAllSliders(); //移除原有数据
        for (int i = 0; i < length; i++) {
            final ItemInfo info = list.get(i);
            TextSliderView sliderView = new TextSliderView(mcontext);   //向SliderLayout中添加控件
            sliderView.image(list.get(i).getImgsrc());
            sliderView.description(list.get(i).getTitle());
            sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent tmpIntent = new Intent(mcontext, WebActivity.class);
                    tmpIntent.putExtra("URL", info.getLinkurl());
                    tmpIntent.putExtra("TITLE", info.getTitle());
                    tmpIntent.putExtra("PUBDATE", info.getPubdate());
                    tmpIntent.putExtra("source", info.getInfosource());
                    tmpIntent.putExtra("author", info.getInfoauthor());
                    tmpIntent.putExtra("infotype", info.getInfotype());
                    tmpIntent.putExtra("ROOT", "#Content");
                    tmpIntent.putExtra("blank", true);
                    tmpIntent.putExtra("img_url",info.getImgsrc());
                    tmpIntent.putExtra("telnum", info.getTelnum());
                    tmpIntent.putExtra("latidue_e", info.getLatidue());
                    tmpIntent.putExtra("longtidue_e", info.getLongtidue());
                    startActivity(tmpIntent);
                }
            });
            mSliderLayout.addSlider(sliderView);
        }
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Top);  //将小圆点设置到右下方
    }
    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        index = 1;
        listDate_g.clear();
        listDate_l.clear();
        getGunDongData();
        getListData();
        onLoad();
    }

    /**
     * 上拉加载
     */
    @Override
    public void onLoadMore() {
        index++;
        getListData();
    }

    /**
     * 刷新完成
     */
    private void onLoad() {
        xListView.stopRefresh();
        xListView.setPullLoadEnable(true);
        xListView.stopLoadMore();
        xListView.setRefreshTime("刚刚");
    }

    /**
     * item点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = position - 2;
        startNextAct(listDate_l.get(position));
    }
    /**
     * enter next activity
     *
     * @param info
     */
    private void startNextAct(ItemInfo info) {
        String linkType = info.getBak4();
        String subjectLink = info.getBak5();
        if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
            Intent tmpIntent = new Intent(mcontext, WebActivity.class);
            tmpIntent.putExtra("URL", info.getLinkurl());
            tmpIntent.putExtra("img_url",info.getImgsrc());
            tmpIntent.putExtra("TITLE", info.getTitle());
            tmpIntent.putExtra("PUBDATE", info.getPubdate());
            tmpIntent.putExtra("source", info.getInfosource());
            tmpIntent.putExtra("author", info.getInfoauthor());
            tmpIntent.putExtra("infotype", info.getInfotype());
            tmpIntent.putExtra("ROOT", "#Content");
            tmpIntent.putExtra("blank", true);
            tmpIntent.putExtra("telnum", info.getTelnum());
            tmpIntent.putExtra("latidue_e", info.getLatidue());
            tmpIntent.putExtra("longtidue_e", info.getLongtidue());
            tmpIntent.putExtra("latidue_b", mCurrentLocation.getLatitude());
            tmpIntent.putExtra("longtidue_b", mCurrentLocation.getLongitude());
            tmpIntent.putExtra("IsDuban", mIsDuban);
            tmpIntent.putExtra("depcode", info.getDepcode());
            tmpIntent.putExtra("identify", mIdentify);
            tmpIntent.putExtra("username", mUserName);
            tmpIntent.putExtra("selfids", mSelfids);
            startActivity(tmpIntent);
        } else {
            if (linkType != null && linkType == "ciist1") {

            } else {
                Intent tmpIntent = new Intent(mcontext, ChannelOfCoverStyleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subjectname", info.getTitle());
                bundle.putString("subjectcode", info.getBak5());
                bundle.putBoolean("IsDuban", mIsDuban);
                bundle.putString("depcode", info.getDepcode());
                bundle.putString("identify", mIdentify);
                bundle.putString("username", mUserName);
                bundle.putString("selfids", mSelfids);
                tmpIntent.putExtras(bundle);
                startActivity(tmpIntent);
            }
        }
    }
}
