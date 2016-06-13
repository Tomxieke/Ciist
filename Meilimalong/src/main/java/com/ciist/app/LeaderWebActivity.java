package com.ciist.app;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ciist.customview.xlistview.WebViewWithProgress;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.ShareBoardlistener;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LeaderWebActivity extends ActionBarActivity {
    private String mCommentStr;  //评论的内容

    String WhereUrl = "";
    String img_url = "";
    boolean isBlank = false;
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int POST_SUCCESS = 2;//评论上传成功
    private static final int POST_FAILURE = 3;//评论上传失败
    private static final int GET_GOOD_SUCCESS = 4;//点赞成功
    private static final int GET_GOOD_FAILURE = 5;//点赞不成功
    private static final int HAS_PRAISE = 6; //该手机用户已经点赞过该资讯
    private ProgressBar waiting;
    WebViewWithProgress webviewp;
    WebView webview;
    FilterZixunHtmlThread thread;
    //    ProgressDialog loading;
    String RootNode;
    String telNum = "";
    String telDesc = "电话联系";
    double longtidue = 0;
    double latidue = 0;
    double longtidue_b = 0;
    double latidue_b = 0;
    boolean IsDuban=false;
    String DubanTitle="";
    String DubanDepCode="";
    String mIdentify="";
    String mUsername="";
    String mSelfids="";

    String mvisb="";

    boolean isLeader;


    private static final String APPLICATION_JSON = "application/json";
    public PageInfo currentPageInfo = new PageInfo();
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
//    private EditText editText;
//    private ImageView likeImg;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    String title;
    String pubDate;
    String infosource;
    String infoAuthor;
    String infoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_web);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        loading = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
//        loading.show();
        waiting = (ProgressBar) findViewById(R.id.lead_web_waitloading);
        webviewp = (WebViewWithProgress) findViewById(R.id.lead_web_webView);
        webview = webviewp.getWebView();
        Intent i = this.getIntent();
        WhereUrl = i.getStringExtra("URL");
        isBlank = i.getBooleanExtra("blank", false);
        title = i.getStringExtra("TITLE");
        pubDate = i.getStringExtra("PUBDATE");
        telNum = i.getStringExtra("telnum");
        longtidue = i.getDoubleExtra("longtidue_e", 0);
        img_url = i.getStringExtra("img_url");
        mvisb=i.getStringExtra("leader");

        Log.d("test","-------------------------"+mvisb+"asjfiofjioajsiofjiasojfiojasfwafassaas");
