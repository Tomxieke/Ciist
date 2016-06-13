package com.ciist.app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.GISLocation;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.DocumentGerenAdapter;
import com.hw.ciist.util.Hutils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class DocumentManaFragment extends Fragment {

    private Context mcontext;
    private boolean isNet;
    private GISLocation mCurrentLocation = new GISLocation();
    private String mIdentify = "";
    private String mUserName = "";
    private String mSelfids = "";
    private boolean mIsDuban = false;

    private static final String TAG_PARMA = "param";
    private String mParam;

    private static final int MSG_SUCCESS = 101;
    private static final int MSG_SUCCESS_QI = 102;

    private CiistTitleView documentgeren_title;
    private ScrollListView documentgeren_list;
    private DocumentGerenAdapter documentGerenAdapter;


    private ArrayList<Hutils.Ciist_entity> listData = new ArrayList<>();

    public static DocumentManaFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TAG_PARMA, title);
        DocumentManaFragment docfragment = new DocumentManaFragment();
        docfragment.setArguments(args);
        return docfragment;
    }


    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    documentGerenAdapter = new DocumentGerenAdapter(mcontext);
                    documentGerenAdapter.setmData(listData);
                    documentgeren_list.setAdapter(documentGerenAdapter);

                    break;
            }
        }

    };

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

        View view = inflater.inflate(R.layout.fragment_document_mana, container, false);


        documentgeren_list = (ScrollListView) view.findViewById(R.id.documen_geren_list);
        documentgeren_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hutils.Ciist_entity ce = (Hutils.Ciist_entity) parent.getAdapter().getItem(position);
                String linkType = ce.Remark4;
                String subjectLink = ce.Remark5;
                if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                    Intent tmpIntent = new Intent(mcontext, Web2Activity.class);
                    tmpIntent.putExtra("color", getResources().getColor(R.color.hongsetitle));
                    tmpIntent.putExtra("subjectname2",ce.Title);
                    tmpIntent.putExtra("URL", ServerInfo.InfoDetailPath + ce.Info_Ids);
                    tmpIntent.putExtra("TITLE", ce.Title);
                    tmpIntent.putExtra("PUBDATE", ce.pubDate);
                    tmpIntent.putExtra("source", ce.Sourse);
                    tmpIntent.putExtra("author", ce.Author);
                    tmpIntent.putExtra("infotype", ce.Type);
                    tmpIntent.putExtra("ROOT", "#Content");
                    tmpIntent.putExtra("blank", false);
                    startActivity(tmpIntent);

                } else {
                    if (linkType != null && linkType == "ciist1") {
                    } else {

                        Intent tmpIntent = new Intent(mcontext, DocumentTiaozhuanActivity.class);
                        Bundle bundle = new Bundle();
//                        bundle.putInt("color",getResources().getColor(R.color.hongsetitle));
                        bundle.putString("subjectname2",ce.Title);
                        bundle.putString("subjectname", ce.Title);
                        bundle.putString("subjectcode", ce.Remark5);
                        tmpIntent.putExtras(bundle);
                        startActivity(tmpIntent);
                    }
                }
            }
        });
        getGerenBanshi();
        return view;
    }


    /**
     * 获取个人办事数据
     */
    private void getGerenBanshi() {

        isNet = Hutils.getNetState(mcontext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "F49868E9-0FE3-42B9-9E4A-2947CE5E5CB9" + "/" + 1 + "/"
                                + 20;
                        String TodayMalong = Hutils.fromNetgetData(InformationPath, true);

                        listData.addAll(Hutils.parseJSONData(TodayMalong, null));


                    }

                    mhandler.sendEmptyMessage(MSG_SUCCESS);


                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }) {


        }.start();


    }


}