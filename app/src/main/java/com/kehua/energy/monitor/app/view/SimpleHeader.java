package com.kehua.energy.monitor.app.view;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.view.SmartView.ClassicsHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import me.walten.fastgo.common.Fastgo;

/**
 *
 */

public class SimpleHeader extends ClassicsHeader {
    public SimpleHeader(Context context) {
        super(context);
        init();
    }

    public SimpleHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setBackgroundColor(ContextCompat.getColor(Fastgo.getContext(), R.color.transparent));
        int textColor = Color.parseColor("#bdbdbd");
        mTitleText.setTextColor(textColor);
        mLastUpdateText.setTextColor(textColor);
        mProgressDrawable.setColor(textColor);
        mProgressView.setImageDrawable(mProgressDrawable);
        mArrowDrawable.setColor(textColor);
        mArrowView.setImageDrawable(mArrowDrawable);

        mLastUpdateText.setVisibility(View.GONE);
    }


    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        super.onStateChanged(refreshLayout, oldState, newState);
        mLastUpdateText.setVisibility(View.GONE);
    }
}
