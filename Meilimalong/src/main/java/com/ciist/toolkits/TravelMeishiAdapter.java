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
public class TravelMeishiAdapter extends BaseAdapter{
    private ArrayList<ItemInfo> data = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;

    public TravelMeishiAdapter(Context context){
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void addData(ArrayList<ItemInfo> mData){
        this.data = mData;
        notifyDataSetChanged();
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

        View v = null;
        Holders holder = null;
        if (convertView == null){
            holder = new Holders();
            v = inflater.inflate(R.layout.travel_meishi_item_layout,null);
            holder.iconImg = (ImageView) v.findViewById(R.id.travel_meishi_item_img);
            holder.titleTxt = (TextView) v.findViewById(R.id.travel_meishi_item_title_txt);
            holder.clickCountTxt = (TextView) v.findViewById(R.id.travel_meishi_item_content_txt);
            holder.timeTxt = (TextView) v.findViewById(R.id.travel_meishi_enter_time_txt);
            holder.clickCountTxt = (TextView) v.findViewById(R.id.travel_meishi_item_click_count);
            holder.contentTxt = (TextView) v.findViewById(R.id.travel_meishi_item_content_txt);
            v.setTag(holder);
        }else {
            v = convertView;
            holder = (Holders) v.getTag();
        }

        ItemInfo info = (ItemInfo) getItem(position);
      //  Hutils.LoadImage(info.getImgsrc(),holder.iconImg);
        Picasso.with(mContext).load(info.getImgsrc()).placeholder(R.drawable.default_bg_pic).into(holder.iconImg);
        holder.titleTxt.setText(NetUtil.IsubString(info.getTitle(),8));
        holder.timeTxt.setText(NetUtil.IsubString(info.getAuthor(),6));
        holder.contentTxt.setText(info.getBak1());
        holder.clickCountTxt.setText(info.getVisitCount()+"次");
        return v;
    }

    class Holders{
        ImageView iconImg;
        TextView titleTxt,contentTxt,timeTxt,clickCountTxt;
    }
}
