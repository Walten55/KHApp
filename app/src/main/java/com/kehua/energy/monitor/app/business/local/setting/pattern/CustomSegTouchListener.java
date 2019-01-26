package com.kehua.energy.monitor.app.business.local.setting.pattern;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kehua.energy.monitor.app.model.entity.PatternEntity;
import com.kehua.energy.monitor.app.utils.linechart.LineChartHelper;

/**
 * SegmentTabLayout 点击响应监听器
 * Created by linyixian on 2018/11/26.
 */

public abstract class CustomSegTouchListener implements OnTabSelectListener {

    LineChartHelper lineChartHelper;

    PatternEntity patternEntity;

    public CustomSegTouchListener(LineChartHelper lineChartHelper, PatternEntity patternEntity) {
        this.lineChartHelper = lineChartHelper;
        this.patternEntity = patternEntity;
    }


    public abstract void onTabSelect(int position, LineChartHelper lineChartHelper, PatternEntity patternEntity);

    @Override
    public void onTabSelect(int position) {
        onTabSelect(position, lineChartHelper, patternEntity);
    }

    public abstract void onTabReselect(int position, LineChartHelper lineChartHelper, PatternEntity patternEntity);

    @Override
    public void onTabReselect(int position) {
        onTabReselect(position, lineChartHelper, patternEntity);
    }
}
