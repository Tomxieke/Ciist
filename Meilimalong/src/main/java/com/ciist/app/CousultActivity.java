package com.ciist.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.CustomDialog;
import com.ciist.entities.ServerInfo;
import com.ciist.util.SharedPreferenceHelper;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.view.DorpbackScrollView;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class CousultActivity extends Activity implements OnClickListener {

    public static final int SUCCESS = 0x45;
    public static final int CONMMIT_SUCCESS = 0x46;
    public static final int COMMIT_GUOQI = 0x47;
    public static final int COMMIT_CANCEL = 0x48;

    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    //底部三大引导控件
	private TextView cousult_explain_tv;
    private TextView cousult_mying_tv;
    private TextView cousult_respong_tv;

    //中间三大界面切换控件
    private DorpbackScrollView cousult_explain_dsc;
    private DorpbackScrollView cousult_mying_dsc;
    private DorpbackScrollView cousult_respond_dsc;

    //其他应用控件
    private TextView cousult_explain_txt; //咨询说明
    private ListView cousult_respond_lv; //咨询回复列表

    //我要咨询界面应用控件
    private EditText cousult_theme_edt;
    private EditText cousult_content_edt;

    private TextView cousult_department_tv; //咨询部门

    private Context mContext;
    private boolean mNetState;
    private String mStr = null;
    private ArrayList<Hutils.Ciist_entity> mData = null;
    private String mUrlPath = ServerInfo.GetInfoPre + "F49868E9-0FE3-42B9-9E4A-2947CE5E5CB9/1/10";//咨询说明接口
    private String mUrlPath2 = ServerInfo.GetInfoPre + "F49868E9-0FE3-42B9-9E4A-2947CE5E5CB9/1/10";//咨询回复接口
    private boolean isRespond = false;

    //咨询表示
    private CiistTitleView cousult_titleview;
    SharedPreferenceHelper sharedPreferenceHelper = null;
    private ProgressDialog d = null;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    parseData();
                    break;
                case CONMMIT_SUCCESS:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    d.dismiss();
                    CousultActivity.this.finish();
                    Toast.makeText(mContext,"咨询资料已提交",Toast.LENGTH_SHORT).show();
                    break;
                case COMMIT_GUOQI:
                    Toast.makeText(mContext,"用户未登录，请登录",Toast.LENGTH_SHORT).show();
                    break;
                case COMMIT_CANCEL:
                    Toast.makeText(mContext,"提交失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cousult_layout);
		initView();
        initOther();
        getTypeData(mUrlPath);
		TotalListener();
	}

    /**
     * 初始化控件
     */
    private void initView() {
        //底部三个跳转按钮
        findViewById(R.id.cousult_explain_tv).setOnClickListener(this);
        findViewById(R.id.cousult_mying_tv).setOnClickListener(this);
        findViewById(R.id.cousult_respong_tv).setOnClickListener(this);

        //中间两个 Button 按钮
        findViewById(R.id.cousult_ok_btn).setOnClickListener(this);
        findViewById(R.id.cousult_cacel_btn).setOnClickListener(this);

        //三个显示的界面
        cousult_explain_dsc = (DorpbackScrollView) findViewById(R.id.cousult_explain_dsc);
        cousult_mying_dsc = (DorpbackScrollView) findViewById(R.id.cousult_mying_dsc);
        cousult_respond_dsc = (DorpbackScrollView) findViewById(R.id.cousult_respond_dsc);

        //其它控件初始化
        cousult_titleview = (CiistTitleView) findViewById(R.id.cousult_titleview);
        cousult_explain_txt = (TextView) findViewById(R.id.cousult_explain_txt);
        cousult_respond_lv = (ListView) findViewById(R.id.cousult_respond_lv);
        cousult_department_tv = (TextView) findViewById(R.id.cousult_department_tv);
        cousult_theme_edt = (EditText) findViewById(R.id.cousult_theme_edt);
        cousult_content_edt = (EditText) findViewById(R.id.cousult_content_edt);

        resetInterface();
        //设置默认显示咨询说明界面
        cousult_explain_dsc.setVisibility(View.VISIBLE);
        //部门选择监听
        cousult_department_tv.setOnClickListener(this);
    }

    /**
     * 初始化其他
     */
    private void initOther() {
        mContext = getApplicationContext();
        mNetState = Hutils.getNetState(mContext);
        sharedPreferenceHelper = SharedPreferenceHelper.getInstance(this);
        d = new ProgressDialog(this);
    }


    /**
     * 所有控件的见监听时间
     */
	private void TotalListener() {
		cousult_titleview.setOnLiftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CousultActivity.this.finish();
            }
        });


	}




    /**
	 * onClick 点击时间
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cousult_explain_tv://跳转说明界面
			resetInterface();
			cousult_explain_dsc.setVisibility(View.VISIBLE);
			break;
		case R.id.cousult_mying_tv://跳转我要咨询界面
			resetInterface();
			cousult_mying_dsc.setVisibility(View.VISIBLE);
			break;
		case R.id.cousult_respong_tv://跳转咨询回复界面
            new CheckPassportThread().start();
			resetInterface();
			cousult_respond_dsc.setVisibility(View.VISIBLE);
			break;
		case R.id.cousult_department_tv://打开部门选择Dialog界面
			CustomDialog dialog = new CustomDialog(CousultActivity.this,new CustomDialog.OnCustomDialogListener() {
				@Override
				public void back(String department, String nextCode) {
					cousult_department_tv.setText(department);
				}
			});
			dialog.setTitle("选择咨询部门");
            dialog.setUrlPath("3EA6562A-3ADE-4600-8C66-66D168CD8380");
            dialog.show();
			break;
        case R.id.cousult_ok_btn: // 点击确认按钮上传数据的功能
            if (cousult_theme_edt.length() != 0 && cousult_content_edt.length() != 0) {
                d.setIndeterminate(true);
                d.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                d.setMessage("正在提交");
                d.show();
                new CousultThread().start();
            }else{
                Toast.makeText(mContext,"填写资料有错误",Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.cousult_cacel_btn:// 点击重写按钮清空所有填写控件里面的数据
            cousult_department_tv.setText("");
            cousult_theme_edt.setText("");
            cousult_content_edt.setText("");
            break;
		}
	}
	
	/**
	 * 重置界面
	 */
	private void resetInterface(){
		cousult_explain_dsc.setVisibility(View.INVISIBLE);
        cousult_respond_dsc.setVisibility(View.INVISIBLE);
		cousult_mying_dsc.setVisibility(View.INVISIBLE);
	}

    /**
     * 得到类型数据
     */
    private void getTypeData(String url) {
        if (mNetState) {
            AsyncGetData(url);
        }else{
            setNoNetStyle("请检查网络状态");
        }
    }


    /**
     * 设置没有网络显示的样子
     * @param content
     */
    private void setNoNetStyle(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }


    /**
     * 异步取数据
     */
    private void AsyncGetData(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mStr = Hutils.fromNetgetData(url);
                handler.sendEmptyMessage(SUCCESS);
            }
        }).start();
    }


    /**
     * 解析获取到的数据
     */
    private void parseData() {
        if (mData == null) {
            mData = new ArrayList<Hutils.Ciist_entity>();
        }
        if (mStr == null) {
            getTypeData(mUrlPath);
            return;
        }
        mData.addAll(Hutils.parseJSONData(mStr, null));
        setData();
    }


    /**
     * 设置数据
     */
    private void setData() {
        if (isRespond){
            //设置回复列表
            isRespond = false;
        }else{
            cousult_explain_txt.setText(mData.get(0).Title);
        }
    }


    /**
     * 提交咨询资料线程
     */
    private class CousultThread extends Thread{
        String resultStr = ""; //上传是否成功返回的结果
        @Override
        public void run() {
            HttpClient httpClient = null;
            String _url = ServerInfo.ServerBKRoot + "/gov/consultation";  //上传地址
            HttpPost _post = new HttpPost(_url);
            _post.setHeader("Accept", APPLICATION_JSON);
            _post.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Acc_tel","null");
                jsonObject.put("Duties", ",");
                jsonObject.put("identify",getPasspotIdentif(sharedPreferenceHelper.getPassport("_passport")));
                jsonObject.put("selfids","A2F1328C-0EDD-409F-9686-1A7A91943CE5");
                jsonObject.put("user_zh", "");
                Log.e("test", "---构造passport" + jsonObject.toString());

                JSONObject sendObj = new JSONObject();
                sendObj.put("_passport",jsonObject);
                sendObj.put("_skey","ciistkey");
                sendObj.put("_title", cousult_theme_edt.getText().toString());
                sendObj.put("_detail", cousult_content_edt.getText().toString());
                sendObj.put("_depids", cousult_department_tv.getText().toString());
                sendObj.put("_depname", getPasspotName(sharedPreferenceHelper.getPassport("_passport")));
                Log.e("test", "---- 提交的数据----- " + sendObj.toString());

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

            Log.e("test", "-咨询提交返回结果-result---" + resultStr + "请求结束");
            if (resultStr.equals("1")){
                handler.sendEmptyMessage(CONMMIT_SUCCESS);
            }else if(resultStr.equals("-8")){
                handler.sendEmptyMessage(COMMIT_GUOQI);
            }else{
                handler.sendEmptyMessage(COMMIT_CANCEL);
            }


        }
    }

    /**
     * 检测用户授权信息
     */
    private class CheckPassportThread extends Thread{
        @Override
        public void run() {
            String resultStr = "";
            String _url = ServerInfo.ServerBKRoot+"gov/GetConsultationAboutMe/ciistKey/"
                    + getPasspotIdentif(sharedPreferenceHelper.getPassport("_passport"))
                    + "/1/10";

            Log.e("test", "-----url------" + _url);

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    resultStr = EntityUtils.toString(entity);
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
            Log.e("test",resultStr + "------------");
        }
    }

    /**
     * jie xi identify
     * @param s
     * @return
     */
    private String getPasspotIdentif(String s) {
        String identify = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            identify = jsonObject.getString("identify");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return identify;
    }
    /**
     * jie xi identify
     * @param s
     * @return
     */
    private String getPasspotName(String s) {
        String identify = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            identify = jsonObject.getString("user_zh");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return identify;
    }


}
