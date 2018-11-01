package com.kehua.energy.monitor.app.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.kehua.energy.monitor.app.R;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import me.walten.fastgo.common.Fastgo;


/**
 *
 */

public class SimpleFooter extends ClassicsFooter {
    public SimpleFooter(Context context) {
        super(context);
        init();
    }

    public SimpleFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setBackgroundColor(ContextCompat.getColor(Fastgo.getContext(), R.color.transparent));
        int textColor = Color.parseColor("#bdbdbd");
        mTitleText.setTextColor(textColor);
        mTitleText.setText("加载更多");
        mProgressDrawable.setColor(textColor);
        mProgressView.setImageDrawable(mProgressDrawable);
        mArrowDrawable.setColor(textColor);
        mArrowView.setImageDrawable(mArrowDrawable);
    }

}
