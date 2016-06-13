package com.ciist.customview.xlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciist.app.R;


/**
 * Created by xieke on 2016/1/28.
 */
public class CiistTitleView extends RelativeLayout implements View.OnClickListener{
    private String titleTxt,leftTxt,rightTxt;
    private int leftIcon,rightIcon;
    private int bgColor; //  背景颜色 资源Id图片 RGB颜色
    private int textColor; //字体颜色 RGB颜色
    private TextView titleTv;  //标题view
    private RelativeLayout layout;  //整个布局
    /**
     * left and right ClickListener
     */
    private OnClickListener liftClickListener,rightClickListener;
    public void setOnLiftClickListener(OnClickListener onClickListener){
        this.liftClickListener = onClickListener;
    }
    public void setOnRightClickListener(OnClickListener onClickListener){
        this.rightClickListener = onClickListener;
    }

    public CiistTitleView(Context context) {
        super(context);
    }

    public CiistTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CiistTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CiistTitleView, 0, 0);
            leftTxt = array.getString(R.styleable.CiistTitleView_ciist_left_text);
            rightTxt = array.getString(R.styleable.CiistTitleView_ciist_right_text);
            titleTxt = array.getString(R.styleable.CiistTitleView_ciist_title_text);
            leftIcon = array.getResourceId(R.styleable.CiistTitleView_ciist_left_icn, -1);
            rightIcon = array.getResourceId(R.styleable.CiistTitleView_ciist_right_icn, -1);
            bgColor = array.getColor(R.styleable.CiistTitleView_ciist_titleview_bg,-1);
            textColor = array.getColor(R.styleable.CiistTitleView_ciist_text_color,-1);
            LayoutInflater inflater = LayoutInflater.from(context);
            initView(inflater);
        }finally {
            array.recycle();
        }

    }

    /**
     * 解析布局并加载
     *
     * @param inflater
     */
    private void initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.titleview_layout,this,true);
       titleTv = (TextView) view.findViewById(R.id.ciist_titleview_title_txt);
        if (-1 != bgColor){
            layout = (RelativeLayout) view.findViewById(R.id.ciist_titleview_parent_layout);
            layout.setBackgroundColor(bgColor);
        }

        if (null != titleTxt){
            titleTv.setText(titleTxt);
            if (-1 != textColor){
                titleTv.setTextColor(textColor);
            }
        }

        if (null != leftTxt){
            TextView leftTv = (TextView) view.findViewById(R.id.ciist_titleview_left_txt);
            leftTv.setVisibility(VISIBLE);
            leftTv.setText(leftTxt);
            if (-1 != textColor){
                leftTv.setTextColor(textColor);
            }
            leftTv.setOnClickListener(this);
        }

        if (null != rightTxt ){
            TextView rightTv = (TextView) view.findViewById(R.id.ciist_titleview_right_txt);
            rightTv.setVisibility(VISIBLE);
            rightTv.setText(rightTxt);

            if (-1 != textColor){
                rightTv.setTextColor(textColor);
            }
            rightTv.setOnClickListener(this);
        }



        if (-1 != leftIcon ){
            ImageView leftIv = (ImageView) view.findViewById(R.id.ciist_titleview_left_img);
            leftIv.setVisibility(VISIBLE);
            leftIv.setImageResource(leftIcon);
            leftIv.setOnClickListener(this);
        }

        if (-1 != rightIcon){
            ImageView rightIv = (ImageView) view.findViewById(R.id.ciist_titleview_right_img);
            rightIv.setVisibility(VISIBLE);
            rightIv.setImageResource(rightIcon);
            rightIv.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ciist_titleview_left_img:
                setClick(liftClickListener,v);
                break;
            case R.id.ciist_titleview_left_txt:
                setClick(liftClickListener,v);
                break;
            case R.id.ciist_titleview_right_img:
                setClick(rightClickListener,v);
                break;
            case R.id.ciist_titleview_right_txt:
                setClick(rightClickListener,v);
                break;
        }
    }

    /**
     * set listener to view
     * @param o
     */
    private void setClick(OnClickListener o ,View view){
        if (null != o){
            o.onClick(view);
        }
    }

    /**
     * set  title text
     * @param title
     */
    public void setTitle(String title){
        titleTv.setText(title);
        if (-1 != textColor){
            titleTv.setTextColor(textColor);
        }
    }

    /**
     * set bgColor
     * @param bgColor
     */
    public void setBgColor(int bgColor){
        if (bgColor != -1){
            layout.setBackgroundColor(bgColor);
        }
    }
}
