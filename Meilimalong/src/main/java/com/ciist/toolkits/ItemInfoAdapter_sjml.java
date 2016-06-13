package com.ciist.toolkits;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ciist.app.R;
import com.ciist.entities.ItemInfo;
import com.ciist.helper.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class ItemInfoAdapter_sjml extends BaseAdapter {
    private List<ItemInfo> data;
    public ItemInfoAdapter_sjml(List<ItemInfo> date){
        this.data = new ArrayList<ItemInfo>();
        this.data = date;
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
        ViewHoler holer = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_shujuml,null);
            holer = new ViewHoler(convertView);
            convertView.setTag(holer);
        }else{
            holer = (ViewHoler) convertView.getTag();
        }
        //
        if (data.get(position).getBak5().equals("")){
            holer.v3.setVisibility(View.INVISIBLE);
            holer.v4.setVisibility(View.VISIBLE);
        }else{
            holer.v3.setVisibility(View.VISIBLE);
            holer.v4.setVisibility(View.INVISIBLE);
        }
        //
        holer.v1.setText(data.get(position).getTitle());
        float _tmp= MathHelper.StringToFloat(data.get(position).getAuthor());
//        if (!data.get(position).getAuthor().equals(".") && !data.get(position).getAuthor().equals("·")){
        if(_tmp>0){
            holer.v2.setText(_tmp+" "+data.get(position).getSource());
        }else{
            holer.v2.setText("");
        }
        return convertView;
    }

    class ViewHoler{
        TextView v1,v2;
        ImageView v3, v4;
        public ViewHoler(View v){
            v1 = (TextView) v.findViewById(R.id.listViewItemSjmlTitle);
            v2 = (TextView) v.findViewById(R.id.listViewItemSjmlDw);
            v3 = (ImageView) v.findViewById(R.id.listViewItemsjmlGoing2);
            v4 = (ImageView) v.findViewById(R.id.listViewItemsjmlGoing);
        }
    }

}
