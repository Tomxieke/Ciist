package com.ciist.customview.xlistview;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciist.app.R;


/**
 * Created by xieke on 2016/1/31.
 */
public class CiistSmallTitle extends RelativeLayout implements View.OnClickListener{

    private String titleTxt,rightTxt;
    private int icon_color,txtColor;
    private OnClickListener onClickListener;
    public void setRightOnClickListener(OnClickListener o){
        this.onClickListener = o;
    }

    public CiistSmallTitle(Context context) {
        super(context);
    }

    public CiistSmallTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CiistSmallTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs){
        TypedArray array = null;
        try {
            array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CiistSmallTitle, 0, 0);
            titleTxt = array.getString(R.styleable.CiistSmallTitle_ciist_smalltitle_title);
            icon_color = array.getColor(R.styleable.CiistSmallTitle_ciist_smalltitle_icon_color,-1);
            rightTxt = array.getString(R.styleable.CiistSmallTitle_ciist_smalltitle_right_txt);
            txtColor = array.getColor(R.styleable.CiistSmallTitle_ciist_smalltitle_txt_color,-1);
            LayoutInflater inflater = LayoutInflater.from(context);
            initView(inflater);
        }finally {
            array.recycle();
        }
    }

    /**
     * get XML sorc
     * @param inflater
     */
    private void initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.small_titleview_layout,this,true);
        if (null != titleTxt){
            TextView titleView = (TextView) view.findViewById(R.id.small_title_txt);
            titleView.setText(titleTxt);
            if (-1 != txtColor){
                titleView.setTextColor(txtColor);
            }
        }
        if (-1 != icon_color){
            View iconView = view.findViewById(R.id.small_title_icon);
            iconView.setBackgroundColor(icon_color);
        }
        if (null != rightTxt){
            TextView rightTv = (TextView) view.findViewById(R.id.small_title_rightText);
            rightTv.setVisibility(VISIBLE);
            rightTv.setOnClickListener(this);
            rightTv.setText(rightTxt);
            if (-1 != txtColor){
                rightTv.setTextColor(txtColor);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (null != onClickListener){
            onClickListener.onClick(v);
        }
    }
}
