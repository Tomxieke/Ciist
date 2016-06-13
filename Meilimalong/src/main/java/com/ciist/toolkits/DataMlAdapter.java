package com.ciist.toolkits;

import java.util.ArrayList;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataMlAdapter extends BaseAdapter {

	private ArrayList<Ciist_entity> data;
	private Context context;

	public DataMlAdapter(ArrayList<Ciist_entity> data, Context context) {
		super();
		this.data = new ArrayList<Ciist_entity>();
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(Hutils.getIdByName(context, "layout", "data_malong_item"), null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (data.get(position).Remark5.length() > 3) {
			holder.dataMaLongItemHave.setVisibility(View.VISIBLE);
			holder.dataMaLongItemNoHave.setVisibility(View.GONE);
		} else {
			holder.dataMaLongItemHave.setVisibility(View.GONE);
			holder.dataMaLongItemNoHave.setVisibility(View.VISIBLE);
		}
		holder.dataMaLongItemTitle.setText(data.get(position).Title);
		String num = data.get(position).Author;
		String unit = data.get(position).Sourse;
		if (StringToFloat(num) > 0) {
			holder.dataMaLongItemNum.setText(num);
			holder.dataMaLongItemUnit.setText(unit);
		} else {
			holder.dataMaLongItemNum.setText(" ");
			holder.dataMaLongItemUnit.setText(" ");
		}
		holder.dataMaLongItemll.getBackground().setAlpha(90);
		return convertView;
	}

	/**
	 * String 转换 float
	 * 
	 * @param str
	 * @return
	 */
	private float StringToFloat(String str) {
		try {
			float f = Float.parseFloat(str);
			return f;
		} catch (Exception e) {
			System.out.println(e + "");
		}
		return 0;
	}

	class ViewHolder {

		ImageView dataMaLongItemHave;
		ImageView dataMaLongItemNoHave;
		TextView dataMaLongItemTitle;
		TextView dataMaLongItemNum;
		TextView dataMaLongItemUnit;
		LinearLayout dataMaLongItemll;

		public ViewHolder(View v) {
			dataMaLongItemHave = (ImageView) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemHave"));
			dataMaLongItemNoHave = (ImageView) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemNoHave"));
			dataMaLongItemTitle = (TextView) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemTitle"));
			dataMaLongItemNum = (TextView) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemNum"));
			dataMaLongItemUnit = (TextView) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemUnit"));
			dataMaLongItemll = (LinearLayout) v.findViewById(Hutils.getIdByName(context, "id", "dataMaLongItemll"));
		}
	}

}
