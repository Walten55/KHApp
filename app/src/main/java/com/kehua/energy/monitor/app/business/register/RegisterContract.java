package com.kehua.energy.monitor.app.business.register;

import android.widget.CheckBox;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;
import me.walten.fastgo.widget.XEditText;

public interface RegisterContract {

    interface View extends BaseView {

        void requestVerCodeOnClickAble(boolean clickAble);

        void updateRequestCodeText(String text);

        void toMain();
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void loadVerCode(String phoneNum);

        abstract void countDown();

        abstract boolean checkPhone(String phoneNum);

        abstract boolean checkRegistParam(String pn, String email, String phoneNum, String code, String password, boolean agree);

        abstract void register(String pn, String email, String phoneNum, String code, String password, boolean agree);
    }
}