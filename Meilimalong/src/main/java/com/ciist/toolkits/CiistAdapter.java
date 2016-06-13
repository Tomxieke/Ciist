package com.ciist.toolkits;

import java.text.SimpleDateFormat; 
import java.util.ArrayList;

import com.ciist.app.R;
import com.hw.ciist.util.Hutils.Ciist_entity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @category tong yong adaper
 * @category ciist
 * @author hw
 * 
 */
public class CiistAdapter extends BaseAdapter {

	private static final int STYLE_1 = 1;//小图
	private static final int STYLE_2 = 2;//大图
	private static final int STYLE_3 = 3;//通知
	private static final int STYLE_4 = 4;//

	private Context mContext = null;
	private ViewHolder holder = null;
	private ArrayList<Ciist_entity> mData = null;// shu ju

	public CiistAdapter(Context context,ArrayList<Ciist_entity> data) {
		this.mContext = context;
		this.mData = new ArrayList<Ciist_entity>();
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
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_ciistadapter, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		FillData(position,parent);
		resetStyle();
		String type = mData.get(position).Type;
		if (type.equals("smallpic")) {
			setStyle(STYLE_1);
		}else if (type.equals("infobigpicmode")){
			setStyle(STYLE_2);
		}else if (type.equals("noticesimplemode")){
			setStyle(STYLE_3);
		}else{
			setStyle(STYLE_1);
		}
		return convertView;
	}

	/**
	 * reset style,all style is invisibily
	 */
	private void resetStyle() {
		holder.ciistAdpStyle1.setVisibility(View.GONE);
		holder.ciistAdpStyle2.setVisibility(View.GONE);
		holder.ciistAdpStyle3.setVisibility(View.GONE);
		holder.ciistAdpStyle4.setVisibility(View.GONE);
	}

