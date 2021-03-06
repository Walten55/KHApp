package com.kehua.energy.monitor.app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerSlide extends ViewPager {
    //是否可以进行滑动
    private boolean isSlide = false;

    private CurrentPageCallBacker currentPageCallBacker;

    public void setSlide(boolean slide) {
        isSlide = slide;
    }

    public ViewPagerSlide(Context context) {
        super(context);
    }

    public ViewPagerSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //这个控件是哪个屌人写的  是不是傻？？   飞哥留。
        return isSlide;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
        if (currentPageCallBacker != null) {
            currentPageCallBacker.call(item);
        }
    }

    public void setCurrentPageCallBacker(CurrentPageCallBacker currentPageCallBacker) {
        this.currentPageCallBacker = currentPageCallBacker;
    }

    public interface CurrentPageCallBacker {
        void call(int item);
    }
}