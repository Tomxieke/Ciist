package com.ciist.toolkits;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciist.app.R;
import com.ciist.entities.ItemInfo;
import com.ciist.entities.ResultInfo;
import com.ciist.entities.ServerInfo;
import com.ciist.swipelistview.SwipeListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ItemInfo_oaAdapter extends ArrayAdapter<ItemInfo> {
    int resource;
    private String mIdentify;
    String CurrentImageFloder = "infonews";
    private SwipeListView mSwipeListView ;
    List<ItemInfo> mDatas = new ArrayList<>();

    public ItemInfo_oaAdapter(Context _context, int _resource, List<ItemInfo> _iteminfos,
                              SwipeListView SwipeListView,String identify) {
        super(_context, _resource, _iteminfos);
        this.mIdentify = identify;
        resource = _resource;
        mSwipeListView = SwipeListView;
        mDatas = _iteminfos;
    }

    public ItemInfo_oaAdapter(Context _context, int _resource, List<ItemInfo> _iteminfos) {
        super(_context, _resource, _iteminfos);
        resource = _resource;
        mDatas = _iteminfos;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout toDoView;
        final ItemInfo item = getItem(position);
        if (convertView == null) {
            toDoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, toDoView, true);
        } else {
            toDoView = (LinearLayout) convertView;
        }

        LinearLayout smallmulu_layout = (LinearLayout) toDoView.findViewById(R.id.mulu_Layout);

        LinearLayout aaa_layout = (LinearLayout) toDoView.findViewById(R.id.AAA_layout);
        LinearLayout bbb_layout = (LinearLayout) toDoView.findViewById(R.id.BBB__Layout);
        LinearLayout ccc_layout = (LinearLayout) toDoView.findViewById(R.id.CCC_Layout);


        aaa_layout.setVisibility(View.GONE);
        bbb_layout.setVisibility(View.GONE);
        ccc_layout.setVisibility(View.GONE);
        smallmulu_layout.setVisibility(View.GONE);

        String _type = "";
        String _title = "";
        String _date = "";
        String _img = "";
        String _source = "";
        String _author = "";
        String _bak1 = "";
        String _bak2 = "";
        String _bak3 = "";
        String _bak4 = "";
        String _bak5 = "";
        int _visicounter = 0;
        double _juli = 0;
        TextView simpletextview_title = (TextView) toDoView.findViewById(R.id.list_view_oa_default_text);
        simpletextview_title.setTag(item);

        if (mSwipeListView != null){
            Button delBtn = (Button) toDoView.findViewById(R.id.id_remove);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(position);
                    notifyDataSetChanged();
                    //这里还要有删除后台数据的操作
                    new DelTaskThread(item).start();
                    /**
                     * 关闭SwipeListView
                     * 不关闭的话，刚删除位置的item存在问题
                     * 在监听事件中onListChange中关闭，会出现问题
                     */
                    mSwipeListView.closeOpenedItems();
                }
            });
        }


        if (item != null) {
            _type = item.getInfotype();
            _title = item.getTitle();
            _date = item.getPubdate();
            _img = item.getImgsrc();
            _source = item.getSource();
            _author = item.getAuthor();
            _bak1 = item.getBak1();
            _bak2 = item.getBak2();
            _bak3 = item.getBak3();
            _bak4 = item.getBak4();
            _bak5 = item.getBak5();
            _visicounter = item.getVisitCount();
            _juli = item.getJuli();
        }
        if (_type.equals("AAA")) {
            aaa_layout.setVisibility(View.VISIBLE);
            TextView txt1 = (TextView) toDoView.findViewById(R.id.txtAAAFuncname);
            txt1.setText(_title);
            TextView txt2 = (TextView) toDoView.findViewById(R.id.txtAAATotal);
            txt2.setText("共" + _bak1 + "项");
            TextView txt3 = (TextView) toDoView.findViewById(R.id.txtAAAEnd);
            txt3.setText("完成" + _bak2 + "项");
            TextView txt4 = (TextView) toDoView.findViewById(R.id.txtAAANews);
            if (_bak3 == null || _bak3.equals("") || _bak3.equals("0")) {
                txt4.setVisibility(View.GONE);
            } else {
                txt4.setVisibility(View.VISIBLE);
            }
            txt4.setText(_bak3);
            ProgressBar pb = (ProgressBar) toDoView.findViewById(R.id.pbAAAprogressbar);
            pb.setMax(Integer.valueOf(_bak1));
            pb.setProgress(Integer.valueOf(_bak2));
        }else if(_type.equals("CCC")) {
            ccc_layout.setVisibility(View.VISIBLE);
            TextView txt1 = (TextView) toDoView.findViewById(R.id.CCCTextView);
            txt1.setText(_title);
            ImageView img1= (ImageView) toDoView.findViewById(R.id.CCCImgView);
            if(_img!=null && !_img.equals("")){
                img1.setImageResource(Integer.valueOf(_img));
            }
        }
        else if (_type.equals("BBB")) {
            bbb_layout.setVisibility(View.VISIBLE);
            TextView txt1 = (TextView) toDoView.findViewById(R.id.BBBTextView_center);
            txt1.setText(_title);
            TextView txt2 = (TextView) toDoView.findViewById(R.id.BBBTextView_lef_top);
            txt2.setText("" + _bak1);//
            TextView txt3 = (TextView) toDoView.findViewById(R.id.BBBTextView_right_top);
            String isOver = _author;
            if (isOver.equals("0")) {
                txt3.setText("倒计时：" + _bak2);
                if (_bak2.contains("超期")) {
                    txt3.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    txt3.setTextColor(Color.parseColor("#5E5E5E"));
                }
                TextView txtNew = (TextView) toDoView.findViewById(R.id.BBBNews);
                txtNew.setText("新");
                if (_visicounter == 0) {
                    txtNew.setVisibility(View.VISIBLE);
                } else {
                    txtNew.setVisibility(View.GONE);
                }
            } else {
                txt3.setText("已完成");
                txt3.setTextColor(Color.parseColor("#5E5E5E"));
                TextView txtNew = (TextView) toDoView.findViewById(R.id.BBBNews);
                txtNew.setVisibility(View.GONE);
            }

            TextView txt4 = (TextView) toDoView.findViewById(R.id.BBBTextView_bottom);
            txt4.setText("要求日期：" + _bak3);

        } else if (_type.equals("smallmulupic")) {

            smallmulu_layout.setVisibility(View.VISIBLE);
            ImageView small_imgview = (ImageView) toDoView.findViewById(R.id.smallmuluImgView);
            TextView small_textview = (TextView) toDoView.findViewById(R.id.smallmuluTextView);
//            TextView small_Detail_textview = (TextView) toDoView.findViewById(R.id.smallDetailTextView);
            small_textview.setText(_title);
//            small_Detail_textview.setText(_bak1);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, small_imgview);
            }
        }
        return toDoView;

    }
    //class end

    /**
     * 删除任务线程
     *
     */
    private class DelTaskThread extends Thread{
        private ItemInfo item;  //对应任务的那条信息
        public DelTaskThread(ItemInfo itemInfo){
            this.item = itemInfo;
        }
        @Override
        public void run() {
            ResultInfo result = new ResultInfo();
            String returnString = "";
            //    String _url = ServerInfo.ServerBKRoot+"/gov/Praise/ciistkey/"+getInfoIds(WhereUrl)+"/"+getDeviceId();
            String _url = ServerInfo.ServerBKRoot+"/task/delMyTask/ciistkey/"+mIdentify+"/"+item.getLinkurl();
         //   Log.d("test","-----url------"+_url+"   + item.getLinkurl() :+"+item.getLinkurl());
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(_url);
            JSONObject json = null;
            try {
                HttpResponse res = client.execute(get);
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = res.getEntity();
                    returnString = EntityUtils.toString(entity);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                client.getConnectionManager().shutdown();
            }
        //    Log.e("test","---phoneNum----:"+returnString);
            /*if (returnString != "-9"){
                mHandler.obtainMessage(GET_GOOD_SUCCESS,result).sendToTarget();
            }else{
                mHandler.obtainMessage(GET_GOOD_FAILURE,result).sendToTarget();
            }*/

        }
    }

}
