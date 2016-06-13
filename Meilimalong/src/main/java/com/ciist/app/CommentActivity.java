package com.ciist.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.CommentAdapter;
import com.hw.ciist.util.Hutils;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private Context mContext;

    private CiistTitleView comment_title;
    private ScrollListView comment_list;
    private CommentAdapter commentAdapter;

    private static final int MSG_SUCCESS = 101;

    private List<Hutils.Ciist_entity> listData = new ArrayList<>();

    private boolean isNet;

    Handler mhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    commentAdapter = new CommentAdapter(CommentActivity.this, listData);
                    comment_list.setAdapter(commentAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mContext = getApplicationContext();
        initview();
        onInitAfterLogic();
        getDataOfComment();

    }

    private void initview() {
        comment_list = (ScrollListView) findViewById(R.id.comment_list);
        comment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hutils.Ciist_entity ce = (Hutils.Ciist_entity) parent.getAdapter().getItem(position);
                String linkType = ce.Remark4;
                String subjectLink = ce.Remark5;
                if (subjectLink == null || subjectLink == "" || subjectLink.length() < 1) {
                    Intent tmpIntent = new Intent(mContext, WebCommentActivity.class);
                    tmpIntent.putExtra("subjecttitle",ce.Title);
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

                        Intent tmpIntent = new Intent(mContext, WebCommentActivity.class);
                        Bundle bundle = new Bundle();
                        tmpIntent.putExtra("subjecttitle",ce.Title);
                        bundle.putString("subjectname", ce.Title);
                        bundle.putString("subjectcode", ce.Remark5);
                        tmpIntent.putExtras(bundle);
                        startActivity(tmpIntent);
                    }
                }
            }
        });

        comment_title = (CiistTitleView) findViewById(R.id.comment_titleview);
        comment_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void onInitAfterLogic() {
        listData = new ArrayList<Hutils.Ciist_entity>();
    }


    /**
     * 获取意见征集数据
     */
    private void getDataOfComment() {
        isNet = Hutils.getNetState(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNet) {


                        String InformationPath = ServerInfo.GetInfoPre
                                + "5D8FC8C9-786E-4E02-8103-23D0D43AADEE" + "/" + 1 + "/"
                                + 10;
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