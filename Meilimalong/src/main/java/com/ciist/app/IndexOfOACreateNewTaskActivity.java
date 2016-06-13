package com.ciist.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.entities.ItemInfo;
import com.ciist.entities.PageInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.entities.UserInfo;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class IndexOfOACreateNewTaskActivity extends AppCompatActivity {

    // UI references.
    private boolean mIsDuban = false;
    private String mDepCode="";
    private EditText mTask_zh;
    private EditText mTkbegindate;
    private EditText mTkenddate;
    private EditText mTkCont;
    private Button mCheckBoxButtonC;
    private Button mSure;
    private View mProgressView;
    private View mCreateTaskFormView;
    private List<String> mUserReciver;
    private String mIdentify;
    private ProgressBar mProgress;
    private String mSelfids;
    private String mUsername;
    private InternetOfSubordinateThread subjectThread;
    private ArrayList<UserInfo> subordinateUser;
    private ArrayList<UserInfo> currentSelectedUser = new ArrayList<UserInfo>();
    public PageInfo currentPageInfo = new PageInfo();
    private static final int MSG_SUCCESS = 0;//成功
    private static final int MSG_FAILURE = 1;//失败
    private static final int MSG_NODATA = 2;//成功但是没有数据
    private static final int MSG_CREATEOK = 100;//成功但是没有数据
    private static final int MSG_LOADCOMPLETED = 9;
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) { //该方法是在UI主线程中执行
            switch (msg.what) {
                case MSG_SUCCESS:
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_FAILURE:
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_NODATA:
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_LOADCOMPLETED:
                    mProgress.setVisibility(View.GONE);
                    break;
                case MSG_CREATEOK:
                    mProgress.setVisibility(View.GONE);
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                    Intent _int = new Intent();
                    _int.putExtra("RESULT_WHAT", "CREATEOK");
                    setResult(IndexOfOAMainActivity.RESULT_REFREASH, _int);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_of_oa_create_new_task_activity);
        // Set up the login form.
        Intent intent = this.getIntent();
        mIdentify = intent.getStringExtra("identify");
        mUsername = intent.getStringExtra("username");
        mSelfids = intent.getStringExtra("selfids");
        mIsDuban = intent.getBooleanExtra("IsDuban", false);
        mDepCode=intent.getStringExtra("depcode");
        initView();
        getSuborinate();

    }

    void getSuborinate() {
        try {
            mProgress.setVisibility(View.VISIBLE);
            subjectThread = new InternetOfSubordinateThread();
            subjectThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage().toString());
        }
    }

    private class InternetOfSubordinateThread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = "";
                if (mIsDuban) {
                    _url = ServerInfo.ServerBKRoot + "/info/getDepUsers/ciistkey/" + mIdentify+"/"+mDepCode + "/1/10";
                }else{
                    _url = ServerInfo.ServerBKRoot + "/info/getSubordinate/ciistkey/" + mIdentify + "/1/10";
                }

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
                mHandler.obtainMessage(MSG_LOADCOMPLETED, result).sendToTarget();
                if (subordinateUser == null) {
                    subordinateUser = new ArrayList<UserInfo>();
                }
                areas = new String[jsonArray.length()];
                areaState = new boolean[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String _duti = jsonObj.getString("Duties");
                    if (_duti != null && !_duti.equals("")) {
                        areas[i] = jsonObj.getString("User_zh") + "(" + jsonObj.getString("Duties") + ")";
                    } else {
                        areas[i] = jsonObj.getString("User_zh");
                    }

                    areaState[i] = false;
                    UserInfo tmp = new UserInfo();
                    tmp.setDids(jsonObj.getString("Dids"));
                    tmp.setDuties(jsonObj.getString("Duties"));
                    tmp.setIsmaster(jsonObj.getInt("Ismaster"));
                    tmp.setSelfids(jsonObj.getString("Account"));
                    tmp.setAccount(jsonObj.getString("Account"));
                    tmp.setTetitle(jsonObj.getString("Tetitle"));
                    tmp.setUser_zh(jsonObj.getString("User_zh"));
                    subordinateUser.add(tmp);
                }

                //把数据赋给联系人选择列表
                ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(IndexOfOACreateNewTaskActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, areas);
                dialogList.setAdapter(dialogAdapter);

