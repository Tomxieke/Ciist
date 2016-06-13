package com.ciist.util;

import java.util.Calendar;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.ciist.app.R;

/**
 * 刷新控制view
 *
 * @ZHN
 */
public class RefreshableView extends LinearLayout {

    private static final int Defaut = 0;
    private static final int XialaState = 1;
    private static final int ZhengzaiState = 2;
    private static final int SucceseState = 3;

    private static final String TAG = "LILITH";
    private Scroller scroller;
    private View refreshView;
    private int refreshTargetTop = -120;
    private ProgressBar bar;
    private TextView downTextView;
    private RefreshListener refreshListener;

    private ImageView shuaxin_jiantou;

    // private Long refreshTime = null;
    private int lastX;
    private int lastY;
    // 拉动标记
    private boolean isDragging = false;
    // 是否可刷新标记
    private boolean isRefreshEnabled = true;
    // 在刷新中标记
    private boolean isRefreshing = false;
    private RelativeLayout linear, shuaxinchenggong_rel;
    Calendar LastRefreshTime;

    private Context mContext;

    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Defaut:
                    downTextView.setVisibility(View.GONE);
                    linear.setVisibility(View.GONE);
                    shuaxinchenggong_rel.setVisibility(View.GONE);
                    shuaxin_jiantou.setVisibility(View.GONE);
                    scroller.startScroll(0, gettopMargin(), 0, refreshTargetTop);
                    invalidate();
                    break;

                case XialaState:
                    downTextView.setVisibility(View.VISIBLE);
                    shuaxin_jiantou.setVisibility(VISIBLE);
                    linear.setVisibility(View.GONE);
                    shuaxinchenggong_rel.setVisibility(View.GONE);

                    break;

                case ZhengzaiState:
                    linear.setVisibility(View.VISIBLE);
                    downTextView.setVisibility(View.GONE);
                    shuaxin_jiantou.setVisibility(View.GONE);
                    shuaxinchenggong_rel.setVisibility(View.GONE);

                    break;

                case SucceseState:
                    downTextView.setVisibility(View.GONE);
                    linear.setVisibility(View.GONE);
                    shuaxin_jiantou.setVisibility(View.GONE);
                    shuaxinchenggong_rel.setVisibility(View.VISIBLE);

