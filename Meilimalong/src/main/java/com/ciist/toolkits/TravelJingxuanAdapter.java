package com.ciist.toolkits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciist.app.R;
import com.ciist.entities.ItemInfo;
import com.ciist.util.NetUtil;
import com.hw.ciist.util.Hutils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by xieke on 2016/1/27.
 */
public class TravelJingxuanAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList<ItemInfo> datas = new ArrayList<>();
    private Context mContext;
    public TravelJingxuanAdapter(Context context){
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void addData(ArrayList<ItemInfo> data){
        this.datas = data;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        JingxuanHolder holder;
        if (convertView == null){
            holder = new JingxuanHolder();
            v = inflater.inflate(R.layout.travel_jingxuan_item_layout,null);
            holder.titleTxt = (TextView) v.findViewById(R.id.travel_jingxuan_item_title_txt);
            holder.ImgView = (ImageView) v.findViewById(R.id.jingxuan_item_img);
            holder.content = (TextView) v.findViewById(R.id.travel_jingxuan_item_content_txt);
            v.setTag(holder);
        }else {
            v = convertView;
            holder = (JingxuanHolder) v.getTag();
        }

        ItemInfo info = (ItemInfo) getItem(position);
        holder.titleTxt.setText(NetUtil.IsubString(info.getTitle(), 7));
        holder.content.setText(info.getBak1());
       // Hutils.LoadImage(info.getImgsrc(),holder.ImgView);
        Picasso.with(mContext).load(info.getImgsrc()).placeholder(R.drawable.default_bg_pic).into(holder.ImgView);
        return v;
    }

    class JingxuanHolder{
        TextView titleTxt,content;
        ImageView ImgView;
    }
}
