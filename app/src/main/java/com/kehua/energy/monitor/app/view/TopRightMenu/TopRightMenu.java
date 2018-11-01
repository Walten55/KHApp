package com.kehua.energy.monitor.app.view.TopRightMenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.entity.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 右上方菜单栏
 */
public class TopRightMenu {
    private Activity mContext;
    private PopupWindow mPopupWindow;
    private RecyclerView mRecyclerView;
    private LinearLayout contentLayout;

    OnDismissListener mOnDismissListener;

    private TRMenuAdapter mAdapter;
    private List<MenuItem> menuItemList;

    private static final int DEFAULT_HEIGHT = 480;
    private int popHeight = RecyclerView.LayoutParams.WRAP_CONTENT;
    private int popWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
    private boolean dimBackground = true;

    private float alpha = 0.75f;

    public TopRightMenu(Activity context) {
        this.mContext = context;
        init();
    }

    private void init() {
        contentLayout = new LinearLayout(mContext);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setGravity(Gravity.RIGHT);

        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams ivLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setImageResource(R.drawable.trangle_white);
//        imageView.setImageResource(R.mipmap.triangle);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        int margin = (int) mContext.getResources().getDimension(R.dimen.grid_5);
        ivLayoutParam.setMargins(0, 0, ConvertUtils.dp2px(mContext.getResources().getDimension(R.dimen.grid_3)), 0);
        contentLayout.addView(imageView, ivLayoutParam);

        mRecyclerView = new RecyclerView(mContext);
        LinearLayout.LayoutParams rvLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rvLayoutParam.setMargins(0, 0, 0, 0);
//        mRecyclerView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.trm_popup_bg));
        mRecyclerView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rectangle_radius_white));
        contentLayout.addView(mRecyclerView, rvLayoutParam);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        menuItemList = new ArrayList<>();

        mAdapter = new TRMenuAdapter(menuItemList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private PopupWindow getPopupWindow() {
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setContentView(contentLayout);
        mPopupWindow.setHeight(popHeight);
        mPopupWindow.setWidth(popWidth);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dimBackground) {
                    setBackgroundAlpha(alpha, 1f, 300);
                }
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
            }
        });

        if (mAdapter == null) {
            mAdapter = new TRMenuAdapter(menuItemList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNewData(menuItemList);
        }

        return mPopupWindow;
    }

    public TopRightMenu setHeight(int height) {
        if (height <= 0 && height != RecyclerView.LayoutParams.MATCH_PARENT
                && height != RecyclerView.LayoutParams.WRAP_CONTENT) {
            this.popHeight = DEFAULT_HEIGHT;
        } else {
            this.popHeight = height;
        }
        return this;
    }

    public TopRightMenu setWidth(int width) {
        if (width <= 0 && width != RecyclerView.LayoutParams.MATCH_PARENT) {
            this.popWidth = RecyclerView.LayoutParams.WRAP_CONTENT;
        } else {
            this.popWidth = width;
        }
        return this;
    }

    /**
     * 重新放置状态菜单
     *
     * @param list
     */
    public TopRightMenu setMenuList(List<MenuItem> list) {
        menuItemList = list;
        mAdapter.setNewData(menuItemList);
        return this;
    }

    /**
     * 添加单个菜单
     *
     * @param item
     * @return
     */
    public TopRightMenu addMenuItem(MenuItem item) {
        menuItemList.add(item);
        mAdapter.notifyDataSetChanged();
        return this;
    }

    /**
     * 添加多个菜单
     *
     * @param list
     * @return
     */
    public TopRightMenu addMenuList(List<MenuItem> list) {
        menuItemList.addAll(list);
        mAdapter.notifyDataSetChanged();
        return this;
    }

    /**
     * 是否让背景变暗
     *
     * @param b
     * @return
     */
    public TopRightMenu dimBackground(boolean b) {
        this.dimBackground = b;
        return this;
    }

    public TopRightMenu setItemOnclickListener(@Nullable BaseQuickAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
        return this;
    }

    public TopRightMenu showAsDropDown(View anchor) {
        showAsDropDown(anchor, 0, 0);
        return this;
    }

    public TopRightMenu showAsDropDown(View anchor, int xoff, int yoff) {
        if (mPopupWindow == null) {
            getPopupWindow();
        }
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(anchor, xoff, yoff);
            if (dimBackground) {
                setBackgroundAlpha(1f, alpha, 240);
            }
        }
        return this;
    }

    private void setBackgroundAlpha(float from, float to, int duration) {
        final WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lp.alpha = (float) animation.getAnimatedValue();
                mContext.getWindow().setAttributes(lp);
            }
        });
        animator.start();
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isMenuShow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public TopRightMenu setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
        return this;
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
