package com.kehua.energy.monitor.app.utils;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import me.walten.fastgo.common.Fastgo;

public class ViewUtils {

    public static void setDrawableTop(TextView attention, int drawableId) {
        Drawable drawable = Fastgo.getContext().getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        attention.setCompoundDrawables(null, drawable, null, null);
    }
}
