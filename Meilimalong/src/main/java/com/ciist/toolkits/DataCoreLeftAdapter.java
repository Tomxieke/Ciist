package com.ciist.toolkits;

import java.util.List;

import com.ciist.app.R;
import com.hw.ciist.util.Hutils.Ciist_entity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DataCoreLeftAdapter extends BaseAdapter {

	int selectedPos=-1;

	private Context mContext;
	private List<Ciist_entity> mData;
	
	public DataCoreLeftAdapter(Context context,List<Ciist_entity> data) {
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


	/**
	 * 用于获得位置
	 */
	public void setSelectedPosition(int pos) {
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPos;
	}




	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.datacore_left_item, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		if (selectedPos == position) {
			holder.tv.setBackgroundResource(R.color.datamalong);
		} else {
			holder.tv.setBackgroundResource(R.color.datamalongxianshi);
		}

		String title = mData.get(position).Title;
		holder.tv.setText(title);
		return convertView;
	}
	
	class Holder{
		TextView tv;
		public Holder(View v){
			tv = (TextView) v.findViewById(R.id.data_core_left_item_tv);
		}
	}

}
