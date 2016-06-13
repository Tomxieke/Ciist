package com.ciist.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ciist.app.R;

/**
 * Created by xieke on 2016/1/25.
 * 提供了标题、内容以及图标和背景颜色的变换提示对话框
 * 标题和内容以String形式传入设置
 * 图标以资源Id的形式设置
 * 背景颜色以shape资源的形式设置。
 */
public class NotificationDialog extends AlertDialog{

    private Context mContext;
    /**
     * 标题和提示内容。控件和传入参数。。
     */
    private String mTitle,mContent;

    private int mIcon_res;  //图标资源
    private boolean mHasIcon = false;

    private int bgColor;  //背景颜色资源

    public NotificationDialog(Context context){
        super(context);
    }

    public NotificationDialog(Context context, int theme) {
        super(context, theme);
    }

    public NotificationDialog(Context context, String title, String content) {
        super(context);
        mContext = context;
        this.mTitle = title;
        this.mContent = content;
    }

    /**
     * 设置提示标题
     * @param title 标题内容
     */
    public void setTitle(String title){
        this.mTitle = title;
    }

    /**
     * 设置提示内容
     * @param content 提示内容
     */
    public void setContent(String content){
        this.mContent = content;
    }

    /**
     * 设置图标
     * @param hasIcon  是否有图标
     * @param iconRes   图标资源id
     */
    public void setIcon(boolean hasIcon,int iconRes){
        this.mHasIcon = hasIcon;
        this.mIcon_res = iconRes;
    }

    /**
     * 设置背景颜色
     * @param bg_color  颜色资源
     */
    public void setBgColor(int bg_color) {
        this.bgColor = bg_color;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_has_net_layout);

        findViewById(R.id.dialog_dismiss_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDialog.this.dismiss();
            }
        });

        findViewById(R.id.dialog_dismiss_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDialog.this.dismiss();
            }
        });

        initText();

    }

    /**
     * 设置数据
     */
    private void initText(){

        if (!(null == mTitle||"".equals(mTitle))){  //title判断
            TextView mTitleTxt = (TextView) findViewById(R.id.dialog_title_text);
            mTitleTxt.setText(mTitle);
        }

        if (!(null == mContent||"".equals(mContent))){  //Content判断
            TextView  mContentTxt = (TextView) findViewById(R.id.dialog_content_text);
            mContentTxt.setText(mContent);
        }

        if (mHasIcon){
            ImageView iconImg = (ImageView) findViewById(R.id.dialog_icon_img);
            iconImg.setVisibility(View.VISIBLE);
            iconImg.setImageResource(mIcon_res);
        }

        if (0 != bgColor){
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.dialog_parent_layout);
            //  layout.setBackgroundColor(bgColor);
            layout.setBackgroundResource(bgColor);
        }
    }
}
