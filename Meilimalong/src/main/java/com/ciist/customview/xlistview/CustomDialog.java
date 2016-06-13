package com.ciist.customview.xlistview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ciist.app.AppointmentActivity;
import com.ciist.app.R;
import com.ciist.toolkits.AppointmentAdapter;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;

import java.util.ArrayList;

@SuppressLint("HandlerLeak") public class CustomDialog extends Dialog {
	
	
	public static final int SUCCESS = 0x45;
	public static final int CANCEL = 0x46;	
	public static final int NODATA = 0x47;
	
	public static final String GetInfoPre = "http://211.149.212.154:2015/apps/info/getdata/ciistkey/";
	
	public String subjectCode = null;
	
	private Context mContext;
	private boolean mNetState;
	private String mStr = null;
	private String mStr2 = null;
	private int index = 1;
	private int num = 200;
	
	private String mUrlPath = null;
	private String mUrlPath2 = null;
	
	private OnCustomDialogListener customDialogListener;
	private AppointmentAdapter mAdapter = null;
	private ArrayList<Ciist_entity> mData = null;
	
	private ListView appointment_department_lv;
	private ProgressBar appointment_pb;
	
	public CustomDialog(Context context, OnCustomDialogListener customDialogListener) {
		super(context);
		this.mContext = context;
		this.customDialogListener = customDialogListener;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				parseData();
				fillListView();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_appointment);
		initView();
		getOther();
		getTypeData();
		totalListener();
	}
	
	/**
	 * chu shi hua kong jian
	 */
	private void initView() {
		appointment_department_lv = (ListView) findViewById(R.id.appointment_department_lv);
		appointment_pb = (ProgressBar) findViewById(R.id.appointment_pb);
	}
	
	/**
	 * de dao qi ta de zhuang tai
	 */
	private void getOther() {
		mNetState = Hutils.getNetState(mContext);
	}
	
	/**
	 * de dao bu men shu ju 
	 */
	private void getTypeData() {
		if (mNetState) {
			AsyncGetData();
		}else{
			setNoNetStyle("请检查网络状态");
		}
	}

	/**
	 * yi bu qu shu ju
	 */
	private void AsyncGetData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mStr = Hutils.fromNetgetData(mUrlPath);
				if (mUrlPath2 != null) {
					mStr2 = Hutils.fromNetgetData(mUrlPath2);
				}
				handler.sendEmptyMessage(SUCCESS);
			}
		}).start();
	}
	
	/**
	 * she zi mei you wang nuo xian shi de yang zi
	 * @param content
	 */
	private void setNoNetStyle(String content) {
		Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * jie xi huo qu dao de shu ju
	 */
	private void parseData() {
		if (mData == null) {
			mData = new ArrayList<Ciist_entity>();
		}
		if (mStr == null) {
			getTypeData();
			return;
		}
		mData.addAll(Hutils.parseJSONData(mStr, null));
		if (mStr2 != null) {
			mData.addAll(Hutils.parseJSONData(mStr2, null));
		}
	}
	
	
	/**
	 * tian chong listView
	 */
	private void fillListView() {
		if (mAdapter == null) {
			mAdapter = new AppointmentAdapter(mContext, mData);
			appointment_department_lv.setAdapter(mAdapter);
			appointment_pb.setVisibility(View.INVISIBLE);
		}else{
			mAdapter.notifyDataSetChanged();
		}
	}
	
	
	/**
	 * zi ding yi hui diao shi jian 
	 * , yong yu dialog de dian ji shi jian
	 * 
	 * @author Administrator
	 *
	 */
	public interface OnCustomDialogListener{
		public void back(String department, String nextCode);
	}
	
	
	/**
	 * suo you kong jian de dian shi jian 
	 */
	private void totalListener() {
		appointment_department_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppointmentActivity app = new AppointmentActivity();
				app.setNextCode(mData.get(position).Remark5);
				Log.e("TAG",mData.get(position).Remark5);
				customDialogListener.back(mData.get(position).Title,mData.get(position).Remark5);
				CustomDialog.this.dismiss();
			}
		});
	}

	
	/**
	 * she zi fang wen lu jin xu yao shi yong de keCode
	 * 
	 * @param subjectCode
	 */
	public void setUrlPath(String subjectCode){
		mUrlPath = GetInfoPre + subjectCode + "/" + index + "/" + num;
	}
	
	
	/**
	 * she zi fang wen lu jin xu yao shi yong de keCode
	 * 
	 * @param subjectCode
	 */
	public void setUrlPath2(String subjectCode){
		mUrlPath2 = GetInfoPre + subjectCode + "/" + index + "/" + num;
	}
	
	
	/**
	 * gei wai bu ti gong jie kou ,
	 * rang wai bu de dao nei bu huo qu de shu ju
	 */
	public ArrayList<Ciist_entity> getData(){
		return mData;
	}
	
	
}
