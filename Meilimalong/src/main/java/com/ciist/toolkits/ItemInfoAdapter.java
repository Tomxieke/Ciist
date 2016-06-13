package com.ciist.toolkits;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciist.app.R;
import com.ciist.entities.ItemInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class ItemInfoAdapter extends ArrayAdapter<ItemInfo> {
    int resource;
    String CurrentImageFloder = "infonews";

    public ItemInfoAdapter(Context _context, int _resource, List<ItemInfo> _iteminfos) {
        super(_context, _resource, _iteminfos);
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout toDoView;
        ItemInfo item = getItem(position);
        if (convertView == null) {
            toDoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, toDoView, true);
        } else {
            toDoView = (LinearLayout) convertView;
        }

        RelativeLayout big_Layout = (RelativeLayout) toDoView.findViewById(R.id.bigPic_Layout);
        RelativeLayout ad_Layout = (RelativeLayout) toDoView.findViewById(R.id.AD_Layout);
        LinearLayout small_Layout = (LinearLayout) toDoView.findViewById(R.id.small_Layout);
        LinearLayout simple_Layout = (LinearLayout) toDoView.findViewById(R.id.simple_Layout);
        RelativeLayout zhaoshang_Layout = (RelativeLayout) toDoView.findViewById(R.id.zhaoshang_Layout);
        RelativeLayout hipop_Layout = (RelativeLayout) toDoView.findViewById(R.id.hipop_Layout);
        LinearLayout productSale_Layout = (LinearLayout) toDoView.findViewById(R.id.productSale_Layout);
        RelativeLayout tinypic_layout = (RelativeLayout) toDoView.findViewById(R.id.tinyPic_Layout);
        LinearLayout info_bigImageMode_Layout = (LinearLayout) toDoView.findViewById(R.id.infoBigImageMode_Layout);
        LinearLayout travel_bigImageMode_Layout = (LinearLayout) toDoView.findViewById(R.id.travelBigImageMode_Layout);
        LinearLayout techan_bigImageMode_Layout = (LinearLayout) toDoView.findViewById(R.id.techanBigImageMode_Layout);
        LinearLayout notice_sampleMode_Layout = (LinearLayout) toDoView.findViewById(R.id.notice__Layout);
        RelativeLayout cover400_Layout = (RelativeLayout) toDoView.findViewById(R.id.cover400_Layout);
        RelativeLayout cover300_Layout = (RelativeLayout) toDoView.findViewById(R.id.cover300_Layout);
        RelativeLayout cover200_Layout = (RelativeLayout) toDoView.findViewById(R.id.cover200_Layout);
        LinearLayout nav_Layout = (LinearLayout) toDoView.findViewById(R.id.nav_Layout);
        RelativeLayout zhaoshangpeojectindex_layout = (RelativeLayout) toDoView.findViewById(R.id.zhaoshangprojectindex_Layout);
        LinearLayout smallmulu_layout = (LinearLayout) toDoView.findViewById(R.id.smallmulu_Layout);

        LinearLayout aaa_layout = (LinearLayout) toDoView.findViewById(R.id.AAA_layout);

        TextView simpletextview_title = (TextView) toDoView.findViewById(R.id.simpleTextView1);
        aaa_layout.setVisibility(View.GONE);
        smallmulu_layout.setVisibility(View.GONE);
        zhaoshangpeojectindex_layout.setVisibility(View.GONE);
        nav_Layout.setVisibility(View.GONE);
        big_Layout.setVisibility(View.GONE);
        ad_Layout.setVisibility(View.GONE);
        small_Layout.setVisibility(View.GONE);
        simple_Layout.setVisibility(View.GONE);
        zhaoshang_Layout.setVisibility(View.GONE);
        hipop_Layout.setVisibility(View.GONE);
        productSale_Layout.setVisibility(View.GONE);
        tinypic_layout.setVisibility(View.GONE);
        info_bigImageMode_Layout.setVisibility(View.GONE);
        travel_bigImageMode_Layout.setVisibility(View.GONE);
        techan_bigImageMode_Layout.setVisibility(View.GONE);
        notice_sampleMode_Layout.setVisibility(View.GONE);
        cover400_Layout.setVisibility(View.GONE);
        cover300_Layout.setVisibility(View.GONE);
        cover200_Layout.setVisibility(View.GONE);

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

        simpletextview_title.setTag(item);

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
            //Log.e("a",_juli+"");
        }
        if (_type.equals("simple")) {
            simple_Layout.setVisibility(View.VISIBLE);
            TextView simpletextview_date = (TextView) toDoView.findViewById(R.id.simpleTextView0);
            simpletextview_title.setText(_title);
            if (_date != null && _date != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = _date;
                try {
                    java.util.Date date = sdf.parse(dstr);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int mm = c.get(Calendar.MONTH) + 1;
                    int dd = c.get(Calendar.DAY_OF_MONTH);
                    String mm_str = "";
                    String dd_str = "";
                    if (mm < 10) {
                        mm_str = "0" + String.valueOf(mm);
                    } else {
                        mm_str = String.valueOf(mm);
                    }
                    if (dd < 10) {
                        dd_str = "0" + String.valueOf(dd);
                    } else {
                        dd_str = String.valueOf(dd);
                    }
                    simpletextview_date.setText(mm_str + "月" + dd_str + "日");
                    simpletextview_date.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                simpletextview_date.setText("");
            }
            if (_bak5.length() <= 6) {
                simpletextview_date.setVisibility(View.VISIBLE);
            } else {
                simpletextview_date.setVisibility(View.GONE);
            }

        } else if (_type.equals("AAA")) {
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
        } else if (_type.equals("smallpic")) {
            small_Layout.setVisibility(View.VISIBLE);
            ImageView small_imgview = (ImageView) toDoView.findViewById(R.id.smallImgView);
//            TextView small_textview = (TextView) toDoView.findViewById(R.id.smallTextView);
            //222
            TextView simpletextview_date = (TextView) toDoView.findViewById(R.id.smallTextPubDateView);
            TextView simpletitletextview = (TextView) toDoView.findViewById(R.id.smallTextView);
            TextView simpledanweitextview = (TextView) toDoView.findViewById(R.id.smallTextDanweiView);
            simpletitletextview.setText(_title);
//            simpledanweitextview.setText(_author);
            if (_visicounter > 0) {
                simpledanweitextview.setText(_visicounter + "次");
            } else {
                simpledanweitextview.setText("");
            }
            if (_date != null && _date != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = _date;
                try {
                    java.util.Date date = sdf.parse(dstr);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int yy = c.get(Calendar.YEAR);
                    int mm = c.get(Calendar.MONTH) + 1;
                    int dd = c.get(Calendar.DAY_OF_MONTH);
                    String mm_str = "";
                    String dd_str = "";
                    if (mm < 10) {
                        mm_str = "0" + String.valueOf(mm);
                    } else {
                        mm_str = String.valueOf(mm);
                    }
                    if (dd < 10) {
                        dd_str = "0" + String.valueOf(dd);
                    } else {
                        dd_str = String.valueOf(dd);
                    }
//                    simpletextview_date.setText(yy + "年" + mm_str + "月" + dd_str + "日");
                    simpletextview_date.setText(mm_str + "-" + dd_str);
                    simpletextview_date.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                }
            }
            //22222
//            TextView small_Detail_textview = (TextView) toDoView.findViewById(R.id.smallDetailTextView);
//            small_textview.setText(_title);
//            small_Detail_textview.setText(_bak1);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, small_imgview);
            }
            if (_bak5.length() <= 6) {
                simpledanweitextview.setVisibility(View.VISIBLE);
                simpletextview_date.setVisibility(View.VISIBLE);
            } else {
                simpledanweitextview.setVisibility(View.GONE);
                simpletextview_date.setVisibility(View.GONE);
            }
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
        } else if (_type.equals("navdis")) {
            nav_Layout.setVisibility(View.VISIBLE);
            ImageView small_imgview = (ImageView) toDoView.findViewById(R.id.navImgView);
            TextView small_textview = (TextView) toDoView.findViewById(R.id.navTextView);
            TextView small_Detail_textview = (TextView) toDoView.findViewById(R.id.navDetailTextView);
            small_textview.setText(_title);
            String _k = "距离您大约" + String.valueOf(_juli) + "公里";
            if (_juli <= 0.0) {
                _k = "距离您很近，就在旁边";
            }
            small_Detail_textview.setText(_k);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, small_imgview);
            }
        } else if (_type.equals("bigpic")) {
            big_Layout.setVisibility(View.VISIBLE);
            ImageView big_imgview = (ImageView) toDoView.findViewById(R.id.bigImgView);
//            big_imgview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            TextView big_textview = (TextView) toDoView.findViewById(R.id.bigTextView);
            big_textview.setText(_title);
            big_textview.getBackground().setAlpha(150);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, big_imgview);
            }
        } else if (_type.equals("infobigpicmode")) {
            info_bigImageMode_Layout.setVisibility(View.VISIBLE);
            ImageView big_imgview = (ImageView) toDoView.findViewById(R.id.infoBigImageModeView);
//            TextView big_textview = (TextView) toDoView.findViewById(R.id.infoBigImageModeTextView);
            //222
            TextView simpletextview_date = (TextView) toDoView.findViewById(R.id.infoTextPubDateView);
            TextView simpletitletextview = (TextView) toDoView.findViewById(R.id.infoBigImageModeTextView);
            TextView simpledanweitextview = (TextView) toDoView.findViewById(R.id.infoTextDanweiView);
            simpletitletextview.setText(_title);
//            simpledanweitextview.setText(_author);
            if (_visicounter > 0) {
                simpledanweitextview.setText(_visicounter + "次");
            } else {
                simpledanweitextview.setText("");
            }
            if (_date != null && _date != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = _date;
                try {
                    java.util.Date date = sdf.parse(dstr);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int yy = c.get(Calendar.YEAR);
                    int mm = c.get(Calendar.MONTH) + 1;
                    int dd = c.get(Calendar.DAY_OF_MONTH);
                    String mm_str = "";
                    String dd_str = "";
                    if (mm < 10) {
                        mm_str = "0" + String.valueOf(mm);
                    } else {
                        mm_str = String.valueOf(mm);
                    }
                    if (dd < 10) {
                        dd_str = "0" + String.valueOf(dd);
                    } else {
                        dd_str = String.valueOf(dd);
                    }
//                    simpletextview_date.setText(yy + "年" + mm_str + "月" + dd_str + "日");
                    simpletextview_date.setText(mm_str + "-" + dd_str);
                    simpletextview_date.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                }
            }
            //22222
//            big_textview.setText(_title);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, big_imgview);
            }
            if(_bak5.length()<=6){
                simpledanweitextview.setVisibility(View.VISIBLE);
                simpletextview_date.setVisibility(View.VISIBLE);
            }else{
                simpledanweitextview.setVisibility(View.GONE);
                simpletextview_date.setVisibility(View.GONE);
            }
        } else if (_type.equals("travelbigpicmode")) {
            travel_bigImageMode_Layout.setVisibility(View.VISIBLE);
            ImageView big_imgview = (ImageView) toDoView.findViewById(R.id.travelBigImageModeView);
            TextView class_textview = (TextView) toDoView.findViewById(R.id.travelBigImageModeClassTextView);
            TextView title_textview = (TextView) toDoView.findViewById(R.id.travelBigImageModeTitleTextView);
            TextView detail_textview = (TextView) toDoView.findViewById(R.id.travelBigImageModeDetailTextView);
            title_textview.setText(_title);
            class_textview.setText(_bak1);
            detail_textview.setText(_bak2);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, big_imgview);
            }
        } else if (_type.equals("techanbigpicmode")) {
            techan_bigImageMode_Layout.setVisibility(View.VISIBLE);
            ImageView big_imgview = (ImageView) toDoView.findViewById(R.id.techanBigImageModeView);
            TextView class_textview = (TextView) toDoView.findViewById(R.id.techanBigImageModeClassTextView);
            TextView title_textview = (TextView) toDoView.findViewById(R.id.techanBigImageModeTitleTextView);
            TextView detail_textview = (TextView) toDoView.findViewById(R.id.techanBigImageModeDetailTextView);
            title_textview.setVisibility(View.GONE);
            class_textview.setVisibility(View.GONE);
            detail_textview.setVisibility(View.GONE);
            if (_title.length() > 2) {
                title_textview.setVisibility(View.VISIBLE);
                title_textview.setText(_title);
            }
            if (_bak1.length() > 2) {
                class_textview.setVisibility(View.VISIBLE);
                class_textview.setText(_bak1);
            }
            if (_bak2.length() > 2) {
                detail_textview.setVisibility(View.VISIBLE);
                detail_textview.setText(_bak2);
            }

            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, big_imgview);
            }
        } else if (_type.equals("noticesimplemode")) {
            notice_sampleMode_Layout.setVisibility(View.VISIBLE);
            TextView simpletextview_date = (TextView) toDoView.findViewById(R.id.noticeTextPubDateView);
            TextView simpletitletextview = (TextView) toDoView.findViewById(R.id.noticeTextTitleView);
            TextView simpledanweitextview = (TextView) toDoView.findViewById(R.id.noticeTextDanweiView);
            simpletitletextview.setText(_title);
//            simpledanweitextview.setText(_author);
            if (_visicounter > 0) {
                simpledanweitextview.setText(_visicounter + "次");
            } else {
                simpledanweitextview.setText("");
            }
            if (_date != null && _date != "") {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = _date;
                try {
                    java.util.Date date = sdf.parse(dstr);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int yy = c.get(Calendar.YEAR);
                    int mm = c.get(Calendar.MONTH) + 1;
                    int dd = c.get(Calendar.DAY_OF_MONTH);
                    String mm_str = "";
                    String dd_str = "";
                    if (mm < 10) {
                        mm_str = "0" + String.valueOf(mm);
                    } else {
                        mm_str = String.valueOf(mm);
                    }
                    if (dd < 10) {
                        dd_str = "0" + String.valueOf(dd);
                    } else {
                        dd_str = String.valueOf(dd);
                    }
//                    simpletextview_date.setText(yy + "年" + mm_str + "月" + dd_str + "日");
                    simpletextview_date.setText(mm_str + "-" + dd_str);
                    simpletextview_date.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                }
            }
            if(_bak5.length()<=6){
                simpledanweitextview.setVisibility(View.VISIBLE);
                simpletextview_date.setVisibility(View.VISIBLE);
            }else{
                simpledanweitextview.setVisibility(View.GONE);
                simpletextview_date.setVisibility(View.GONE);
            }
        } else if (_type.equals("AD")) {
            ad_Layout.setVisibility(View.VISIBLE);
            ImageView ad_imgview = (ImageView) toDoView.findViewById(R.id.ADImgView);
            TextView ad_textview = (TextView) toDoView.findViewById(R.id.ADTextView);
            ad_textview.setText(_source);
            ad_textview.getBackground().setAlpha(150);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, ad_imgview);
            }
        } else if (_type.equals("hipop")) {
            hipop_Layout.setVisibility(View.VISIBLE);
            ImageView hipop_imgview = (ImageView) toDoView.findViewById(R.id.hipopImgView);
            TextView hipop_Textview = (TextView) toDoView.findViewById(R.id.hiPopTextView);
            hipop_Textview.setText(_source);
            hipop_Textview.getBackground().setAlpha(150);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, hipop_imgview);
            }
        } else if (_type.equals("cover400")) {
            cover400_Layout.setVisibility(View.VISIBLE);
            ImageView cover_imgview = (ImageView) toDoView.findViewById(R.id.cover400ImgView);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, cover_imgview);
            }
        } else if (_type.equals("cover300")) {
            cover300_Layout.setVisibility(View.VISIBLE);
            ImageView cover_imgview = (ImageView) toDoView.findViewById(R.id.cover300ImgView);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, cover_imgview);
            }
        } else if (_type.equals("cover200")) {
            cover200_Layout.setVisibility(View.VISIBLE);
            ImageView cover_imgview = (ImageView) toDoView.findViewById(R.id.cover200ImgView);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, cover_imgview);
            }
        } else if (_type.equals("zhaoshang")) {
            zhaoshang_Layout.setVisibility(View.VISIBLE);
            ImageView zhaoshang_imgview = (ImageView) toDoView.findViewById(R.id.zhaoshangImgView);
            TextView zhaoshang_textview0 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView0);
            TextView zhaoshang_textview1 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView1);
            TextView zhaoshang_textview2 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView2);
            TextView zhaoshang_textview3 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView3);
            TextView zhaoshang_textview4 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView4);
            TextView zhaoshang_textview5 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView5);
            zhaoshang_textview0.setVisibility(View.GONE);
            zhaoshang_textview1.setVisibility(View.GONE);
            zhaoshang_textview2.setVisibility(View.GONE);
            zhaoshang_textview3.setVisibility(View.GONE);
            zhaoshang_textview4.setVisibility(View.GONE);
            zhaoshang_textview5.setVisibility(View.GONE);
            if (_title.length() > 2) {
                zhaoshang_textview0.setVisibility(View.VISIBLE);
                zhaoshang_textview0.setText(_title);
            }
            if (_bak1.length() > 2) {
                zhaoshang_textview1.setVisibility(View.VISIBLE);
                zhaoshang_textview1.setText(_bak1);
            }
            if (_bak2.length() > 2) {
                zhaoshang_textview2.setVisibility(View.VISIBLE);
                zhaoshang_textview2.setText(_bak2);
            }
            if (_bak3.length() > 2) {
                zhaoshang_textview3.setVisibility(View.VISIBLE);
                zhaoshang_textview3.setText(_bak3);
            }
            if (_bak4.length() > 2) {
                zhaoshang_textview4.setVisibility(View.VISIBLE);
                zhaoshang_textview4.setText(_bak4);
            }
            if (_bak5.length() > 2) {
                zhaoshang_textview5.setVisibility(View.VISIBLE);
                zhaoshang_textview5.setText(_bak5);
            }
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, zhaoshang_imgview);
            }
        } else if (_type.equals("zhaoshangprojectindex")) {
            zhaoshang_Layout.setVisibility(View.VISIBLE);
            TextView zhaoshang_textview0 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView0);
            TextView zhaoshang_textview1 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView1);
            TextView zhaoshang_textview2 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView2);
            TextView zhaoshang_textview3 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView3);
            TextView zhaoshang_textview4 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView4);
            TextView zhaoshang_textview5 = (TextView) toDoView.findViewById(R.id.zhaoshang_TextView5);
            zhaoshang_textview0.setVisibility(View.GONE);
            zhaoshang_textview1.setVisibility(View.GONE);
            zhaoshang_textview2.setVisibility(View.GONE);
            zhaoshang_textview3.setVisibility(View.GONE);
            zhaoshang_textview4.setVisibility(View.GONE);
            zhaoshang_textview5.setVisibility(View.GONE);
            if (_title.length() > 2) {
                zhaoshang_textview0.setVisibility(View.VISIBLE);
                zhaoshang_textview0.setText(_title);
            }
            if (_author.length() > 2) {
                zhaoshang_textview1.setVisibility(View.VISIBLE);
                zhaoshang_textview1.setText("合作单位：" + _author);
            }
            if (_source.length() > 2) {
                zhaoshang_textview2.setVisibility(View.VISIBLE);
                zhaoshang_textview2.setText("联系人：" + _source);
            }
            if (_bak1.length() > 0) {
                zhaoshang_textview3.setVisibility(View.VISIBLE);
                zhaoshang_textview3.setText("总投资（亿元）：" + _bak1);
            }
            if (_bak2.length() > 0) {
                zhaoshang_textview4.setVisibility(View.VISIBLE);
                zhaoshang_textview4.setText("用地（亩）：" + _bak2);
            }
            if (_bak3.length() > 2) {
                zhaoshang_textview5.setVisibility(View.VISIBLE);
                zhaoshang_textview5.setText("项目类别：" + _bak3);
            }
        } else if (_type.equals("productsale")) {
            productSale_Layout.setVisibility(View.VISIBLE);
            ImageView productsale_imgview = (ImageView) toDoView.findViewById(R.id.productSaleImgView);
            TextView productsale_textview0 = (TextView) toDoView.findViewById(R.id.productSaleTextView0);
            TextView productsale_textview1 = (TextView) toDoView.findViewById(R.id.productSaleTextView1);
            productsale_textview0.setText(_title);
            productsale_textview1.setText(_source);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, productsale_imgview);
            }
        } else if (_type.equals("tinypic")) {
            tinypic_layout.setVisibility(View.VISIBLE);
            ImageView tinypic_imgview = (ImageView) toDoView.findViewById(R.id.tinyPicImgView);
            if (_img != null && _img != "") {
                AsyncImageLoader asyImg = new AsyncImageLoader(CurrentImageFloder);
                asyImg.LoadImage(_img, tinypic_imgview);
            }
        }
        return toDoView;

    }
    //class end
}
