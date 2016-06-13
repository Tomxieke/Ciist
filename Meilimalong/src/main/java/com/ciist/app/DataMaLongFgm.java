package com.ciist.app;

import java.util.ArrayList;

import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.DataMlAdapter;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
@SuppressWarnings("unused")
public class DataMaLongFgm extends Fragment implements XListView.IXListViewListener {

	private static final int DTATSUCCESS = 100;//数据获取成功
	private static final int NOTDATASUCCESS = 200;//获取成功但没有数据
	private static final int NODATAOVER = 300;//数据结束

	private static final String dataCode = "7A0822AB-0E9D-4A1C-AA0F-7E292F140A5F/";//数据马龙 Key
	private static final String num = "/20";//获取的个数
	private int index = 1;//
	private boolean currentNetState = false;//网络状态

	private ArrayList<Ciist_entity> data = new ArrayList<Ciist_entity>();//解析数据的集合
	private DataMlAdapter adapter = null;//gridView适配器

	private LinearLayout dataMaLongShowChart;
	private Button dataMaLongLine;
	private Button dataMaLongRect;
	private Button dataMaLongPie;
	private XListView dataMaLongListView;
	private ProgressBar dataMaLongListViewPb;
	private EditText data_ml_search_edt;
	private ImageView data_ml_search_imv;

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
//		setContentView(Hutils.getIdByName(this, "layout", "data_malong"));
//		initView();//初始化
//		loadData();//加载数据
//		TotalListener();//所有的监听
//	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.data_malong,null);
		initView(view);//初始化
		loadData();//加载数据
		TotalListener();//所有的监听
		return view;
	}

	/**
	 * 所有控件的监听事件
	 */
	private void TotalListener() {
		//跳转
		dataMaLongListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg2--;
				Intent intent = new Intent(getActivity(), DataMaLongAct.class);
				String Code = data.get(arg2).Remark5;
				String Title = data.get(arg2).Title;
				intent.putExtra("userCode", Code);
				intent.putExtra("userTitle", Title);
				getActivity().startActivity(intent);
			}
		});
		//搜索
		data_ml_search_imv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchTxt = data_ml_search_edt.getText()+"";
				getSearchTxt(searchTxt);
				Intent intent = new Intent(getActivity(), DataMaLongAct.class);
				intent.putExtra("searchCode", data_ml_search_edt.getText()+"");
				getActivity().startActivity(intent);
			}
		});
	}

	/**
	 * 判断索搜字符串是否正确
	 */
	private void getSearchTxt(String str){
		if (str.contains(" ")){
			Toast.makeText(getActivity(),"输入内容格式错误，请重新输入",Toast.LENGTH_SHORT).show();
			return;
		}
		if (str.equals("")){
			Toast.makeText(getActivity(),"还没有输入内容，请继续输入",Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		currentNetState = Hutils.getNetState(getActivity());
		if (currentNetState) {
			new Thread(new Runnable() {
				public void run() {
					String urlPath = ServerInfo.ServerBKRoot + "/info/getDataASC/ciistkey/" + dataCode + index + num;;
					String str = Hutils.fromNetgetData(urlPath, true);
					if (Hutils.parseJSONData(str, null).size() != 0) {
						data.addAll(Hutils.parseJSONData(str, null));
						handler.sendEmptyMessage(DTATSUCCESS);
						if (Hutils.parseJSONData(str, null).size() < 20) {
							handler.sendEmptyMessage(NODATAOVER);
						}
					}else{
						handler.sendEmptyMessage(NOTDATASUCCESS);
					}
				}
			}).start();
		} else {
			Toast.makeText(getActivity(), "请打开网络", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView(View v) {
		dataMaLongShowChart = (LinearLayout) v.findViewById(R.id.dataMaLongShowChart);
		dataMaLongLine = (Button) v.findViewById(R.id.dataMaLongLine);
		dataMaLongRect = (Button) v.findViewById(R.id.dataMaLongRect);
		dataMaLongPie = (Button) v.findViewById(R.id.dataMaLongPie);
		dataMaLongListView = (XListView) v.findViewById(R.id.dataMaLongListView);
		dataMaLongListViewPb = (ProgressBar) v.findViewById(R.id.dataMaLongListViewPb);
		data_ml_search_edt = (EditText) v.findViewById(R.id.data_ml_search_edt);
		data_ml_search_imv = (ImageView) v.findViewById(R.id.data_ml_search_imv);

		dataMaLongShowChart.setVisibility(View.GONE);
		dataMaLongListView.setPullLoadEnable(true);
		dataMaLongListView.setXListViewListener(this);
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore() {
		index ++;
		loadData();
		onLoad();
	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		index = 1;
		data.clear();
		loadData();
		dataMaLongListView.setPullLoadEnable(true);
		onLoad();
	}
	
	/**
	 * 刷新完成的逻辑操作
	 */
	private void onLoad() {
		dataMaLongListView.stopRefresh();
		dataMaLongListView.stopLoadMore();
		dataMaLongListView.setRefreshTime("刚刚");
	}

	/**
	 * 异步处理
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DTATSUCCESS:
					if (adapter == null) {
						adapter = new DataMlAdapter(data, getActivity());
						dataMaLongListView.setAdapter(adapter);
						dataMaLongListViewPb.setVisibility(View.INVISIBLE);
					} else {
						adapter.notifyDataSetChanged();
					}
					break;
				case NOTDATASUCCESS:
					adapter.notifyDataSetChanged();
					dataMaLongListView.setPullLoadEnable(false);
					break;
				case NODATAOVER:
					adapter.notifyDataSetChanged();
					dataMaLongListView.setPullLoadEnable(false);
					break;
			}
		}
	};
}
