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
import com.hw.ciist.util.Hutils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by xieke on 2016/1/31.
 */
public class CiistIGridviewAdapter extends BaseAdapter{
    private ArrayList<ItemInfo> lists = new ArrayList<>();
    private LayoutInflater inflater;
    private int VIEW_TYPE_COUNT = 2;
    public static final int HORIZONTAL_TYPE = 0;  //水平排版
    public static final int VERTICAL_TYPE = 1;  //纵向显示
    private int DEFAULT_TYPE = 0;  //默认为水平排版
    private Context mContext;

    public CiistIGridviewAdapter(Context context){
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setData(ArrayList datas){
        this.lists = datas;
        notifyDataSetChanged();
    }

    /**
     *  set layout style
     *  layout的展现形式
     * @param type
     */
    public void setType(int type){
        DEFAULT_TYPE = type;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (DEFAULT_TYPE == HORIZONTAL_TYPE){
            return HORIZONTAL_TYPE;
        }else {
            return VERTICAL_TYPE;
        }
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        Holder holder1 = null;
        Holder holder2 = null;
        int viewType = getItemViewType(position);
        if (null == convertView){
            switch (viewType){
                case HORIZONTAL_TYPE:
                    holder1 = new Holder();
                    view = inflater.inflate(R.layout.ciist_gridview_item_layout,null);
                    holder1.iconImg = (ImageView) view.findViewById(R.id.ciist_item_icon_img);
                    holder1.titleTxt = (TextView) view.findViewById(R.id.ciist_item_title_txt);
                    view.setTag(holder1);
                    break;
                case VERTICAL_TYPE:
                    holder2 = new Holder();
                    view = inflater.inflate(R.layout.ciist_gridview_vitem_layout,null);
                    holder2.iconImg = (ImageView) view.findViewById(R.id.gridview_top_img);
                    holder2.titleTxt = (TextView) view.findViewById(R.id.gridview_buttom_img);
                    view.setTag(holder2);
                    break;
            }
        }else {
            switch (viewType){
                case HORIZONTAL_TYPE:
                    view = convertView;
                    holder1 = (Holder) view.getTag();
                    break;
                case VERTICAL_TYPE:
                    view = convertView;
                    holder2 = (Holder) view.getTag();
                    break;
            }
        }
        ItemInfo itemInfo = (ItemInfo) getItem(position);
        switch (viewType){
            case HORIZONTAL_TYPE:
             //   Hutils.LoadImage(itemInfo.getImgsrc(),holder1.iconImg);
                Picasso.with(mContext)
                        .load(itemInfo.getImgsrc())
                        .placeholder(R.drawable.default_bg_pic)
                        .into(holder1.iconImg);
                holder1.titleTxt.setText(itemInfo.getTitle());
                break;
            case VERTICAL_TYPE:
              //  Hutils.LoadImage(itemInfo.getImgsrc(),holder2.iconImg);
                Picasso.with(mContext)
                        .load(itemInfo.getImgsrc())
                        .placeholder(R.drawable.default_bg_pic)
                        .into(holder2.iconImg);
                holder2.titleTxt.setText(itemInfo.getTitle());
                break;
        }

    //    holder1.titleTxt.setText(itemInfo.getTitletxt());
        return view;
    }

    class Holder{
        ImageView iconImg;
        TextView titleTxt;
    }

}
