package com.kehua.energy.monitor.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walten on 2017/4/13.
 * 动态流图
 */

public class EnergyFlowChart extends RelativeLayout {

    //流动方向
    public static final int FLOW_DIRECTION_TL_IN = 0;
    public static final int FLOW_DIRECTION_TL_OUT = 1;
    public static final int FLOW_DIRECTION_BL_IN = 2;
    public static final int FLOW_DIRECTION_BL_OUT = 3;
    public static final int FLOW_DIRECTION_TR_IN = 4;
    public static final int FLOW_DIRECTION_TR_OUT = 5;
    public static final int FLOW_DIRECTION_BR_IN = 6;
    public static final int FLOW_DIRECTION_BR_OUT = 7;
    public static final int CHILD_TL = 0;
    public static final int CHILD_BL = 1;
    public static final int CHILD_TR = 2;
    public static final int CHILD_BR = 3;

    private List<Path> mPaths = new ArrayList<>();
    private List<PathView> mPathViews = new ArrayList<>();
    private List<View> mChilds = new ArrayList<>();

    private View mTLChild;
    private View mBLChild;
    private View mTRChild;
    private View mBRChild;
    private View mCenterChild;

    private TextView mTLTextView;
    private TextView mTRTextView;
    private TextView mBLTextView;
    private TextView mBRTextView;

    private Paint mPaint;

    private float leftX1;
    private float leftX2;
    private float topY1;
    private float topY2;
    private float bTopY1;
    private float bTopY2;
    private float centerOffset = 10;

    private boolean isFirst = true;

    private int lineColor = Color.parseColor("#A1ABC2");

    public EnergyFlowChart(Context context) {
        this(context, null);
    }

