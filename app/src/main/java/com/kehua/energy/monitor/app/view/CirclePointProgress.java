package com.kehua.energy.monitor.app.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.blankj.utilcode.util.StringUtils;
import com.kehua.energy.monitor.app.R;

/**
 * 进度圆环
 */

public class CirclePointProgress extends View {

    private int width;
    private int height;

    private float centerX;
    private float centerY;

    private float startAngle = -90;

    private int barWidth = 25;

    private float mRadius = 60;

    private float paddingRect = 5;

    //圆环的画笔
    private Paint mRingPaint;
    //文字的画笔
    private Paint mTextPaint;
    //圆点内部画笔
    private Paint mPointPaint;

    private int progress = 35;

    private int dataBgColor = R.color.ring_green_bg;
    private int dataColor = R.color.ring_green_arrived;

    private RectF mArcRectF;

//    private Bitmap mPointBitmap;

    String text;

    public CirclePointProgress(Context context) {
        this(context, null);
    }

    public CirclePointProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePointProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CirclePointProgress);
        progress = ta.getInt(R.styleable.CirclePointProgress_progress, progress);
        progress = progress > 99 ? 100 : (progress % 100);
        text = ta.getString(R.styleable.CirclePointProgress_text);
        text = StringUtils.isTrimEmpty(text) ? "自用百分比" : text;
        init();
    }

    private void init() {
        //进度圆环的画笔
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);//画线
        mRingPaint.setStrokeWidth(barWidth);//画笔粗细
        //进度文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_black));
        //圆点画笔
        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);//画线
        mPointPaint.setColor(ContextCompat.getColor(getContext(), dataColor));
        mPointPaint.setAntiAlias(true);

//        mPointBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_icon_point);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        mRadius = (width < height ? width : height) * 1.0f / 2 - barWidth - paddingRect;
        mArcRectF = new RectF(centerX - mRadius, centerY - mRadius, centerX + mRadius, centerY + mRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //先画圆环
        mRingPaint.setColor(ContextCompat.getColor(getContext(), dataColor));
        float endAngle = progress < 100 ? (progress * 360.0f) / 100 : 360;
        //圆环背景
        mRingPaint.setColor(ContextCompat.getColor(getContext(), dataBgColor));
        canvas.drawArc(mArcRectF, 0, 360, false, mRingPaint);
        //圆环值
        mRingPaint.setColor(ContextCompat.getColor(getContext(), dataColor));
        canvas.drawArc(mArcRectF, startAngle, endAngle, false, mRingPaint);
        //写上文字
        mTextPaint.setTextSize(getResources().getDimensionPixelOffset(R.dimen.grid_32));
        String valueStr = new StringBuilder()
                .append(String.valueOf(progress))
                .append("%")
                .toString();
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(valueStr.toString(), 0, valueStr.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(valueStr, getMeasuredWidth() / 2 - bounds.width() / 1.8f, baseline - bounds.height() / 3, mTextPaint);

        mTextPaint.setTextSize(getResources().getDimensionPixelOffset(R.dimen.text_size_main));
        Rect boundsStr = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), boundsStr);
        canvas.drawText(text, getMeasuredWidth() / 2 - boundsStr.width() / 1.95f, baseline + boundsStr.height() * 1.5f, mTextPaint);

        //获取目标点
        float[] pointXY = getPointXY();
        canvas.drawCircle(pointXY[0], pointXY[1], barWidth / 2, mPointPaint);
//        canvas.drawBitmap(mPointBitmap, pointXY[0] - mPointBitmap.getWidth() / 2, pointXY[1] - mPointBitmap.getWidth() / 2, mPointPaint);
    }

    private float[] getPointXY() {
        float[] pointXY = new float[2];
        //先计算弧度
        double radian = (progress * 2 * Math.PI) / 100;
        pointXY[0] = (float) Math.abs(mRadius * Math.sin(radian) + centerX);
        pointXY[1] = (float) Math.abs(mRadius * Math.cos(radian) - centerY);

        return pointXY;
    }

    public int getProgress() {
        return progress;
    }

    @SuppressLint("ObjectAnimatorBinding")
    public void setProgress(int progress) {
        this.progress = progress > 99 ? 100 : progress % 100;
        invalidate();
    }

    public void animProgress(int progress) {
        if (this.progress == progress) {
            return;
        }
        progress = progress > 99 ? 100 : progress % 100;
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(this, "Progress", 0, progress);
        progressAnim.setDuration(3000);
        progressAnim.setInterpolator(new AccelerateInterpolator());
        progressAnim.start();
    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (mPointBitmap != null) {
//            mPointBitmap.recycle();
//        }
//    }

    public void setText(String text) {
        this.text = text;
    }
}
