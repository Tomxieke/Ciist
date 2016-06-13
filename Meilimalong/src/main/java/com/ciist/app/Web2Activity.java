package com.ciist.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.IGridView;
import com.ciist.customview.xlistview.ScrollListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.PhotoGridViewAdapter;
import com.ciist.util.ImageTools;
import com.hw.ciist.util.Hutils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Web2Activity extends ActionBarActivity {

    String WhereUrl = "";
    boolean isBlank = false;

    private ScrollView web2_scr;
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private EditText web2_name_edi;
    private EditText web2_phonenumber_edi;

    private TextView web2_name_txt;
    private ImageView web2_imageView;
    private IGridView web2_gridview;

    private CiistTitleView web2_up_title;
    private Button web2_down_title;
    private LinearLayout web2_lin;
    private LinearLayout web2_all_lin;
    private PhotoGridViewAdapter web2_adapter;

    private static final int SCALE = 2;//照片缩小比例

    private static final int MSG_SUCCESS = 101;//成功
    private static final int MSG_FAILURE = 102;//失败
    private ProgressBar waiting;
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
    boolean IsDuban = false;
    String DubanTitle = "";
    String DubanDepCode = "";
    String mIdentify = "";
    String mUsername = "";
    String mSelfids = "";

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
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
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
                        if (telNum != null && telNum != "" && telNum.length() > 3) {
                        } else {
                        }
                        if (IsDuban == true && !DubanDepCode.equals("")) {
                        } else {
                        }
                        if (longtidue > 0 && latidue > 0) {
                        } else {
                        }
                    }
                    break;
                case MSG_FAILURE:
                    Toast.makeText(Web2Activity.this, "亲，获取数据失败了，请检查您的网络", Toast.LENGTH_SHORT).show();
