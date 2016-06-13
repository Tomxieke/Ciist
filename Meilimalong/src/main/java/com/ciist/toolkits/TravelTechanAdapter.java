package com.ciist.toolkits;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ciist.app.R;
import com.ciist.entities.ItemInfo;
import com.ciist.util.NetUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by xieke on 2016/1/27.
 */
public class TravelTechanAdapter extends RecyclerView.Adapter<TravelTechanAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ItemInfo> mDatas = new ArrayList<>();

    public TravelTechanAdapter(Context context){
        this.context = context;
    }

    public void addData(ArrayList<ItemInfo> data){
        this.mDatas = data;
        notifyDataSetChanged();
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.travel_techan_item_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ItemInfo info = mDatas.get(position);
        holder.titleTxt.setText(NetUtil.IsubString(info.getTitle(), 7));
    //    Hutils.LoadImage(info.getImgsrc(), holder.iconImg);
        Picasso.with(context).load(info.getImgsrc()).placeholder(R.drawable.default_bg_pic).into(holder.iconImg);
        holder.contentTxt.setText(NetUtil.IsubString(info.getBak1(), 7));

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //   int pos = holder.getLayoutPosition();
                    int pos = holder.getPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    //   int pos = holder.getLayoutPosition();
                    int pos = holder.getPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public ArrayList<ItemInfo> getmDatas() {
        return mDatas;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt,contentTxt;
        ImageView iconImg;

        public MyViewHolder(View view) {
            super(view);
            titleTxt = (TextView) view.findViewById(R.id.travel_techan_item_title_txt);
            contentTxt = (TextView) view.findViewById(R.id.travel_techan_item_content_txt);
            iconImg = (ImageView) view.findViewById(R.id.travel_techan_item_img);
        }
    }
}
