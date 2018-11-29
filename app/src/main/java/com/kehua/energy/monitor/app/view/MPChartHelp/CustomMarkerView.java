package com.kehua.energy.monitor.app.view.MPChartHelp;

import android.content.Context;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.LineChartEntity;

/*
        * * 自定义标注样式
        * */
public class CustomMarkerView extends MarkerView {

    TextView tvContentX, tvContentY;

    private DecimalFormat format;

    List<LineChartEntity> oriData;

    String tagX, tagY;

    public CustomMarkerView(Context context, List<LineChartEntity> oriData, String tagX, String tagY) {
        super(context, R.layout.layout_linechar_marker);
        tvContentX = findViewById(R.id.tv_content_x);
        tvContentY = findViewById(R.id.tv_content_y);
        format = new DecimalFormat("###.00");
        this.oriData = oriData;
        this.tagX = tagX;
        this.tagY = tagY;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (oriData != null && oriData.size() > 0) {
            int index = -1;
            for (int i = 0; i < oriData.size(); i++) {
                if (e.getX() == oriData.get(i).getEntry().getX() && e.getY() == oriData.get(i).getEntry().getY()) {
                    index = i;
                    break;
                }
            }
            String contentX = index < 0 ? tagX : (tagX + index);
            contentX = contentX + ":" + (e.getX() == 0 ? "0" : format.format(e.getX()));

            String contentY = tagY + ":" + (e.getY() == 0 ? "0" : format.format(e.getY()));

            tvContentX.setText(contentX);
            tvContentY.setText(contentY);
        }

        super.refreshContent(e, highlight);
    }
}