package com.ciist.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ciist.customview.xlistview.CiistTitleView;
import com.ciist.customview.xlistview.CustomDialog;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;


@SuppressLint("HandlerLeak") public class AppointmentActivity extends Activity {
	
	public static final int SUCCESS = 0x45;
	public static final int CANCEL = 0x46;	
	public static final int NODATA = 0x47;
	
	private TextView appointment_department_tv;
	private TextView appointment_type_tv;
	
	private EditText appointment_name_edt;
	private EditText appointment_content_edt;
	private EditText appointment_phonenumber_edt;
	private EditText appointment_time_edt;

	private CiistTitleView appointment_titleview;

	private Button appointment_confirm_btn;
	private Button appointment_cancel_btn;
	private CheckBox appointment_confirm_cbx;
	private TextView appointment_agreement_tv;

	private Context mContext;
	private String mNextCode;
	private boolean mNetState;
	
	private String mUrlPath = null;
	private String mStr = null;
	
	private ArrayList<Ciist_entity> mData = null;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				parseData();
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_appointment);
        initView();
        initOther();
        totalListener();
    }

    
	/**
     * chu shi hua kong jian
     */
	private void initView() {
		appointment_name_edt = (EditText) findViewById(R.id.appointment_name_edt);
		appointment_department_tv =  (TextView) findViewById(R.id.appointment_department_tv);
		appointment_type_tv = (TextView) findViewById(R.id.appointment_type_tv);
		appointment_content_edt = (EditText) findViewById(R.id.appointment_content_edt);
		appointment_phonenumber_edt = (EditText) findViewById(R.id.appointment_phonenumber_edt);
		appointment_time_edt = (EditText) findViewById(R.id.appointment_time_edt);
		appointment_confirm_btn = (Button) findViewById(R.id.appointment_confirm_btn);
		appointment_cancel_btn = (Button) findViewById(R.id.appointment_cancel_btn);
		appointment_confirm_cbx = (CheckBox) findViewById(R.id.appointment_confirm_cbx);
		appointment_agreement_tv = (TextView) findViewById(R.id.appointment_agreement_tv);
		appointment_titleview = (CiistTitleView) findViewById(R.id.appointment_titleview);
	}
	
	
	/**
     * shu shi hua qi ta
     */
	private void initOther() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mContext = AppointmentActivity.this;
		mNetState = Hutils.getNetState(mContext);
		//gei yu yue shi jian she zi shang dang qian de ri qi
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String datenow = sdf.format(new java.util.Date());
		appointment_time_edt.setText(datenow);
	}
	
	
	/**
	 * suo you kong jian de jian ting
	 */
	private void totalListener() { 
		appointment_department_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDialog dialog = new CustomDialog(mContext,new CustomDialog.OnCustomDialogListener() {
					@Override
					public void back(String department, String nextCode) {
						appointment_department_tv.setText(department);
						setNextCode(nextCode);
						Log.i("TAG", nextCode + ".....................");
					}
				});
				dialog.setTitle("选择预约类型");
				dialog.setUrlPath("F49868E9-0FE3-42B9-9E4A-2947CE5E5CB9");
				dialog.setUrlPath2("0ACD0635-2490-4114-95D5-32DFE9C38B68");
				dialog.show();
			}
		});
		
		appointment_type_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDialog dialog = new CustomDialog(mContext,new CustomDialog.OnCustomDialogListener() {
					@Override
					public void back(String department, String nextCode) {
						appointment_type_tv.setText(department);
					}
				});
				dialog.setTitle("选择预约业务");
				dialog.setUrlPath(mNextCode);
				dialog.show();
			}
		});
		
		appointment_confirm_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (appointment_confirm_cbx.isChecked()) {
					if (appointment_name_edt.length()!=0
							&& appointment_department_tv.length() != 0
							&& appointment_type_tv.length() != 0
							&& appointment_content_edt.length() != 0
							&& appointment_phonenumber_edt.length() != 0
							&& appointment_time_edt.length() != 0){
						//zi liao tian xie wan zhi hou ti jiao yu yue shen qing de chaun shu ju chao zuo
						if (appointment_phonenumber_edt.length() == 11) {
							Toast.makeText(mContext, "已提交预约申请", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(mContext, "输入11位电话号码", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(mContext, "内容不能为空", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(mContext, "您还没有签订预约协议哦", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		appointment_cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppointmentActivity.this.finish();
			}
		});
		
		appointment_agreement_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getTypeData();
			}
		});

		appointment_titleview.setOnLiftClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppointmentActivity.this.finish();
			}
		});

		appointment_time_edt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar c = Calendar.getInstance();
				Dialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
						appointment_time_edt.setText(i + "-" + (i1 + 1) + "-" + i2);
					}
				},
					c.get(Calendar.YEAR),
					c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH)
			  );
				dialog.show();
			}
		});
	}
	
	/**
	 * she zi xia yi jie mian bian ma
	 * @param nextCode
	 */
	public void setNextCode(String nextCode){
		this.mNextCode = nextCode;
	}

	/**
	 * de dao lei xing shu ju
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
	}

    
}
