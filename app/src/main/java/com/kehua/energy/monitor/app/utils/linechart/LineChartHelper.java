package com.kehua.energy.monitor.app.utils.linechart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.LineChartEntity;
import com.kehua.energy.monitor.app.model.entity.LineChartEntityList;
import com.kehua.energy.monitor.app.view.MPChartHelp.CustomMarkerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LineChartHelper {

    static LineChartHelper _Instance;

    private boolean touchEnabl = true;
    private boolean dragEnable = false;
    private boolean scaleEnable = false;
    private boolean pinchZoom = false;

    private boolean xAxisLineEnable = true;
    private float xAxisTextSize = 12f;
    private int xAxisTextColor = Color.DKGRAY;
    private boolean xAxisMinimumEnable = false;
    private float xAxisMinimum = 0;
    private float xAxisGranularity = 1f;
    private boolean xAxisGridLineEnable = true;
    private int xAxisLableCounts = 4;

    private boolean yLeftMaxSetted = false;
    private float yLeftMax = 200;
    private boolean yLeftMinSetted = false;
    private float yLeftMin = 0;
    private boolean yLeftAxisZeroLine = false;
    private boolean yLeftAxisLimitLinesBehindData = true;
    private float yLeftAxisGranularity = 1f;
    private boolean yLeftAxisGridLineEnable = false;

    private boolean dataIconEnable = false;
    private boolean dataCirclesEnable = true;
    private int dataCircleColor = Color.WHITE;
    private float dataCircleRadius = 2f;

    private boolean dataColorEnable = false;
    private int dataColor = Color.BLACK;
    private float lineWidth = 1f;

    private Drawable fillDrawable;
    private int fillColor = Color.TRANSPARENT;

    private boolean dataDrawValue = false;
    private String dataValueTagX = "";
    private String dataValueTagY = "";

    private boolean dataCircleHoleEnable = true;
    private float dataTextSize = 10f;
    private boolean dataDrawFillEnable = false;
    private float dataFormLineWidth = 1f;
    private float dataFormSize = 15f;

    private CustomMarkerView cusValueMarkerView;


    public static LineChartHelper init(Context context, LineChart lineChart) {
        _Instance = new LineChartHelper(context, lineChart);
        return _Instance;
    }

    public static LineChartHelper get() {
        return _Instance;
    }

    private Context context;
    private LineChart lineChart;

    public LineChartHelper(Context context, LineChart lineChart) {
        this.context = context;
        this.lineChart = lineChart;

        fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_blue);
        dataCircleColor = ContextCompat.getColor(context, R.color.btn_blue_nor);
        dataColorEnable = true;
        dataColor = ContextCompat.getColor(context, R.color.green);
        lineChartSet();
    }

    private void lineChartSet() {
        // no description text
        lineChart.getDescription().setEnabled(false);
        // enable touch gestures
        lineChart.setTouchEnabled(touchEnabl);

        // enable scaling and dragging
        lineChart.setDragEnabled(dragEnable);
        lineChart.setScaleEnabled(scaleEnable);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(pinchZoom);
//        lineChart.setBackground(ContextCompat.getDrawable(context, R.drawable.fade_blue));
        if (cusValueMarkerView != null) {
            lineChart.setMarker(cusValueMarkerView);
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(xAxisGridLineEnable);
        xAxis.setGranularity(xAxisGranularity);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(xAxisLineEnable);
        xAxis.enableGridDashedLine(10f, 0f, 0f);
        xAxis.setTextSize(xAxisTextSize);
        xAxis.setTextColor(xAxisTextColor);
        xAxis.setLabelCount(xAxisLableCounts);
        xAxis.setYOffset(24f);


        if (xAxisMinimumEnable) {
            xAxis.setAxisMinimum(xAxisMinimum);
        }
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to aLineChartHelper overlapping lines
        if (yLeftMaxSetted) {
            leftAxis.setAxisMaximum(yLeftMax);
        }
        if (yLeftMinSetted) {
            leftAxis.setAxisMinimum(yLeftMin);
        }
        leftAxis.setDrawGridLines(yLeftAxisGridLineEnable);
        leftAxis.setGranularity(yLeftAxisGranularity);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(yLeftAxisZeroLine);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(yLeftAxisLimitLinesBehindData);

        lineChart.getAxisRight().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        //legend.setForm(Legend.LegendForm.LINE);

        //数据容器设置集合
        LineDataSet set1;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(new ArrayList<Entry>(), "");
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);
            // set data
            lineChart.setData(lineData);
        }
        defaultDataSet(set1);

        refreshChart();
    }

    public void refreshChart() {
        if (lineChart == null) {
            return;
        }
        lineChart.getData().notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.getViewPortHandler().refresh(new Matrix(), lineChart, true);
    }


    /**
     * 设置数据
     */
    public LineChartHelper setData(List<LineChartEntity> lineChartEntities) {
        List<Entry> values = new ArrayList<>();
        XAxis xAxis = lineChart.getXAxis();
        YAxis yLeftAxis = lineChart.getAxisLeft();

        if (lineChartEntities != null && lineChartEntities.size() > 0) {
            final String xUnitStr = lineChartEntities.get(0).getXUnit();
            final String yUnitStr = lineChartEntities.get(0).getYUnit();

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return value + xUnitStr;
                }
            });

            yLeftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return value + yUnitStr;
                }
            });

            for (LineChartEntity lineChartEntity : lineChartEntities) {
                values.add(lineChartEntity.getEntry());
            }
        }


        if (!xAxisMinimumEnable) {
            float minX = values.get(0).getX();

            for (Entry entry : values) {
                minX = minX < entry.getX() ? minX : entry.getX();
            }
            xAxis.setAxisMinimum(minX);
        }

        LineDataSet set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
        set1.clear();
        set1.setValues(sortValues(values));

        lineChartSet();
        return this;
    }

    public LineChartHelper setData(LineChartEntityList... lineChartEntitieLists) {
        if (lineChartEntitieLists == null || lineChartEntitieLists.length < 1) {
            return this;
        }
        List<LineChartEntity> lineChartEntitys;
        List<Entry> values = new ArrayList<>();

        final String xUnitStr = lineChartEntitieLists[0].getData().get(0).getXUnit();
        final String yUnitStr = lineChartEntitieLists[0].getData().get(0).getYUnit();
        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + xUnitStr;
            }
        });
        lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value + yUnitStr;
            }
        });

        LineDataSet set;

        if (lineChart.getData().getDataSets().size() < lineChartEntitieLists.length) {
            for (int i = 0; i < lineChartEntitieLists.length - lineChart.getData().getDataSets().size(); i++) {
                set = new LineDataSet(new ArrayList<Entry>(), "");
                defaultDataSet(set);
                lineChart.getData().getDataSets().add(set);
            }
        }

        for (int i = 0; i < lineChartEntitieLists.length; i++) {
            if (lineChartEntitieLists[i].getData() != null && lineChartEntitieLists[i].getData().size() > 0) {
                set = (LineDataSet) lineChart.getLineData().getDataSetByIndex(i);
                set.setValues(lineChartEntitys2Entry(lineChartEntitieLists[i].getData()));
                set.setLabel(lineChartEntitieLists[i].getDes());
                set.setColor(lineChartEntitieLists[i].getColor());
            }
        }
        lineChart.getData().notifyDataChanged();
        lineChart.notifyDataSetChanged();

        return this;
    }

    public void setDeviceData(String[] lables, int[] colors, List<DeviceData>... deviceDataLists) {
        if (deviceDataLists == null || deviceDataLists.length < 1
                || colors == null || colors.length < 1 || lables == null || lables.length < 1) {
            return;
        }

        List<List<DeviceData>> targetLists = new ArrayList<>();
        for (int i = 0; i < deviceDataLists.length; i++) {
            if (deviceDataLists[i] != null && deviceDataLists[i].size() > 0) {
                targetLists.add(deviceDataLists[i]);
            }
        }
        if (targetLists.size() < 1) {
            return;
        }

        LineChartEntityList[] lineChartEntityLists = new LineChartEntityList[targetLists.size()];
        for (int i = 0; i < targetLists.size(); i++) {
            lineChartEntityLists[i] = new LineChartEntityList(lables[i % lables.length], colors[i % colors.length]
                    , deviceDatas2LineChartEntitys(targetLists.get(i)));
        }
        setData(lineChartEntityLists);
    }

    public void setDeviceData(List<DeviceData> deviceDataList) {
        if (deviceDataList == null || deviceDataList.size() < 1) {
            return;
        }
        List<LineChartEntity> data = deviceDatas2LineChartEntitys(deviceDataList);
        cusValueMarkerView = new CustomMarkerView(context, data, dataValueTagX, dataValueTagY);
        setData(data);
    }


    private void defaultDataSet(LineDataSet set) {
        set.setDrawValues(dataDrawValue);
        set.setDrawIcons(dataIconEnable);
        set.setDrawCircles(dataCirclesEnable);
        set.setCircleColor(dataCircleColor);
        set.setCircleRadius(dataCircleRadius);
        // set the line to be drawn like this "- - - - - -"
        //set1.enableDashedLine(10f, 0f, 0f);
        //set1.enableDashedHighlightLine(10f, 5f, 0f);
        if (dataColorEnable) {
            set.setColor(dataColor);
        }
        set.setLineWidth(lineWidth);

        set.setDrawCircleHole(dataCircleHoleEnable);
        set.setValueTextSize(dataTextSize);

        set.setFormLineWidth(dataFormLineWidth);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(dataFormSize);

        set.setDrawFilled(dataDrawFillEnable);
        if (Utils.getSDKInt() >= 18 && fillDrawable != null) {
            set.setFillDrawable(fillDrawable);
        } else {
            set.setFillColor(fillColor);
        }

//        set.setValueFormatter(new CusLineChartValueFormat(dataValueTag));
    }

    public LineChartHelper setTouchEnabl(boolean touchEnabl) {
        this.touchEnabl = touchEnabl;
        return this;
    }

    public LineChartHelper setDragEnable(boolean dragEnable) {
        this.dragEnable = dragEnable;
        return this;
    }

    public LineChartHelper setDataValueTagX(String dataValueTagX) {
        this.dataValueTagX = dataValueTagX;
        return this;
    }

    public LineChartHelper setDataValueTagY(String dataValueTagY) {
        this.dataValueTagY = dataValueTagY;
        return this;
    }

    public LineChartHelper setScaleEnable(boolean scaleEnable) {
        this.scaleEnable = scaleEnable;
        return this;
    }

    public LineChartHelper setPinchZoom(boolean pinchZoom) {
        this.pinchZoom = pinchZoom;
        return this;
    }

    public LineChartHelper setyLeftMaxSetted(boolean yLeftMaxSetted) {
        this.yLeftMaxSetted = yLeftMaxSetted;
        return this;
    }

    public LineChartHelper setyLeftMax(float yLeftMax) {
        this.yLeftMaxSetted = true;
        this.yLeftMax = yLeftMax;
        return this;
    }

    public LineChartHelper setyLeftMin(float yLeftMin) {
        this.yLeftMinSetted = true;
        this.yLeftMin = yLeftMin;
        return this;
    }

    public LineChartHelper setxAxisTextSize(float xAxisTextSize) {
        this.xAxisTextSize = xAxisTextSize;
        return this;
    }

    public LineChartHelper setxAxisTextColor(int xAxisTextColor) {
        this.xAxisTextColor = xAxisTextColor;
        return this;
    }

    public LineChartHelper setyLeftAxisZeroLine(boolean yLeftAxisZeroLine) {
        this.yLeftAxisZeroLine = yLeftAxisZeroLine;
        return this;
    }

    public LineChartHelper setyLeftAxisLimitLinesBehindData(boolean yLeftAxisLimitLinesBehindData) {
        this.yLeftAxisLimitLinesBehindData = yLeftAxisLimitLinesBehindData;
        return this;
    }

    public LineChartHelper setDataIconEnable(boolean dataIconEnable) {
        this.dataIconEnable = dataIconEnable;
        return this;
    }

    public LineChartHelper setDataDrawValue(boolean dataDrawValue) {
        this.dataDrawValue = dataDrawValue;
        return this;
    }

    public LineChartHelper setDataCircleColor(int dataCircleColor) {
        this.dataCircleColor = dataCircleColor;
        return this;
    }

    public LineChartHelper setDataCircleRadius(float dataCircleRadius) {
        this.dataCircleRadius = dataCircleRadius;
        return this;
    }

    public LineChartHelper setDataColorEnable(boolean dataColorEnable) {
        this.dataColorEnable = dataColorEnable;
        return this;
    }

    public LineChartHelper setDataColor(int dataColor) {
        this.dataColor = dataColor;
        return this;
    }

    public LineChartHelper setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }

    public LineChartHelper setFillDrawable(Drawable fillDrawable) {
        this.fillDrawable = fillDrawable;
        return this;
    }

    public LineChartHelper setFillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public LineChartHelper setDataCircleHoleEnable(boolean dataCircleHoleEnable) {
        this.dataCircleHoleEnable = dataCircleHoleEnable;
        return this;
    }

    public LineChartHelper setDataTextSize(float dataTextSize) {
        this.dataTextSize = dataTextSize;
        return this;
    }

    public LineChartHelper setDataDrawFillEnable(boolean dataDrawFillEnable) {
        this.dataDrawFillEnable = dataDrawFillEnable;
        return this;
    }

    public LineChartHelper setDataFormLineWidth(float dataFormLineWidth) {
        this.dataFormLineWidth = dataFormLineWidth;
        return this;
    }

    public LineChartHelper setDataFormSize(float dataFormSize) {
        this.dataFormSize = dataFormSize;
        return this;
    }

    public LineChartHelper setxAxisLineEnable(boolean xAxisLineEnable) {
        this.xAxisLineEnable = xAxisLineEnable;
        return this;
    }

    public LineChartHelper setxAxisMinimum(float xAxisMinimum) {
        this.xAxisMinimum = xAxisMinimum;
        return this;
    }

    public LineChartHelper setxAxisGranularity(float xAxisGranularity) {
        this.xAxisGranularity = xAxisGranularity;
        return this;
    }

    public LineChartHelper setxAxisLableCounts(int xAxisLableCounts) {
        this.xAxisLableCounts = xAxisLableCounts;
        return this;
    }

    public LineChartHelper setxAxisGridLineEnable(boolean xAxisGridLineEnable) {
        this.xAxisGridLineEnable = xAxisGridLineEnable;
        return this;
    }

    public LineChartHelper setyLeftAxisGranularity(float yLeftAxisGranularity) {
        this.yLeftAxisGranularity = yLeftAxisGranularity;
        return this;
    }

    public LineChartHelper setyLeftAxisGridLineEnable(boolean yLeftAxisGridLineEnable) {
        this.yLeftAxisGridLineEnable = yLeftAxisGridLineEnable;
        return this;
    }

    public LineChartHelper setCusValueMarkerView(CustomMarkerView cusValueMarkerView) {
        this.cusValueMarkerView = cusValueMarkerView;
        return this;
    }

    public void invalidate() {
        lineChartSet();
        lineChart.invalidate();
    }


    /**
     * 将设备模式列表信息转化成对应的图表数据列表信息
     */
    public List<LineChartEntity> deviceDatas2LineChartEntitys(List<DeviceData> deviceDataList) {
        List<LineChartEntity> lineChartEntityList = new ArrayList<>();
        if (deviceDataList != null && deviceDataList.size() > 0) {
            int targetCount = deviceDataList.size();
            if (targetCount % 2 != 0) {
                targetCount--;
            }
            for (int i = 0; i < targetCount; i += 2) {
                lineChartEntityList.add(new LineChartEntity(deviceDataList.get(i), deviceDataList.get(i + 1)));
            }
        }
        return lineChartEntityList;
    }

    /**
     * 将图表数据信息转化为图表坐标信息
     */
    public List<Entry> lineChartEntitys2Entry(List<LineChartEntity> lineChartEntityList) {
        List<Entry> entryList = new ArrayList<>();
        for (LineChartEntity lineChartEntity : lineChartEntityList) {
            entryList.add(lineChartEntity.getEntry());
        }
        return entryList;
    }

    public LineChart getLineChart() {
        return lineChart;
    }

    /**
     * 将结果排序
     */
    private List<Entry> sortValues(List<Entry> values) {
        Collections.sort(values, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return (int) (e1.getX() - e2.getX());
            }
        });
        return values;
    }

}