//        if(mvisb==mvisb){
//
//        }
//



        IsDuban = i.getBooleanExtra("IsDuban", false);
        DubanDepCode=i.getStringExtra("depcode");
        DubanTitle=i.getStringExtra("dubantitle");
        mIdentify=i.getStringExtra("identify");
        mUsername=i.getStringExtra("username");
        mSelfids=i.getStringExtra("selfids");

        latidue = i.getDoubleExtra("latidue_e", 0);
        longtidue_b = i.getDoubleExtra("longtidue_b", 0);
        latidue_b = i.getDoubleExtra("latidue_b", 0);
        telDesc = i.getStringExtra("teldesc");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dstr = pubDate;
        try {
            java.util.Date date = sdf.parse(dstr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int mm = c.get(Calendar.MONTH) + 1;
            pubDate = c.get(Calendar.YEAR) + "年" + mm + "月" + c.get(Calendar.DAY_OF_MONTH) + "日";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RootNode = i.getStringExtra("ROOT");
        infosource = i.getStringExtra("source");
        infoAuthor = i.getStringExtra("author");
        infoType = i.getStringExtra("infotype");
        setTitle(title);
        try {
            thread = new FilterZixunHtmlThread();
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        editText = (EditText) findViewById(R.id.comment_edt);
        //    editText.setHint("说两句");
//        initTalkView();
        new IsMyLikeThread().start(); //检查该讯息该手机用户是否已经点过赞了。

    }


    private void umengShare(){
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                        SHARE_MEDIA.SMS,SHARE_MEDIA.EMAIL
                };
        if(img_url==null || img_url.equals("")|| !img_url.toLowerCase().contains("http://")){
            img_url="http://www.ciist.com:2015/meilimalong/meilimalong_logo.png";
        }else{
            img_url=img_url.replaceAll("211.149.212.154","www.ciist.com");
        }
        UMImage image = new UMImage(LeaderWebActivity.this, img_url);
        WhereUrl=WhereUrl.replaceAll("211.149.212.154","www.ciist.com");
        new ShareAction(this).setDisplayList( displaylist )
                .withText( title )
                .withTitle("魅力马龙")
                .withTargetUrl(WhereUrl)
                .withMedia( image )
//            .setListenerList(umShareListener,umShareListener)
//            .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    /**
     * 分享方法
     */
    /*private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("魅力马龙");

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(WhereUrl);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(title+WhereUrl);  //所有平台就用这个字段就可以搞定

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //    oks.setImagePath("/storage/emulated/test.jpg");//确保SDcard下面存在此张图片

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(WhereUrl);

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(WhereUrl);

        // 启动分享GUI
        oks.show(this);
    }*/

   /**
     * 评论布局部分
    */
//    private void initTalkView() {
//        final RelativeLayout likeAndShare = (RelativeLayout) findViewById(R.id.like_and_share_layout);
//
//
//        ImageView shareImg = (ImageView) findViewById(R.id.share_img);
//        shareImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                showShare();
//                umengShare();
//            }
//        });
//
//
//        likeImg = (ImageView) findViewById(R.id.like_img);
//        likeImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new LikeThread().start();
//                /*likeImg.setImageResource(R.mipmap.ciist_icon_news_good_on720);
//                //将这个点击传递给服务器。 喜欢数+1
//                Toast.makeText(WebActivity.this, "成功点赞", Toast.LENGTH_SHORT).show();
//                likeImg.setClickable(false);*/
//            }
//        });
//
//        final ImageView sendImg = (ImageView) findViewById(R.id.commend_send_img);
//        sendImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 发送评论内容到服务器
//                new CommentThread(mCommentStr).start();
//                editText.setText("");
//                //    editText.setFocusable(false);
//                editText.clearFocus();
//                likeAndShare.setVisibility(View.VISIBLE);
//                sendImg.setVisibility(View.GONE);
//                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).
//                        hideSoftInputFromWindow(WebActivity.this.getCurrentFocus().
//                                getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); //发送之后关闭输入法
//
//                editText.setCompoundDrawables(getResources().getDrawable(R.mipmap.ciist_icon_news_write),
//                        null,null,null);
//
//            }
//        });
//
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus){
//                    editText.setCompoundDrawables(null,null,null,null);
//                    int newHeight = 300;
//                    //注意这里，到底是用ViewGroup还是用LinearLayout或者是FrameLayout，主要是看你这个EditTex
//                    //控件所在的父控件是啥布局，如果是LinearLayout，那么这里就要改成LinearLayout.LayoutParams
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
//                    lp.height = newHeight;
//                    editText.setLayoutParams(lp);
//
//                    likeAndShare.setVisibility(View.GONE);
//                    sendImg.setVisibility(View.VISIBLE);
//
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//
//                }else {
//                    editText.setCompoundDrawables(getResources().getDrawable(R.mipmap.ciist_icon_news_write),
//                            null,null,null);
//                    //注意这里，到底是用ViewGroup还是用LinearLayout或者是FrameLayout，主要是看你这个EditTex
//                    //控件所在的父控件是啥布局，如果是LinearLayout，那么这里就要改成LinearLayout.LayoutParams
//                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
//                    lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                    editText.setLayoutParams(lp);
//                    String editTextStr = editText.getText().toString();
//                    if (editTextStr == null || editTextStr.equals("")){
//                        likeAndShare.setVisibility(View.VISIBLE);
//                        sendImg.setVisibility(View.GONE);
//                    }
//                    // 隐藏输入法
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//
//            }
//        });
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                mCommentStr = editText.getText().toString();
//                canInput(mCommentStr);
//                //    likeImg.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mCommentStr = editText.getText().toString();
//                canInput(mCommentStr);
//                //    likeImg.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                mCommentStr = editText.getText().toString();
//                canInput(mCommentStr);
//                //   likeImg.setVisibility(View.GONE);
//            }
//        });
//
//
//    }

    /**
     * cut whereurl get infoids
     * @param whereUrl
     * @return
     */
    private String getInfoIds(String whereUrl){
        int type = whereUrl.lastIndexOf("/");
        String infoIds = whereUrl.substring(type+1,whereUrl.length());
        return infoIds;
    }

    /**
     * 检查该手机用户是否对该讯息已经点赞
     * 失败 -9，否则返回当前信息我的点赞次数
     */
    private class IsMyLikeThread extends Thread{
        @Override
        public void run() {
            String returnString = "";
            ResultInfo result = new ResultInfo();
            String _url = ServerInfo.ServerBKRoot+"/gov/CheckPraise/ciistkey/"
                    +getInfoIds(WhereUrl)+"/"+getDeviceId();
            //    Log.d("test","-----url------"+_url);
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            JSONObject json = null;
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    returnString = EntityUtils.toString(entity);
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

            //    Log.e("test","---phoneNum----:"+returnString);
            if (Integer.parseInt(returnString) != 0){   //已经点赞过了。
                mHandler.obtainMessage(HAS_PRAISE,result).sendToTarget();
            }
        }
    }

    /**
     * 点赞线程
     * 失败 -9，否则成功
     */
    private class LikeThread extends Thread{
        @Override
        public void run() {
            ResultInfo result = new ResultInfo();
            String returnString = "";
            String _url = ServerInfo.ServerBKRoot+"/gov/Praise/ciistkey/"+getInfoIds(WhereUrl)+"/"+getDeviceId();
            //    Log.d("test","-----url------"+_url);
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            JSONObject json = null;
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    returnString = EntityUtils.toString(entity);
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
            //    Log.e("test","---phoneNum----:"+returnString);
            if (returnString != "-9"){
                mHandler.obtainMessage(GET_GOOD_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(GET_GOOD_FAILURE,result).sendToTarget();
            }

        }
    }

    /**
     * 获取Device_ID
     * @return
     */
    private String getDeviceId(){
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }

    /**
     * 传输评论线程
     * 上传成功返回OK
     */
    private class CommentThread extends Thread{
        private String mCommentStr;
        public CommentThread(String commentStr){
            this.mCommentStr = commentStr;
        }

        String resultStr = ""; //上传是否成功返回的结果
        @Override
        public void run() {
            HttpClient httpClient = null;
            ResultInfo result = new ResultInfo();
            String _url = ServerInfo.ServerBKRoot + "/info/InfoComment";  //上传地址
            //    Log.e("test","+_url++"+_url);
            //    Log.e("test","--mSelfids--"+mSelfids+"--mIdentify--"+mIdentify);
            HttpPost _post = new HttpPost(_url);
            _post.setHeader("Accept", APPLICATION_JSON);
            _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("SKEY","ciistkey");
                jsonObject.put("SelfIDS",mSelfids);
                jsonObject.put("PageIndex",currentPageInfo.getPageIndex());
                jsonObject.put("PageSize",currentPageInfo.getPageSize());
                jsonObject.put("Identify",mIdentify);


                JSONObject sendObj = new JSONObject();
                sendObj.put("_header",jsonObject);
                sendObj.put("infoids",getInfoIds(WhereUrl));
                sendObj.put("comment",mCommentStr);

                StringEntity entity = new StringEntity(sendObj.toString(), HTTP.UTF_8);//需要设置成utf-8否则汉字乱码
                entity.setContentType(CONTENT_TYPE_TEXT_JSON);
                entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
                _post.setEntity(entity);
                // 向WCF服务发送请求

                httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(_post);
                // 判断是否成功,返回的为OK则成功
                if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                    resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (httpClient != null){
                    httpClient.getConnectionManager().shutdown();  //关闭连接
                }
            }
            //post end
            //    Log.e("test","--result---"+resultStr.substring(1,3));
            if (resultStr.substring(1,3).equalsIgnoreCase("OK")){
                mHandler.obtainMessage(POST_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(POST_FAILURE,result).sendToTarget();
            }

        }
    }

    /**
     * 是否还可以输入
     * @param str
     */
    private void canInput(String str){
        if (str == null){
            return;
        }else {
            if (str.length() >=60){
                Toast.makeText(LeaderWebActivity.this,"已经超出评论字数",Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * get data Handler
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
//                case HAS_PRAISE:  //已经点赞过了
//                    likeImg.setImageResource(R.mipmap.ciist_icon_news_good_on720);
//                    likeImg.setClickable(false);
//                    break;
//                case GET_GOOD_SUCCESS:
//                    likeImg.setImageResource(R.mipmap.ciist_icon_news_good_on720);
//                    //将这个点击传递给服务器。 喜欢数+1
//                    Toast.makeText(WebActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
//                    likeImg.setClickable(false);
//                    break;
                case GET_GOOD_FAILURE:
                    Toast.makeText(LeaderWebActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                    break;
                case POST_SUCCESS:
                    Toast.makeText(LeaderWebActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                    break;
                case POST_FAILURE:
                    Toast.makeText(LeaderWebActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SUCCESS:
                    String backhtml = (String) msg.obj;
                    webview.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
                    webview.getSettings().setSupportZoom(true);
                    webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
                    webview.getSettings().setBuiltInZoomControls(true);
//                    WebSettings ws = wv.getSettings();
//
//                    ws.setUseWideViewPort(true);
//
//                    ws.setJavaScriptEnabled(true);
//
//                    ws.setSupportZoom(true); //设置可以支持缩放
//
//                    ws.setDefaultZoom(WebSettings.ZoomDensity.FAR);
//
//                    ws.setBuiltInZoomControls(true);//设置出现缩放工具


//                    String htmlheader = "<style type='text/css'> img{width:100%;}</style>";
                    String htmlheader = "<!DOCTYPE html><html lang='en'><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta charset='utf-8'><title>ciist</title><link href='" + ServerInfo.ServerRoot + "/btn.css' rel='stylesheet' type='text/css' /></head><body>    <style type='text/css'> img{width:100%;}</style>";
                    String htmlender = "</body></html>";
                    if (!isBlank) {
                        if (infoType.equals("AD")) {
                        } else if (infoType.equals("hipop")) {
                        } else if (infoType.equals("zhaoshang")) {
                            htmlheader += "<h2>" + title + "</h2>" + infoAuthor + "&nbsp;&nbsp;&nbsp;&nbsp;  " + infosource + "<hr> ";
                        } else {
                            htmlheader += "<h2>" + title + "</h2>" + pubDate + "&nbsp;&nbsp;&nbsp;&nbsp;  " + infoAuthor + "&nbsp;&nbsp;&nbsp;&nbsp;  " + infosource + "<hr> ";
                        }
                    }

                    webview.loadData(htmlheader + backhtml + htmlender, "text/html; charset=UTF-8", null);//这种写法可以正确解码
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                        Button telbtn = (Button) findViewById(R.id.lead_web_btnTel);
                        Button mapnavbtn = (Button) findViewById(R.id.lead_web_btnMapNav);
                        Button DuBanBtn= (Button) findViewById(R.id.lead_web_btnWebDuban);
                        if (telNum!=null && telNum != "" && telNum.length() > 3) {
                            telbtn.setVisibility(View.VISIBLE);
                            telbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNum));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                    }
                                    startActivity(intent);
                                }
                            });
                        } else {
                            telbtn.setVisibility(View.GONE);
                        }
                        if(IsDuban==true && !DubanDepCode.equals("")){
                            DuBanBtn.setVisibility(View.VISIBLE);
                            DuBanBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent tmpIntent = new Intent(LeaderWebActivity.this, IndexOfOACreateNewTaskActivity.class);
                                    Bundle bundle = new Bundle();
                                    //mIdentify   mUsername   mSelfids
                                    bundle.putBoolean("IsDuban", IsDuban);
                                    bundle.putString("identify", mIdentify);
                                    bundle.putString("username", mUsername);
                                    bundle.putString("selfids", mSelfids);
                                    bundle.putString("depcode", DubanDepCode);
                                    tmpIntent.putExtras(bundle);
                                    startActivity(tmpIntent);
                                }
                            });
                        }else{
                            DuBanBtn.setVisibility(View.GONE);
                        }
                        if (longtidue > 0 && latidue > 0) {
                            mapnavbtn.setVisibility(View.VISIBLE);
                            mapnavbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent tmpIntent = new Intent(LeaderWebActivity.this, MapStartActivity.class);
                                    tmpIntent.putExtra("latidue_e", latidue);
                                    tmpIntent.putExtra("longtidue_e", longtidue);
                                    tmpIntent.putExtra("latidue_b", latidue_b);
                                    tmpIntent.putExtra("longtidue_b", longtidue_b);
                                    startActivity(tmpIntent);
                                }
                            });
                        } else {
                            mapnavbtn.setVisibility(View.GONE);
                        }
                    }
                    break;
                case MSG_FAILURE:
                    Toast.makeText(LeaderWebActivity.this, "亲，获取数据失败了，请检查您的网络", Toast.LENGTH_SHORT).show();
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        ;
    };


    private class FilterZixunHtmlThread extends Thread {
        public void run() {
            Document doc;
            String ContHtml = "";
            try {
                doc = Jsoup.connect(WhereUrl).timeout(5000).get();
                Document content = Jsoup.parse(doc.toString());
                Elements divs = content.select(RootNode);
                ContHtml = divs.toString();
                mHandler.obtainMessage(MSG_SUCCESS, ContHtml).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, ContHtml).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, ContHtml).sendToTarget();
            }
        }
    }

}
