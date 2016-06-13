package com.ciist.toolkits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by xieke on 2016/2/2.
 */
public class PhotoGridViewAdapter extends BaseAdapter{
    private ArrayList<String> imgList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public PhotoGridViewAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<String> mList){
        this.imgList = mList;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<String> data){
        this.imgList.addAll(data);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      /*  View view;
        Holder holder = null;
        if (convertView == null){
            holder = new Holder();
            view = inflater.inflate(R.layout.pgridview_item_layout,null);
            holder.viewimg = (ImageView) view.findViewById(R.id.item_img);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (Holder) view.getTag();
        }


        Uri path = (Uri) getItem(position);
        Glide.with(context)
                .load(path)
                .into(holder.viewimg);
        return view;*/

        ImageView imageview;
        if(convertView==null)
        {
            imageview=new ImageView(context);
            imageview.setLayoutParams(new GridView.LayoutParams(
                    500,
                    500));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setPadding(8,8,8,8);
        }
        else
        {
            imageview=(ImageView) convertView;
        }
        String path = (String) getItem(position);
        imageview.setImageBitmap(getBitmap(path));
       /* if (path instanceof Uri){
            Glide.with(context)
                    .load(path)
                    .into(imageview);
        }else{
          //  imageview.setImageBitmap(ImageTools.g);
        }*/

        return imageview;
    }

    /**
     *
     * @param path
     * @return bitmap
     */
    private   Bitmap getBitmap(String path){
        Bitmap photoBitmap = BitmapFactory.decodeFile(path +".png");
        if (photoBitmap == null) {
            return null;
        }else {
            return photoBitmap;
        }
    }


    class Holder{
        ImageView viewimg;
    }
}
