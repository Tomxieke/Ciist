package com.ciist.toolkits;

import java.util.ArrayList; 
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ciist.app.R;
import com.hw.ciist.util.Hutils.Ciist_entity;

public class AppointmentAdapter extends BaseAdapter {
	
	private ArrayList<Ciist_entity> mData = null;
	
	private Context mContext = null;
	
	public AppointmentAdapter(Context context, ArrayList<Ciist_entity> data){
		this.mData = new ArrayList<Ciist_entity>();
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

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_appointment_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.appointment_departmentname_item.setText(mData.get(position).Title);
		return convertView;
	}
	
	class ViewHolder{
		TextView appointment_departmentname_item;
		public ViewHolder(View v){
			appointment_departmentname_item = (TextView) v.findViewById(R.id.appointment_departmentname_item);
		}
	}

}
