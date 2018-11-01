package com.kehua.energy.monitor.app.configuration;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideApp
                .with(context)
                .load(path)
                .into(imageView);
    }
}