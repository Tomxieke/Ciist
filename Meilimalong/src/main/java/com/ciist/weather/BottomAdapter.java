package com.ciist.weather;

import java.util.List;

import com.ciist.app.R;
import com.ciist.beans.WeatherResult;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Rx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BottomAdapter extends BaseAdapter {

	public Context mContext;
	public List<WeatherResult> lists;
	private boolean Lodaweather = false;
	public String status1 = "晴";
	public String status2 = "晴转";
	public String status3 = "雨";
	public String status4 = "雷电";
	public String status5 = "阴";
	public String status6 = "雪";
	public String status7 = "雨夹雪";

	public BottomAdapter(Context ct, List<WeatherResult> fb) {
		this.mContext = ct;
		this.lists = fb;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		viewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.ciist_zhn_werther_list_item, null);
			holder = new viewHolder();

			holder.bodyTv = (TextView) convertView.findViewById(R.id.Listdata);
			holder.bottomTv = (TextView) convertView.findViewById(R.id.Listsuorce);
			holder.listfutrue = (TextView) convertView.findViewById(R.id.Listfutrue);
			holder.listw = (TextView) convertView.findViewById(R.id.Listweather);
			holder.titleImg = (ImageView) convertView.findViewById(R.id.listImage);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}

		holder.bodyTv.setText(lists.get(position).getWeek());
		holder.bottomTv.setText(lists.get(position).getDays());
		holder.listfutrue.setText(lists.get(position).getTemperature());
		holder.listw.setText(lists.get(position).getWeather());
		Lodaweather = false;
//		String weather = lists.get(position).getWeather();
//		if (weather.contains(status1)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_2);
//		} else if (weather.contains(status2)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_1);
//		} else if (weather.contains(status3)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_3);
//		} else if (weather.contains(status4)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_5);
//		} else if (weather.contains(status5)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_6);
//		} else if (weather.contains(status6)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_4);
//		} else if (weather.contains(status7)) {
//			holder.titleImg
//					.setBackgroundResource(R.drawable.ciist_home_icon_weather_4);
//		}

		return convertView;
	}

	public class viewHolder {
		ImageView titleImg;
		TextView bodyTv;
		TextView bottomTv;
		TextView listfutrue;
		TextView listw;
	}
}