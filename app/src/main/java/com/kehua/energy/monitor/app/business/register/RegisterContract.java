package com.kehua.energy.monitor.app.business.register;

import android.widget.CheckBox;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;
import me.walten.fastgo.widget.XEditText;

public interface RegisterContract {

    interface View extends BaseView {

        void toMain();
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract boolean checkRegistParam(String nickName, String account, String password,boolean agree);

        abstract void register(String nickName,String account,String password,boolean agree);
    }
}