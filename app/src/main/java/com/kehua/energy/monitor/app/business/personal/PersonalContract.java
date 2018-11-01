package com.kehua.energy.monitor.app.business.personal;

import com.kehua.energy.monitor.app.model.entity.InvInfoList;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface PersonalContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        abstract public void loadPersonalMainInfo();

        abstract public void invinfo();

        abstract public void invinfo(Consumer<InvInfoList> consumer);
    }
}