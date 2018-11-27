package com.kehua.energy.monitor.app.business.local.setting.pattern;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kehua.energy.monitor.app.utils.LineChartHelper;

/**
 * SegmentTabLayout 点击响应监听器
 * Created by linyixian on 2018/11/26.
 */

public abstract class CustomSegTouchListener implements OnTabSelectListener {

    LineChartHelper lineChartHelper;

    public CustomSegTouchListener(LineChartHelper lineChartHelper) {
        this.lineChartHelper = lineChartHelper;
    }


    public abstract void onTabSelect(int position, LineChartHelper lineChartHelper);

    @Override
    public void onTabSelect(int position) {
        onTabSelect(position, lineChartHelper);
    }

    public abstract void onTabReselect(int position, LineChartHelper lineChartHelper);

    @Override
    public void onTabReselect(int position) {
        onTabReselect(position, lineChartHelper);
    }
}
