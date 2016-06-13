package com.ciist.toolkits;

import java.util.List;

import com.ciist.app.R;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataCoreRightAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Context mContext;
	private List<Ciist_entity> mData;
	
	public DataCoreRightAdapter(Context context,List<Ciist_entity> data) {
		this.mContext = context;
		this.mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.datacore_right_item, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		holder.tv1.setText(mData.get(position).Title);
		if (Hutils.StringToFloat(mData.get(position).Author) == 0) {
			holder.tv2.setText("");
			holder.tv3.setText("");
		}else{
			holder.tv2.setText(Hutils.StringToFloat(mData.get(position).Author) + "");
			holder.tv3.setText(mData.get(position).Sourse);
		}
		return convertView;
	}
	
	class Holder{
		TextView tv1,tv2,tv3;
		public Holder(View v){
			tv1 = (TextView) v.findViewById(R.id.data_core_item_title);//内容
			tv2 = (TextView) v.findViewById(R.id.data_core_item_num);//数量
			tv3 = (TextView) v.findViewById(R.id.data_core_item_unit);//单位
		}
	}

}