	/**
	 * set style,dang choose dang qian style, jiu xian shi chu dang qian de
	 * style
	 * 
	 * @param style
	 */
	private void setStyle(int style) {
		if (style == STYLE_1) {
			holder.ciistAdpStyle1.setVisibility(View.VISIBLE);
		} else if (style == STYLE_2) {
			holder.ciistAdpStyle2.setVisibility(View.VISIBLE);
		} else if (style == STYLE_3) {
			holder.ciistAdpStyle3.setVisibility(View.VISIBLE);
		} else if (style == STYLE_4) {
			holder.ciistAdpStyle4.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * fill data,ba data dou set zai jie mian shang
	 * @param position
	 */
	private void FillData(int position,ViewGroup parent) {

		String url = mData.get(position).Image;
		String content = mData.get(position).Title;
		String time = mData.get(position).pubDate;
		String num = mData.get(position).VisitCount;

		if (url.length()>2)Picasso.with(mContext).load(url).placeholder(R.drawable.default_bg_pic).into(holder.ciistAdpImg1);
		if (url.length()>2)Picasso.with(mContext).load(url).placeholder(R.drawable.default_bg_pic).into(holder.ciistAdpImg2);
		if (url.length()>2)Picasso.with(mContext).load(url).placeholder(R.drawable.default_bg_pic).into(holder.ciistAdpImg4);
//		utils.display(holder.ciistAdpImg1,url,new BitmapDisplayConfig().setLoadFailedDrawable());


		if (content.equals(""))holder.ciistAdpContent1.setText("");else holder.ciistAdpContent1.setText(content);
		if (content.equals(""))holder.ciistAdpContent2.setText("");else holder.ciistAdpContent2.setText(content);
		if (content.equals(""))holder.ciistAdpContent3.setText("");else holder.ciistAdpContent3.setText(content);
		if (content.equals(""))holder.ciistAdpContent4.setText("");else holder.ciistAdpContent4.setText(content);
		
		if (time.equals(""))holder.ciistAdpTime1.setText("");else holder.ciistAdpTime1.setText(changeTimeStyle(time));
		if (time.equals(""))holder.ciistAdpTime2.setText("");else holder.ciistAdpTime2.setText(changeTimeStyle(time));
		if (time.equals(""))holder.ciistAdpTime3.setText("");else holder.ciistAdpTime3.setText(changeTimeStyle(time));
		
		if (num.equals(""))holder.ciistAdpNum1.setText("");else holder.ciistAdpNum1.setText(" "+num);
		if (num.equals(""))holder.ciistAdpNum2.setText("");else holder.ciistAdpNum2.setText(" "+num);
		if (num.equals(""))holder.ciistAdpNum3.setText("");else holder.ciistAdpNum3.setText(" "+num);
	}
	
	
	/**
	 *  change time style
	 */
	@SuppressLint("SimpleDateFormat") private String changeTimeStyle(String time){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		String strs0[] = date.split(" ");
		String strs1[] = strs0[0].split("-");
		String strs2[] = time.split(" ");
		String strs3[] = strs2[0].split("-");
		
		String strs4[] = strs0[1].split(":");
		String str = strs4[0] + ":" + strs4[1];
		if (strs0[0].equals(strs2[0])) return "今天 " + str;
		if (strs1[0].equals(strs3[0])) {
			if (strs1[1].equals(strs3[1])) {
				int a = StringToInt(strs1[2]) - 1;
				int b = StringToInt(strs1[2]) - 2;
				if (strs3[2].equals(a + "")) {
					return "昨天";
				}else if (strs3[2].equals(b + "")) {
					return "前天";
				}else{
					return formatTime1(time);
				}
			}else{
				return formatTime1(time);
			}
		}else{
			return formatTime2(time);
		}
	}
	
	
	/**
	 * string to int
	 * @param str
	 * @return
	 */
	private int StringToInt(String str){
		return Integer.parseInt(str);
	}
	

	/**
	 * change time style1
	 * 
	 * @param time
	 * @return time
	 */
	public String formatTime1(String time) {
		String strs[] = time.split(" ");
		String[] b = strs[0].split("-");
		return b[1] + "-" + b[2];
	}
	
	
	/**
	 * change time style2
	 * 
	 * @param time
	 * @return time
	 */
	public String formatTime2(String time) {
		String strs[] = time.split(" ");
		String[] b = strs[0].split("-");
		return b[0] + "-" + b[1] + "-" + b[2];
	}
	

	/** holder you hua **/
	class ViewHolder {
		// four style
		LinearLayout ciistAdpStyle1, ciistAdpStyle2, ciistAdpStyle3,ciistAdpStyle4;
		// four text content
		TextView ciistAdpContent1, ciistAdpContent2, ciistAdpContent3,ciistAdpContent4;
		// three image
		ImageView ciistAdpImg1, ciistAdpImg2, ciistAdpImg4;
		// three text time
		TextView ciistAdpTime1, ciistAdpTime2, ciistAdpTime3;
		// three text num
		TextView ciistAdpNum1, ciistAdpNum2, ciistAdpNum3;

		public ViewHolder(View v) {
			ciistAdpStyle1 = (LinearLayout) v.findViewById(R.id.ciistAdpStyle1);
			ciistAdpStyle2 = (LinearLayout) v.findViewById(R.id.ciistAdpStyle2);
			ciistAdpStyle3 = (LinearLayout) v.findViewById(R.id.ciistAdpStyle3);
			ciistAdpStyle4 = (LinearLayout) v.findViewById(R.id.ciistAdpStyle4);

			ciistAdpImg1 = (ImageView) v.findViewById(R.id.ciistAdpImg1);
			ciistAdpImg2 = (ImageView) v.findViewById(R.id.ciistAdpImg2);
			ciistAdpImg4 = (ImageView) v.findViewById(R.id.ciistAdpImg4);

			ciistAdpContent1 = (TextView) v.findViewById(R.id.ciistAdpContent1);
			ciistAdpContent2 = (TextView) v.findViewById(R.id.ciistAdpContent2);
			ciistAdpContent3 = (TextView) v.findViewById(R.id.ciistAdpContent3);
			ciistAdpContent4 = (TextView) v.findViewById(R.id.ciistAdpContent4);

			ciistAdpTime1 = (TextView) v.findViewById(R.id.ciistAdpTime1);
			ciistAdpTime2 = (TextView) v.findViewById(R.id.ciistAdpTime2);
			ciistAdpTime3 = (TextView) v.findViewById(R.id.ciistAdpTime3);

			ciistAdpNum1 = (TextView) v.findViewById(R.id.ciistAdpNum1);
			ciistAdpNum2 = (TextView) v.findViewById(R.id.ciistAdpNum2);
			ciistAdpNum3 = (TextView) v.findViewById(R.id.ciistAdpNum3);
		}
	}

}
