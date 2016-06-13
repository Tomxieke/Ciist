package com.ciist.weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import com.ciist.app.R;
import com.ciist.bean.ResultBeans2;
import com.ciist.beans.ResultBeans;
import com.ciist.beans.WeatherResult;
import com.ciist.util.NetUtil;
import com.ciist.util.NotificationDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class WeatherMainActivity extends Activity {

	private LinearLayout lin;
	private RelativeLayout relat;
	private boolean Lodaweather = false;
	private TextView num1, num2, city, data, nowTime, chuanyi, yundong, wuran,
			xiche, ziwaixian, pm25;
	private ProgressBar weather_pro;
	private ListView list;
	private Gson gson;
	private ResultBeans resultBeans;
	private ResultBeans2 resultbeans2;
	private final int SUCCESS = 0x01;
	private final int ISOK = 0x02;
	private BottomAdapter btomAdapter;
	private List<WeatherResult> lists_bottom;

	private LinearLayout hcrobody;

	RefreshableView refreshableView;

	private String cityName = "马龙县";
	/*
	 * 通过JSON解析获得数据显示到文本中
	 */
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == SUCCESS) {
				weather_pro.setVisibility(View.GONE);
				setText(num1, resultBeans.getResult().get(0).getTemperature()
						+ "°");
				setText(num2, resultBeans.getResult().get(0).getWeather()

				);
				setText(data, resultBeans.getResult().get(0).getDays());
				Lodaweather=false;
				relat.setVisibility(View.VISIBLE);
				lists_bottom.addAll(resultBeans.getResult());
				Lodaweather = false;
				btomAdapter.notifyDataSetChanged();

			} else if (msg.what == ISOK) {

				weather_pro.setVisibility(View.GONE);
				setText(chuanyi, resultbeans2.getResult().getDate().getLife()
						.getInfo().getChuanyi()[1]);
				setText(yundong, resultbeans2.getResult().getDate().getLife()
						.getInfo().getYundong()[1]);
				setText(wuran, resultbeans2.getResult().getDate().getLife()
						.getInfo().getWuran()[1]);
				setText(pm25, resultbeans2.getResult().getDate().getPm25()
						.getPm25().getQuality()
						+ "---"
						+ resultbeans2.getResult().getDate().getPm25()
						.getPm25().getDes());
				setText(xiche, resultbeans2.getResult().getDate().getLife()
						.getInfo().getXiche()[1]);
				setText(ziwaixian, resultbeans2.getResult().getDate().getLife()
						.getInfo().getZiwaixian()[1]);
				Lodaweather=false;
				hcrobody.setVisibility(View.VISIBLE);

			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ciist_zhn_weather_main);

		initView();
		NetUtil.hasNetWork(this);  //检查网络，没有网络弹出提示对话框


		// 下拉刷新
		refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 0);
		// 切换背景图
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		System.out.println("--------------" + hour);
		if (7 < hour && hour < 20) {
			lin.setBackgroundResource(R.drawable.ciist_weather_tupian_baitian_a);

		} else {
			lin.setBackgroundResource(R.drawable.ciist_weather_weather_bg);

		}
		list.getBackground().setAlpha(50);

	}

	public void initView() {

		ziwaixian = (TextView) findViewById(R.id.tuijie_ziwaixian);
		relat = (RelativeLayout) findViewById(R.id.wearher_relat);
		relat.setVisibility(View.GONE);
		weather_pro = (ProgressBar) findViewById(R.id.weather_pro);
		xiche = (TextView) findViewById(R.id.tuijie_xiche);
		pm25 = (TextView) findViewById(R.id.tuijie_pm);
		wuran = (TextView) findViewById(R.id.tuijie_wuran);
		yundong = (TextView) findViewById(R.id.tuijie_yundong);
		chuanyi = (TextView) findViewById(R.id.tuijie_chuanyi);
		num1 = (TextView) findViewById(R.id.weather_tv_num1);
		num2 = (TextView) findViewById(R.id.weather_tv_num2);
		city = (TextView) findViewById(R.id.weather_tv_city);
		data = (TextView) findViewById(R.id.weather_tv_data);
		nowTime = (TextView) findViewById(R.id.weather_tv_nowtime);
		lin = (LinearLayout) findViewById(R.id.linear_back);

		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);

		list = (ListView) findViewById(R.id.weather_listview);

		lists_bottom = new ArrayList<WeatherResult>();

		hcrobody = (LinearLayout) findViewById(R.id.hscro_body);

		hcrobody.getBackground().setAlpha(50);
		hcrobody.setVisibility(View.GONE);

		gson = new Gson();
		getJson(weatherUrl1(cityName), true);
		getJson(weatherUrl2(cityName), false);

		btomAdapter = new BottomAdapter(this, lists_bottom);

		list.setAdapter(btomAdapter);

	}

	// JSON解析
	@SuppressWarnings("unused")
	public void getJson(String url, final boolean type) {
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(1000 * 10);
		http.send(HttpMethod.POST, url, null, new RequestCallBack<String>() {

			public void onSuccess(ResponseInfo<String> info) {
				Log.d("LOG", "code:" + info.result);
				if (type) {
					resultBeans = gson.fromJson(info.result, ResultBeans.class);
					mHandler.sendEmptyMessage(SUCCESS);
				} else {
					resultbeans2 = gson.fromJson(info.result,
							ResultBeans2.class);
					mHandler.sendEmptyMessage(ISOK);

				}
			}

			public void onFailure(HttpException arg0, String arg1) {
				Log.d("LOG", "请求数据失败！！");

			}
		});
	}

	// 接口地址
	public String weatherUrl1(String city) {
		return "http://api.k780.com:88/?app=weather.future&weaid=101290405&&appkey=17417&sign=e203438d9686d936b908e0c8c3a8fc6e&format=json";
	}

	public String weatherUrl2(String city) {
		return "http://op.juhe.cn/onebox/weather/query?cityname=马龙&key=011cb6a7256041f80efad688919f10ed";
	}

	public void setText(TextView tv, String str) {
		tv.setText(str);
	}

	/**
	 * 获取assets文件下的资源文件
	 *
	 * @param context
	 * @param fileName
	 * @return drawable
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getAssertDrawable(Context context, String fileName) {
		try {
			InputStream is = context.getAssets().open(fileName);
			return new BitmapDrawable(BitmapFactory.decodeStream(is));
		} catch (IOException e) {
			Log.e("LOG_TAG", "Assert中" + fileName + "不存在");
		}
		return null;
	}

}