//                currentPageInfo.setPageCount(0);
//                currentPageInfo.setTotalCount(0);
//                result.setPageinfo(currentPageInfo);
//                result.setSimpleItemObj(data);
//                result.setResultCode("102");
//                mHandler.obtainMessage(MSG_SUCCESS, result).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }

        }
    }

    void initView() {
        mTask_zh = (EditText) findViewById(R.id.Task_zh);
        if (mTask_zh != null) {
            if (mIsDuban) {
                mTask_zh.setHint("督办主题");
                mTask_zh.setText("督办：");
                TextInputLayout task_zh_label = (TextInputLayout) findViewById(R.id.Task_zh_label);
                if (task_zh_label != null) {
                    task_zh_label.setHint("督办主题");
                }
            } else {
                mTask_zh.setHint("任务标题");

            }
        }
        mTkbegindate = (EditText) findViewById(R.id.Tkbegindate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datenow = sdf.format(new java.util.Date());
        mTkbegindate.setText(datenow);
        if (mTkbegindate != null) {
            if (mIsDuban) {
                mTkbegindate.setVisibility(View.GONE);
            } else {
                mTkbegindate.setVisibility(View.VISIBLE);
            }
        }
        mTkenddate = (EditText) findViewById(R.id.Tkenddate);
        mTkenddate.setText(datenow);
        if (mTkenddate != null) {
            if (mIsDuban) {
                mTkenddate.setHint("督办要求完成日期");
                TextInputLayout task_enddate_label = (TextInputLayout) findViewById(R.id.Tkenddate_label);
                if (task_enddate_label != null) {
                    task_enddate_label.setHint("督办要求完成日期");
                }
            } else {
                mTkenddate.setHint("结束日期");
            }
        }
        mTkCont = (EditText) findViewById(R.id.Tkcont);
        if (mTkCont != null) {
            if (mIsDuban) {
                mTkCont.setHint("督办要求");
                TextInputLayout task_cont_label = (TextInputLayout) findViewById(R.id.Tkcont_label);
                if (task_cont_label != null) {
                    task_cont_label.setHint("督办要求");
                }
            } else {
                mTkCont.setHint("任务要求");
            }
        }
        mCheckBoxButtonC = (Button) findViewById(R.id.checkBoxButton);
        if (mCheckBoxButtonC != null) {
            if (mIsDuban) {
                mCheckBoxButtonC.setText("选择督办对象");
            } else {
                mCheckBoxButtonC.setText("选择承办人");
            }
        }
        mProgress = (ProgressBar) findViewById(R.id.oacreatenew_progress);
        Button mCancle = (Button) findViewById(R.id.no_create_button);
        mCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSure = (Button) findViewById(R.id.create_button);
        mSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTask_zh.getText() == null || mTask_zh.getText().toString().equals("")) {
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "请输入任务", Toast.LENGTH_SHORT).show();
                    mTask_zh.requestFocus();
                    return;
                }
                if (mTkbegindate.getText() == null || mTkbegindate.getText().toString().equals("")) {
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "请选择任务开始日期", Toast.LENGTH_SHORT).show();
                    mTkbegindate.requestFocus();
                    return;
                }
                if (mTkenddate.getText() == null || mTkenddate.getText().toString().equals("")) {
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "请选择任务结束日期", Toast.LENGTH_SHORT).show();
                    mTkenddate.requestFocus();
                    return;
                }
                if (currentSelectedUser == null || currentSelectedUser.size() <= 0) {
                    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "请选择任务承办人", Toast.LENGTH_SHORT).show();
                    return;
                }

                beginCreate();

            }
        });

        mTkbegindate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                Dialog dialog = new DatePickerDialog(
                        IndexOfOACreateNewTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                mTkbegindate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                dialog.show();
            }
        });
        mTkenddate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                Dialog dialog = new DatePickerDialog(
                        IndexOfOACreateNewTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                mTkenddate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        },
                        c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                dialog.show();
            }
        });


        //联系人搜索功能，------------------------------------------------------------
        //dialog弹出选择联系人的开始
        LayoutInflater inflater = LayoutInflater.from(IndexOfOACreateNewTaskActivity.this);
        final RelativeLayout dialogLayout = (RelativeLayout) inflater.inflate(R.layout.pop_dialog_layout,null);
        RelativeLayout dialogSearchBtn = (RelativeLayout) dialogLayout.findViewById(R.id.dialog_search_btn);
        dialogSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexOfOACreateNewTaskActivity.this,DialogSearchActivity.class);
                intent.putExtra("names",areas);
                startActivityForResult(intent,0);
            }
        });
        dialogShowTxt = (TextView) dialogLayout.findViewById(R.id.dialog_show_chocedname);
        dialogList = (ListView) dialogLayout.findViewById(R.id.dialog_listview);

        for (int i = 0;i<areas.length;i++){
            Log.d("test", "第" + i + "个是：" + areas[i] + "    ");
        }

        dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogShowTxt.setText(getChoceNames());
            }
        });

        //确定选项
        Button sureBtn = (Button) dialogLayout.findViewById(R.id.sureBtn);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedUser.clear();

                long[] authorsId = dialogList.getCheckItemIds();
                String name = "";
                String message;
                if (authorsId.length > 0) {
                    // 用户至少选择了一位作家
                    for (int i = 0; i < authorsId.length; i++) {
                        name += "," + areas[(int) authorsId[i]];

                        currentSelectedUser.add(subordinateUser.get((int) authorsId[i]));  //添加联系人到已选择联系人
                    }

                    for (int i = 0;i <currentSelectedUser.size();i++){
                //        Toast.makeText(IndexOfOACreateNewTaskActivity.this,"已选中的联系人    "+currentSelectedUser.get(i).getUser_zh(),
                //                Toast.LENGTH_SHORT).show();
                    }

                    // 将第一个作家前面的“，”去掉
                    message = name.substring(1);
                    //    showCheckedName.setText("你选择了   "+message);
                } else {
                    message = "请至少选择一位作家！";
                }
            //    Toast.makeText(IndexOfOACreateNewTaskActivity.this, "dialog你选择了   "+message, Toast.LENGTH_LONG)
               //         .show();
                dialog.dismiss();

            }
        });

        //取消事件
        Button canclBtn = (Button) dialogLayout.findViewById(R.id.canclBrn);
        canclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        mCheckBoxButtonC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null){
                    dialog = new Dialog(IndexOfOACreateNewTaskActivity.this);
                    dialog.setTitle("选择联系人");
                    dialog.setContentView(dialogLayout);
                }
                dialog.show();
            }
        });
    }

    //返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = "";
        if (requestCode == 0 ){
            name = data.getStringExtra("name");
            if (name.equalsIgnoreCase("未选择联系人")){
                return;
            }
        //    Toast.makeText(IndexOfOACreateNewTaskActivity.this,"  result name   " + name
        //            ,Toast.LENGTH_SHORT).show();

            //    showCheckedName.setText("你选择了   "+  name);
        }

        dialogList.setItemChecked(getIdbyName(name,areas),true);
        dialogShowTxt.setText(getChoceNames());
    }


    /**
     * 遍历数据源找对应name值得位置
     * @param
     */
    public int getIdbyName(String name,String[] data){
        int k = 0;
        for (int i = 0;i < data.length;i++){
            if (name.equalsIgnoreCase(data[i])){
                k = i;
            }
        }
        return k;
    }

    //-----------选择联系人相关---------------
    ListView dialogList;
    TextView dialogShowTxt;
    Dialog dialog;
    /**
     * 得到已被选中的names
     * @return
     */
    public String getChoceNames(){
        //   long[] authorsId = radioButtonList.getCheckItemIds();
        long[] authorsId = dialogList.getCheckItemIds();
        String name = "";
        String message;
        if (authorsId.length > 0) {
            // 用户至少选择了一位作家
            for (int i = 0; i < authorsId.length; i++) {
                name += "," + areas[(int) authorsId[i]];
            }
            // 将第一个作家前面的“，”去掉
            message = name.substring(1);
        } else {
            message = "请至少选择一位作家！";
        }
        return "您选择了："+message;
    }
    //----------------------联系人搜索-------------------


    void beginCreate() {
        try {
            mProgress.setVisibility(View.VISIBLE);
            new InternetOfBeginCreateThread().start();
        } catch (Exception e) {
        }
    }

    private class InternetOfBeginCreateThread extends Thread {
        public void run() {
            ResultInfo result = new ResultInfo();
            String jsonString = "";
            try {
                List<ItemInfo> data = new ArrayList<ItemInfo>();
                String _url = ServerInfo.ServerBKRoot + "/info/createNewTask";
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
                    _obj1.put("Task_zh", mTask_zh.getText());
                    _obj1.put("Tktype", "日常工作");
                    _obj1.put("Tkpuber", mSelfids);
                    _obj1.put("Tkpubdate", "1900-01-01");
                    _obj1.put("Tkbegindate", mTkbegindate.getText());
                    _obj1.put("Tkenddate", mTkenddate.getText());
                    _obj1.put("Tkcont", mTkCont.getText());
                    _obj1.put("Tkstate", 0);
                    _obj1.put("Relationids", "");
                    _obj1.put("Labelmark", "");
                    JSONArray _objArr = new JSONArray();
                    for (int i = 0; i < currentSelectedUser.size(); i++) {
                        JSONObject _tmp = new JSONObject();
                        _tmp.put("Isread", 0);
                        _tmp.put("Uids", currentSelectedUser.get(i).getAccount());
                        _tmp.put("Taskids", "0");
                        _tmp.put("Guids", "ciistkey");
                        _tmp.put("Visible", 1);
                        _tmp.put("Timestamp", "1900-01-01");
                        _tmp.put("Byuids", mSelfids);
                        _objArr.put(_tmp);
                    }
                    JSONObject _header = new JSONObject();
                    _header.put("SKEY", "ciistkey");
                    _header.put("PageIndex", 1);
                    _header.put("PageSize", 1);
                    _header.put("Identify", mIdentify);
                    _header.put("SelfIDS", mSelfids);
                    JSONObject sendObj = new JSONObject();

                    sendObj.put("_header", _header);
                    sendObj.put("_taskinfo", _obj1);
                    sendObj.put("_taskreciver", _objArr);

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
                    mHandler.obtainMessage(MSG_CREATEOK, result).sendToTarget();
                    return;
                } else {
                    mHandler.obtainMessage(MSG_NODATA, result).sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println(e.getMessage().toString());
                mHandler.obtainMessage(MSG_FAILURE, result).sendToTarget();
            }
        }
    }

    private String[] areas = new String[]{};
    private boolean[] areaState = new boolean[]{};
    private ListView areaCheckListView;

    class CheckBoxClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            currentSelectedUser.clear();
            AlertDialog ad = new AlertDialog.Builder(IndexOfOACreateNewTaskActivity.this)
                    .setTitle("联系人")
                    .setMultiChoiceItems(areas, areaState, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
//点击某个区域
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String s = "您选择了:";
                            for (int i = 0; i < areas.length; i++) {
                                if (areaCheckListView.getCheckedItemPositions().get(i)) {
                                    currentSelectedUser.add(subordinateUser.get(i));
//                                    s += i + ":" + areaCheckListView.getAdapter().getItem(i) + " ";
                                } else {
                                    areaCheckListView.getCheckedItemPositions().get(i, false);
                                }
                            }

//                            if (areaCheckListView.getCheckedItemPositions().size() > 0) {
//                                Toast.makeText(IndexOfOACreateNewTaskActivity.this, s, Toast.LENGTH_LONG).show();
//                            } else {
////没有选择
//
//                            }
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).create();
            areaCheckListView = ad.getListView();
            ad.show();
        }
    }
}

