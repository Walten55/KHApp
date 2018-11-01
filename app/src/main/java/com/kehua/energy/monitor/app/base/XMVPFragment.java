package com.kehua.energy.monitor.app.base;

import android.support.annotation.Nullable;

import com.kehua.energy.monitor.app.R;

import butterknife.BindView;
import me.walten.fastgo.base.fragment.MVPFragment;
import me.walten.fastgo.base.mvp.IPresenter;
import me.walten.fastgo.widget.titlebar.XTitleBar;

public abstract class XMVPFragment<T extends IPresenter> extends MVPFragment<T> {
    @Nullable
    @BindView(R.id.title_bar)
    public XTitleBar mTitleBar;
}