    public EnergyFlowChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnergyFlowChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init();
        initView();
    }

    public void reset() {
        stopAll();
        isFirst = true;
        for (PathView v : mPathViews){
            removeView(v);
        }
        mPaths.clear();
        mPathViews.clear();
        drawPath(null);
    }

    private void init() {
        mPaint = new Paint();// 创建画笔
        mPaint.setColor(lineColor);// 画笔颜色 - 黑色
        mPaint.setStyle(Paint.Style.STROKE);// 填充模式 - 描边
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
        mPaint.setPathEffect(new DashPathEffect(new float[]{20f, 10f, 5f}, 0));
    }

    private void initView() {
        mTLChild = LayoutInflater.from(getContext()).inflate(R.layout.energy_flow_chart_child_left, null);
        mBLChild = LayoutInflater.from(getContext()).inflate(R.layout.energy_flow_chart_child_left, null);
        mTRChild = LayoutInflater.from(getContext()).inflate(R.layout.energy_flow_chart_child_right, null);
        mBRChild = LayoutInflater.from(getContext()).inflate(R.layout.energy_flow_chart_child_right, null);
        mCenterChild = LayoutInflater.from(getContext()).inflate(R.layout.energy_flow_chart_child_center, null);

        addView(mTLChild);
        mChilds.add(mTLChild);

        RelativeLayout.LayoutParams blParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        blParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBLChild, blParams);
        mChilds.add(mBLChild);

        RelativeLayout.LayoutParams trParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        trParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(mTRChild, trParams);
        mChilds.add(mTRChild);

        RelativeLayout.LayoutParams brParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        brParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        brParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(mBRChild, brParams);
        mChilds.add(mBRChild);

        RelativeLayout.LayoutParams cParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        cParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mCenterChild, cParams);

        mTLTextView = (TextView) mTLChild.findViewById(R.id.tv_value);
        mTRTextView = (TextView) mTRChild.findViewById(R.id.tv_value);
        mBLTextView = (TextView) mBLChild.findViewById(R.id.tv_value);
        mBRTextView = (TextView) mBRChild.findViewById(R.id.tv_value);

        ((TextView) mTRChild.findViewById(R.id.tv_name)).setText(LocalUserManager.getPn()== Frame.单相协议?R.string.电网功率_W:R.string.电网功率_Kw);
        ((TextView) mTLChild.findViewById(R.id.tv_name)).setText(LocalUserManager.getPn()== Frame.单相协议?R.string.发电功率_W:R.string.发电功率_Kw);
        ((TextView) mBRChild.findViewById(R.id.tv_name)).setText(LocalUserManager.getPn()== Frame.单相协议?R.string.主次负载_W:R.string.主次负载_Kw);
        ((TextView) mBLChild.findViewById(R.id.tv_name)).setText(LocalUserManager.getPn()== Frame.单相协议?R.string.电池功率_W:R.string.电池功率_Kw);

        ((ImageView) mTRChild.findViewById(R.id.iv_img)).setImageResource(R.mipmap.icon_station_grid);
        ((ImageView) mTLChild.findViewById(R.id.iv_img)).setImageResource(R.mipmap.icon_station_electricity);
        ((ImageView) mBRChild.findViewById(R.id.iv_img)).setImageResource(R.mipmap.icon_station_use);
        ((ImageView) mBLChild.findViewById(R.id.iv_img)).setImageResource(R.mipmap.icon_station_battery);
    }

    private Path setupPath(Path mPath) {
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(mPath, false);
        Path path = new Path();
        path.rLineTo(0, 0);
        pathMeasure.getSegment(0, pathMeasure.getLength(), path, true);
        return path;
    }

    //画静止路径
    private void drawPath(Canvas canvas) {
        if (isFirst) {

            leftX1 = mTLChild.getWidth() / 6.0f;
            leftX2 = getWidth() - mTLChild.getWidth() / 6.0f;

            topY1 = mTLChild.getHeight() + (mTLChild.getHeight() / 4.0f);
            topY2 = topY1 + mCenterChild.getHeight() * 3 / 4.0f;

            bTopY1 = getHeight() - mTLChild.getHeight() - (mTLChild.getHeight() / 4.0f);
            bTopY2 = bTopY1 - mCenterChild.getHeight() * 3 / 4.0f;

            //左上进
            Path path1 = new Path();
            path1.moveTo(leftX1, topY1);
            path1.lineTo(leftX1, topY2);
            path1.lineTo(getWidth() / 2.0f-centerOffset, topY2);
            path1.lineTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);
            mPaths.add(path1);
            addView(getPathView(setupPath(path1)), getChildCount() - 2);

            //左上出
            Path path2 = new Path();
            path2.moveTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);
            path2.lineTo(getWidth() / 2.0f-centerOffset, topY2);
            path2.lineTo(leftX1, topY2);
            path2.lineTo(leftX1, topY1);
            mPaths.add(path2);
            addView(getPathView(setupPath(path2)), getChildCount() - 2);

            //左下进
            Path path3 = new Path();
            path3.moveTo(leftX1, bTopY1);
            path3.lineTo(leftX1, bTopY2);
            path3.lineTo(getWidth() / 2.0f-centerOffset, bTopY2);
            path3.lineTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);
            mPaths.add(path3);
            addView(getPathView(setupPath(path3)), getChildCount() - 2);

            //左下出
            Path path4 = new Path();
            path4.moveTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);
            path4.lineTo(getWidth() / 2.0f-centerOffset, bTopY2);
            path4.lineTo(leftX1, bTopY2);
            path4.lineTo(leftX1, bTopY1);
            mPaths.add(path4);
            addView(getPathView(setupPath(path4)), getChildCount() - 2);

            //右上进
            Path path5 = new Path();
            path5.moveTo(leftX2, topY1);
            path5.lineTo(leftX2, topY2);
            path5.lineTo(getWidth() / 2.0f+centerOffset, topY2);
            path5.lineTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);
            mPaths.add(path5);
            addView(getPathView(setupPath(path5)), getChildCount() - 2);

            //右上出
            Path path6 = new Path();
            path6.moveTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);
            path6.lineTo(getWidth() / 2.0f+centerOffset, topY2);
            path6.lineTo(leftX2, topY2);
            path6.lineTo(leftX2, topY1);
            mPaths.add(path6);
            addView(getPathView(setupPath(path6)), getChildCount() - 2);

            //右下进
            Path path7 = new Path();
            path7.moveTo(leftX2, bTopY1);
            path7.lineTo(leftX2, bTopY2);
            path7.lineTo(getWidth() / 2.0f+centerOffset, bTopY2);
            path7.lineTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);
            mPaths.add(path7);
            addView(getPathView(setupPath(path7)), getChildCount() - 2);

            //右下出
            Path path8 = new Path();
            path8.moveTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);
            path8.lineTo(getWidth() / 2.0f+centerOffset, bTopY2);
            path8.lineTo(leftX2, bTopY2);
            path8.lineTo(leftX2, bTopY1);
            mPaths.add(path8);
            addView(getPathView(setupPath(path8)), getChildCount() - 2);

            isFirst = false;
        }

        Path path = new Path();
        path.moveTo(leftX1, topY1);
        path.lineTo(leftX1, topY2);
        path.lineTo(getWidth() / 2.0f-centerOffset, topY2);
        path.lineTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);

        path.moveTo(leftX1, bTopY1);
        path.lineTo(leftX1, bTopY2);
        path.lineTo(getWidth() / 2.0f-centerOffset, bTopY2);
        path.lineTo(getWidth() / 2.0f-centerOffset, getHeight() / 2.0f);

        path.moveTo(leftX2, topY1);
        path.lineTo(leftX2, topY2);
        path.lineTo(getWidth() / 2.0f+centerOffset, topY2);
        path.lineTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);

        path.moveTo(leftX2, bTopY1);
        path.lineTo(leftX2, bTopY2);
        path.lineTo(getWidth() / 2.0f+centerOffset, bTopY2);
        path.lineTo(getWidth() / 2.0f+centerOffset, getHeight() / 2.0f);

        if (canvas != null)
            canvas.drawPath(path, mPaint);
    }

    private PathView getPathView(Path path) {
        PathView pathView = new PathView(getContext());
        pathView.setPath(path);
        pathView.setLineWidth(6);
        pathView.setDuration(1800);
        pathView.setMode(PathView.MODE_AIRPLANE);
        pathView.setDarkLineColorRes(R.color.transparent);
        pathView.setLightLineColorRes(R.color.colorPrimary);
        pathView.setRepeat(true);
        mPathViews.add(pathView);
        return pathView;
    }

    public void start(int index) {
        mPathViews.get(index).start();
    }

    public void stopAll() {
        for (PathView v : mPathViews) {
            v.stop();
        }
    }

    public void releaseAll() {
        for (PathView v : mPathViews) {
            v.release();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawPath(canvas);
    }

    public TextView getTLTextView() {
        return mTLTextView;
    }

    public TextView getTRTextView() {
        return mTRTextView;
    }

    public TextView getBLTextView() {
        return mBLTextView;
    }

    public TextView getBRTextView() {
        return mBRTextView;
    }

    public void disable(int index) {
        View view = mChilds.get(index);
        ImageView imgIv = view.findViewById(R.id.iv_img);
        TextView nameTv = view.findViewById(R.id.tv_name);
        TextView valueTv = view.findViewById(R.id.tv_value);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imgIv.setColorFilter(filter);

        nameTv.setVisibility(View.INVISIBLE);
        valueTv.setVisibility(View.INVISIBLE);
    }

    public void able(int index) {
        View view = mChilds.get(index);
        ImageView imgIv = view.findViewById(R.id.iv_img);
        TextView nameTv = view.findViewById(R.id.tv_name);
        TextView valueTv = view.findViewById(R.id.tv_value);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(1);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imgIv.setColorFilter(filter);

        nameTv.setVisibility(View.VISIBLE);
        valueTv.setVisibility(View.VISIBLE);
    }
}