                    break;

            }

        };

    };

    public RefreshableView(Context context) {
        super(context);
        mContext = context;

    }

    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();

    }

    private void init() {
        // TODO Auto-generated method stub
        // 滑动对象，
        LastRefreshTime = Calendar.getInstance();
        scroller = new Scroller(mContext);

        // 刷新视图顶端的的view
        refreshView = LayoutInflater.from(mContext).inflate(
                R.layout.refresh_top_item, null);

        // 刷新bar
        shuaxin_jiantou = (ImageView) refreshView.findViewById(R.id.shuaxin_jiantou);
        bar = (ProgressBar) refreshView.findViewById(R.id.progress);
        // 下拉显示text
        downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);

        linear = (RelativeLayout) refreshView.findViewById(R.id.shuaxin_linear);
        shuaxinchenggong_rel = (RelativeLayout) refreshView
                .findViewById(R.id.shuaxinchenggong_rel);
        // 下来显示时间

        LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, -refreshTargetTop);
        lp.topMargin = refreshTargetTop;
        lp.gravity = Gravity.CENTER;
        addView(refreshView, lp);
    }

    /**
     * 设置上次刷新时间
     */
    private void setLastRefreshTimeText() {
        // TODO Auto-generated method stub
        Calendar NowTime = Calendar.getInstance();
        long l = NowTime.getTimeInMillis() - LastRefreshTime.getTimeInMillis();
        int days = new Long(l / (1000 * 60 * 60 * 24)).intValue();
        int hour = new Long(l / (1000 * 60 * 60)).intValue();
        int min = new Long(l / (1000 * 60)).intValue();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录下y坐标
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                // y移动坐标
                int m = y - lastY;
                if (((m < 6) && (m > -1)) || (!isDragging)) {
                    setLastRefreshTimeText();
                    doMovement(m);
                }
                // 记录下此刻y坐标
                this.lastY = y;
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");

                fling();

                break;
        }
        return true;
    }

    /**
     * up事件处理
     */
    private void fling() {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = (LayoutParams) refreshView
                .getLayoutParams();
        Log.i(TAG, "fling()" + lp.topMargin);
        if (lp.topMargin > 0) {// 拉到了触发可刷新事件
            refresh();
        } else {
            returnInitState();
        }
    }

    private void returnInitState() {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
                .getLayoutParams();
        int i = lp.topMargin;
        scroller.startScroll(0, i, 0, refreshTargetTop);
        invalidate();
    }

    private void refresh() {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
                .getLayoutParams();
        int i = lp.topMargin;
        mhandler.sendEmptyMessage(ZhengzaiState);
        scroller.startScroll(0, i, 0, 0 - i);
        invalidate();
        if (refreshListener != null) {
            refreshListener.onRefresh(this);
            isRefreshing = true;
        }
    }

    /**
     *
     */
    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (scroller.computeScrollOffset()) {
            int i = this.scroller.getCurrY();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
                    .getLayoutParams();
            int k = Math.max(i, refreshTargetTop);
            lp.topMargin = k;
            this.refreshView.setLayoutParams(lp);
            this.refreshView.invalidate();
            invalidate();
        }
    }

    /**
     * 下拉move事件处理
     */
    private void doMovement(int moveY) {
        // TODO Auto-generated method stub
        LinearLayout.LayoutParams lp = (LayoutParams) refreshView
                .getLayoutParams();
        if (moveY > 0) {
            // 获取view的上边距
            float f1 = lp.topMargin;
            float f2 = moveY * 0.3F;
            int i = (int) (f1 + f2);
            // 修改上边距
            lp.topMargin = i;
            // 修改后刷新
            refreshView.setLayoutParams(lp);
            refreshView.invalidate();
            invalidate();
        } else {
            float f1 = lp.topMargin;
            int i = (int) (f1 + moveY * 0.9F);
            Log.i("aa", String.valueOf(i));
            if (i >= refreshTargetTop) {
                lp.topMargin = i;
                // 修改后刷新
                refreshView.setLayoutParams(lp);
                refreshView.invalidate();
                invalidate();
            } else {

            }
        }

        mhandler.sendEmptyMessage(XialaState);
        if (lp.topMargin > 0) {
            downTextView.setText("释放可刷新");
            shuaxin_jiantou.setImageResource(R.drawable.ciist_shuaxin_jiantou);
        } else {

            downTextView.setText("下拉可刷新");
            shuaxin_jiantou.setImageResource(R.drawable.ciist_shuaxin_jiantou_down);
        }

    }

    public void setRefreshEnabled(boolean b) {
        this.isRefreshEnabled = b;
    }

    public void setRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }

    int gettopMargin() {

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
                .getLayoutParams();
        int i = lp.topMargin;
        return i;

    }

    /**
     * 结束刷新事件
     */
    public void finishRefresh() {
        Log.i(TAG, "执行了=====finishRefresh");
        scroller.startScroll(0, gettopMargin(), 0, gettopMargin() * -1);
        mhandler.sendEmptyMessage(SucceseState);
        mhandler.sendEmptyMessageDelayed(Defaut, 1000);
        invalidate();
        LastRefreshTime = Calendar.getInstance();

        Handler mhandler = new Handler() {
            public void handleMessage(Message msg) {

            };
        };

    }

    /*
     * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
     *
     * @see
     * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        int action = e.getAction();
        int y = (int) e.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                // y移动坐标
                int m = y - lastY;

                // 记录下此刻y坐标
                this.lastY = y;
                if (m > 6 && canScroll()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return false;
    }

    private boolean canScroll() {
        // TODO Auto-generated method stub
        View childView;
        if (getChildCount() > 1) {
            childView = this.getChildAt(1);
            if (childView instanceof ListView) {
                int top = ((ListView) childView).getChildAt(0).getTop();
                int pad = ((ListView) childView).getListPaddingTop();
                if ((Math.abs(top - pad)) < 3
                        && ((ListView) childView).getFirstVisiblePosition() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (childView instanceof ScrollView) {
                if (((ScrollView) childView).getScrollY() == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    /**
     * 刷新监听接口
     */
    public interface RefreshListener {
        public void onRefresh(RefreshableView view);
    }

}
