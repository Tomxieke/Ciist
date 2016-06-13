package com.ciist.toolkits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ciist.app.R;
import com.ciist.entities.ItemInfo;
import com.hw.ciist.util.Hutils;
import com.hw.ciist.util.Hutils.Ciist_entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2016/2/22.
 */
public class DocumentGerenAdapter extends BaseAdapter {

    private Context mContext;
    private ViewHolder hold;
    private List<Ciist_entity> mData;

    public DocumentGerenAdapter(Context mContext) {
        this.mContext = mContext;
        this.mData = new ArrayList<Ciist_entity>();



    }

    public void setmData(List<Ciist_entity> mData){
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
                    R.layout.item_document_list, null);
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
        hold.document_neirong.setText(mData.get(position).Title);

    }



    class ViewHolder {
        TextView document_neirong;


        public ViewHolder(View v) {
            document_neirong = (TextView) v.findViewById(R.id.document_neirong);

        }

    }

}
