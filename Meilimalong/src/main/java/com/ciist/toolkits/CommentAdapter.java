package com.ciist.toolkits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ciist.app.R;
import com.hw.ciist.util.Hutils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2016/2/25.
 */
public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private ViewHolder hold;
    private List<Hutils.Ciist_entity> mData;

    public CommentAdapter(Context mContext, List<Hutils.Ciist_entity> mData) {
        this.mContext = mContext;
        this.mData = new ArrayList<Hutils.Ciist_entity>();
        this.mData = mData;
        notifyDataSetChanged();

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
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_comment_list, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        setDate(position);

        return convertView;
    }




    public String formatTime(String time) {
        String[] strs = time.split(" ");

        for(int b = 0; b < strs.length; ++b) {
            System.out.println(strs[b]);
        }

        String[] var5 = strs[0].split("-");

        for(int i = 0; i < var5.length; ++i) {
            System.out.println(var5[i]);
        }

        return var5[1] + "-" + var5[2];
    }


  void setDate(int position) {
       hold.comment_neirong.setText(mData.get(position).Title);
      hold.comment_riqi.setText(formatTime(mData.get(position).pubDate));

    }

    class ViewHolder {
        //        TextView merchants_tiaoshu;
        TextView comment_neirong;
        TextView comment_riqi;


        public ViewHolder(View v) {
//            merchants_tiaoshu = (TextView) v.findViewById(R.id.merchants_tiaoshu);
            comment_neirong = (TextView) v.findViewById(R.id.comment_neirong);
            comment_riqi=(TextView)v.findViewById(R.id.comment_riqi);

        }

    }
}