//                    loading.dismiss();
                    if (waiting != null) {
                        waiting.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        loading = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
//        loading.show();
        web2_name_edi = (EditText) findViewById(R.id.web2_zhengjianbanli_name_edi);
        web2_phonenumber_edi = (EditText) findViewById(R.id.web2_zhengjianbanli_phonenumber_edi);


        web2_up_title = (CiistTitleView) findViewById(R.id.web2_up_titleview);
        web2_up_title.setOnLiftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        web2_down_title = (Button) findViewById(R.id.web2_down_titleview);
        web2_down_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                != null&& web2_phonenumber_edi.getText().toString() != null  && web2_IDnumber_edi.getText().toString() != null &&web2_gridview!=null) {
//                web2_name_edi.getText().toString().equals(web2_phonenumber_edi.getText().toString().equals( web2_IDnumber_edi.getText().toString().equals(web2_gridview!=null)))
                if (web2_name_edi.length()==0||web2_phonenumber_edi.length()==0){
                    Toast.makeText(Web2Activity.this,"请输入信息",Toast.LENGTH_LONG).show();

                }else{
                    web2_lin.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Web2Activity.this, ServiceActivity.class));

                }

            }
        });
        web2_scr = (ScrollView) findViewById(R.id.web2_document_geren_scr);
        web2_lin = (LinearLayout) findViewById(R.id.web2_banli_lin);
        web2_all_lin = (LinearLayout) findViewById(R.id.web2_all_lin);
        Button web2_shangchuan = (Button) findViewById(R.id.web2_shangchuan_button);
        web2_shangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicturePicker(Web2Activity.this);
            }
        });
        Button web2_button = (Button) findViewById(R.id.web2_button);
        web2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(web2_name_edi.length()==0&&web2_phonenumber_edi.length()==0){
                    Toast.makeText(Web2Activity.this,"请输入姓名和电话",Toast.LENGTH_LONG).show();
                }else if(web2_name_edi.length()!=0&&web2_phonenumber_edi.length()!=11){
                    Toast.makeText(Web2Activity.this,"请输入11位电话号码",Toast.LENGTH_LONG).show();
                }else if(web2_name_edi.length()==0&&web2_phonenumber_edi.length()==11){
                    Toast.makeText(Web2Activity.this,"请输入姓名",Toast.LENGTH_LONG).show();
                }else if(web2_name_edi.length()!=0&&web2_phonenumber_edi.length()==11){
                    Toast.makeText(Web2Activity.this,"正在上传姓名、电话",Toast.LENGTH_LONG).show();
                }
            }
        });
        Intent intent = this.getIntent();
        web2_name_txt = (TextView) findViewById(R.id.web2_name_txt);
        web2_name_txt.setText(intent.getStringExtra("subjecttitle"));
        web2_gridview = (IGridView) findViewById(R.id.web2_photo_gridview);
        web2_adapter = new PhotoGridViewAdapter(this);
        web2_gridview.setAdapter(web2_adapter);
        web2_imageView = (ImageView) findViewById(R.id.web2_show_img);
        waiting = (ProgressBar) findViewById(R.id.web2_waitloading);
        webview = (WebView) findViewById(R.id.web2_webView);
        //调用拨号功能
        webview.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view,String url){
                //调用拨号程序
                if (url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });

        Intent i = getIntent();
        WhereUrl = i.getStringExtra("URL");
        isBlank = i.getBooleanExtra("blank", false);
        title = i.getStringExtra("TITLE");
        pubDate = i.getStringExtra("PUBDATE");
        telNum = i.getStringExtra("telnum");
        longtidue = i.getDoubleExtra("longtidue_e", 0);

        IsDuban = i.getBooleanExtra("IsDuban", false);
        DubanDepCode = i.getStringExtra("depcode");
        DubanTitle = i.getStringExtra("dubantitle");
        mIdentify = i.getStringExtra("identify");
        mUsername = i.getStringExtra("username");
        mSelfids = i.getStringExtra("selfids");

        latidue = i.getDoubleExtra("latidue_e", 0);
        longtidue_b = i.getDoubleExtra("longtidue_b", 0);
        latidue_b = i.getDoubleExtra("latidue_b", 0);
        telDesc = i.getStringExtra("teldesc");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dstr = pubDate;
        try {
            Date date = sdf.parse(dstr);
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
    }


    public static void scrollToBottom(final View scroll, final View inner) {
        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }

                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }

    /**
     * 弹出dialog选择拍照或者是相册去照片
     *
     * @param context
     */
    public void showPicturePicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //    String photoName = getPhotoFileName();   //用当前时间来命名照片
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        Log.v("test", "-----photoUrl-------" + imageUri);
                        //  openCameraIntent.putExtra("name",photoName);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 选择照片或者拍照后返回的结果并处理 主要包括保存处理或压缩图片处理。
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  mShowLayout.setVisibility(View.VISIBLE);
        Log.v("text", "----------" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    //   String photoName = data.getStringExtra("name");
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);

                    //   imageView.setImageBitmap(newBitmap);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    //   iv_image.setImageBitmap(newBitmap);
                    String photoName = getPhotoFileName();  //photo name
                    String photoPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    ImageTools.savePhotoToSDCard(newBitmap, photoPath, photoName);   //save photo
                    //   putPath(photoPath+"/"+photoName);
                    Log.e("test", "--TAKE_PICTURE--path--" + photoPath + "/" + photoName);
                    //    Glide.with(this).load(photoPath+"/"+photoName).into(imageView);
                    putPath(photoPath + "/" + photoName);
                    break;


                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();

                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    Log.e("test", "++onActivityResult+getDataString+" + data.getDataString() + "  getData " + data.getData());
                    //    putPath(originalUri);  //
                    Log.v("test", "-----CHOOSE_PICTURE--originalUri-----" + originalUri);
                    Log.e("test", "++++++++++++++");
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            //    iv_image.setImageBitmap(smallBitmap);
                            String photoName1 = getPhotoFileName();  //photo name
                            String photoPath1 = Environment.getExternalStorageDirectory().getAbsolutePath();

                            ImageTools.savePhotoToSDCard(smallBitmap, photoPath1, photoName1);   //save photo并压缩
                            putPath(photoPath1 + "/" + photoName1);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    ArrayList<String> pathLists = null;

    private void putPath(String path) {

        if (null == pathLists) {
            pathLists = new ArrayList<>();
        }
        pathLists.add(path);
        web2_adapter.setData(pathLists);
    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date);
    }

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
