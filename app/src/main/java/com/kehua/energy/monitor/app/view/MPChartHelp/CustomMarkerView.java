package com.kehua.energy.monitor.app.view.MPChartHelp;

import android.content.Context;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.kehua.energy.monitor.app.R;

/*
        * * 自定义标注样式
        * */
public class CustomMarkerView extends MarkerView {

    TextView tvContentX, tvContentY;

    private DecimalFormat format;

    public CustomMarkerView(Context context) {
        super(context, R.layout.layout_linechar_marker);
        tvContentX = findViewById(R.id.tv_content_x);
        tvContentY = findViewById(R.id.tv_content_y);
        format = new DecimalFormat("###.00");
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContentX.setText(e.getX() == 0 ? "0" : format.format(e.getX()));
        tvContentY.setText(e.getY() == 0 ? "0" : format.format(e.getY()));
        super.refreshContent(e, highlight);
    }
}