package com.ciist.app;

import java.util.ArrayList;

import com.ciist.customview.xlistview.XListView;
import com.ciist.entities.ServerInfo;
import com.ciist.toolkits.DataMlAdapter;
import com.ciist.util.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;
import com.hw.ciist.view.HListView;
import com.hw.ciist.view.HListView.IXListViewListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DataMaLongAct extends FragmentActivity implements XListView.IXListViewListener{

	private static final int NODATAOVER = 300;//数据加载完毕
	private static final int DTATSUCCESS = 100;//数据获取成功
	private static final int NOTDATASUCCESS = 200;//获取成功但没有数据

	private static final String num = "/10";//获取的个数
	private int index = 1;//页数
	private String searchCode = null;//搜索内容
	private String dataCode = "";//数据马龙 Key
	private String dataTitle = "";//每一页的标题
	private boolean currentNetState = false;//网络状态
	private boolean isSHOW = false;//chart展示图显示状态

	private ArrayList<Ciist_entity> data = new ArrayList<Ciist_entity>();//解析数据的集合
	private DataMlAdapter adapter = null;//gridView的适配器

	private FrameLayout dataMaLongTitleBarFl;
	private LinearLayout dataMaLongShowChart;
	private LineChart dataMaLongLineChart;
	private BarChart dataMaLongRectChart;
	private PieChart dataMaLongPieChart;
	private Button dataMaLongLine;
	private Button dataMaLongRect;
	private Button dataMaLongPie;
	private Button dataMaLongSetShowChart;
	private TextView dataMaLongTitleBar;
	private XListView dataMaLongListView;
	private ProgressBar dataMaLongListViewPb;
	private LinearLayout data_ml_search;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
		setContentView(Hutils.getIdByName(this, "layout", "data_malong"));
		initView();//初始化
		loadData();//加载数据
		TotalListener();//所有监听事件
	}
	
	/**
	 * 所有控件的监听事件
	 */
	private void TotalListener() {
		//列表的监听事件
		dataMaLongListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg2--;//由于HListView添加了footer页改变item的position
				String str = data.get(arg2).Remark5;
				if (str.length() > 5) {
					Intent intent = new Intent(DataMaLongAct.this,DataMaLongAct.class);
					String Code = data.get(arg2).Remark5;
					String Title = data.get(arg2).Title; 
					intent.putExtra("userCode", Code);
					intent.putExtra("userTitle", Title);
					startActivity(intent);
				}else{
					Toast.makeText(DataMaLongAct.this, "此项目录还有没有录入数据哦！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//显示按钮的监听事件
		dataMaLongSetShowChart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSHOW) {
					dataMaLongShowChart.setVisibility(View.GONE);
					dataMaLongSetShowChart.setText("显示");
					isSHOW = false;
				}else{
					dataMaLongShowChart.setVisibility(View.VISIBLE);
					dataMaLongSetShowChart.setText("隐藏");
					isSHOW = true;
				}
			}
		});
		dataMaLongLine.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataMaLongLineChart.setVisibility(View.VISIBLE);
				dataMaLongRectChart.setVisibility(View.GONE);
				dataMaLongPieChart.setVisibility(View.GONE);
				
				dataMaLongLine.setBackgroundResource(R.drawable.selector_bg_btn_dataml_p);
				dataMaLongRect.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
				dataMaLongPie.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
			}
		});
		dataMaLongRect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataMaLongLineChart.setVisibility(View.GONE);
				dataMaLongRectChart.setVisibility(View.VISIBLE);
				dataMaLongPieChart.setVisibility(View.GONE);
				
				dataMaLongLine.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
				dataMaLongRect.setBackgroundResource(R.drawable.selector_bg_btn_dataml_p);
				dataMaLongPie.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
			}
		});
		dataMaLongPie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dataMaLongLineChart.setVisibility(View.GONE);
				dataMaLongRectChart.setVisibility(View.GONE);
				dataMaLongPieChart.setVisibility(View.VISIBLE);
				
				dataMaLongLine.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
				dataMaLongRect.setBackgroundResource(R.drawable.selector_bg_btn_dataml_n);
				dataMaLongPie.setBackgroundResource(R.drawable.selector_bg_btn_dataml_p);
			}
		});
	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		currentNetState = Hutils.getNetState(this);
		if (currentNetState) {
			new Thread(new Runnable() {
				public void run() {
					// 组合访问地址
					String urlPath = "";// = ServerInfo.GetInfoPre + dataCode + index + num;
					if (searchCode == null){//正常入口
						urlPath = ServerInfo.ServerBKRoot + "/info/getDataASC/ciistkey/" + dataCode + index + num;
					}else{
						urlPath = ServerInfo.DataSearchPre + searchCode + "/" + index + num;//搜索入口
					}
					// 把获取到的数据添加到集合中
					String str = Hutils.fromNetgetData(urlPath, true);
					System.out.println("打印数据" + str);
					if (Hutils.parseJSONData(str, null).size() != 0) {//获取的数据成功的话字符串的长度会大于80
						data.addAll(Hutils.parseJSONData(str, null));
						handler.sendEmptyMessage(DTATSUCCESS);
						if (Hutils.parseJSONData(str, null).size() < 10) {
							handler.sendEmptyMessage(NODATAOVER);
						}
					}else{
						handler.sendEmptyMessage(NOTDATASUCCESS);
					}
				}
			}).start();
		} else {
			Toast.makeText(this, "你还没有开启网络哦", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		dataMaLongShowChart = (LinearLayout) findViewById(Hutils.getIdByName(this, "id", "dataMaLongShowChart"));
		dataMaLongLine = (Button) findViewById(Hutils.getIdByName(this, "id", "dataMaLongLine"));
		dataMaLongRect = (Button) findViewById(Hutils.getIdByName(this, "id", "dataMaLongRect"));
		dataMaLongPie = (Button) findViewById(Hutils.getIdByName(this, "id", "dataMaLongPie"));
		dataMaLongSetShowChart = (Button) findViewById(Hutils.getIdByName(this, "id", "dataMaLongSetShowChart"));
		dataMaLongListView = (XListView) findViewById(Hutils.getIdByName(this, "id", "dataMaLongListView"));
		dataMaLongListViewPb = (ProgressBar) findViewById(Hutils.getIdByName(this, "id", "dataMaLongListViewPb"));
		dataMaLongLineChart = (LineChart) findViewById(Hutils.getIdByName(this, "id", "dataMaLongLineChart"));
		dataMaLongRectChart = (BarChart) findViewById(Hutils.getIdByName(this, "id", "dataMaLongRectChart"));
		dataMaLongPieChart = (PieChart) findViewById(Hutils.getIdByName(this, "id", "dataMaLongPieChart"));
		dataMaLongTitleBar = (TextView) findViewById(Hutils.getIdByName(this, "id", "dataMaLongTitleBar"));
		dataMaLongTitleBarFl = (FrameLayout) findViewById(Hutils.getIdByName(this, "id", "dataMaLongTitleBarFl"));
		data_ml_search = (LinearLayout) findViewById(R.id.data_ml_search);
		//接收传递值
		dataCode = getIntent().getStringExtra("userCode") + "/";
		dataTitle = getIntent().getStringExtra("userTitle");
		searchCode = getIntent().getStringExtra("searchCode");
		//设置标题内容，显示标题
		if (searchCode == null)
			dataMaLongTitleBar.setText(dataTitle);
		else
			dataMaLongTitleBar.setText(searchCode);

		dataMaLongTitleBarFl.setVisibility(View.VISIBLE);
		data_ml_search.setVisibility(View.GONE);
		
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
	 * 刷新完成停止所以加载动作
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
					//判断适配器是否为空
					if (adapter == null) {
						adapter = new DataMlAdapter(data, DataMaLongAct.this);
						dataMaLongListView.setAdapter(adapter);
						dataMaLongListViewPb.setVisibility(View.INVISIBLE);
					} else {
						adapter.notifyDataSetChanged();
					}

					//判断当前的现实状态
					if (isSHOW) {
						dataMaLongShowChart.setVisibility(View.VISIBLE);
					}else{
						dataMaLongShowChart.setVisibility(View.GONE);
					}

					//判断item中是否有数据，有则设置为默认显示chart展示图
					// if (ChartUtils.StringToFloat(data.get(0).Author) > 0) {
					// dataMaLongShowChart.setVisibility(View.VISIBLE);
					// dataMaLongSetShowChart.setText("隐藏");
					// isSHOW = true;
					// }else{
					// dataMaLongShowChart.setVisibility(View.GONE);
					// dataMaLongSetShowChart.setText("显示");
					// isSHOW = false;
					// }

					//设置折线图
					LineData lineData = ChartUtils.makeLineData(data.size(), data,"数据马龙");
					ChartUtils.setChartStyle(dataMaLongLineChart, lineData, Color.TRANSPARENT);
					//设置柱形图
					BarData barData = ChartUtils.getBarData(data.size(), data, "数据马龙");
					ChartUtils.setBarChart(dataMaLongRectChart, barData);
					//设置饼状图
					PieData pieData = ChartUtils.getPieData(data.size(), data);
					ChartUtils.setPieChart(dataMaLongPieChart, pieData, "数据马龙");
					break;
				case NOTDATASUCCESS:
					// adapter.notifyDataSetChanged();
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
