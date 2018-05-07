package com.yibo.ymusic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.yibo.ymusic.utils.MyLog;

/**
 * Created by Administrator on 2018/5/3.
 */

public class ScrollRelativeLayout extends RelativeLayout{

    private Scroller mScroller;
    private int mIndicatorHeight;

    public ScrollRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View indicator = getChildAt(0);
        mIndicatorHeight = indicator.getMeasuredHeight();
        MyLog.d("indicator height",mIndicatorHeight+"");
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void hideIndicator(){
        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
        mScroller.startScroll(0,0,0,mIndicatorHeight,500);
    }

    public void showIndicator(){
        scrollTo(0,0);
        postInvalidate();
    }
}
