package com.kehua.energy.monitor.app.business.forgetpwd;

import android.widget.TextView;

import com.flyco.roundview.RoundTextView;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface ForgetPasswordContract {

    interface View extends BaseView {

        void requestVerCodeOnClickAble(boolean clickAble);

        void updateRequestCodeText(String text);

        void toMain();
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void saveLocalAccount(String account);

        abstract void loadVerCode();

        abstract void countDown();

        abstract void updatePassword(String account, String verCode, String newPwd, String confirmPwd);

        abstract boolean checkUpdateParam(String account, String verCode, String newPwd, String confirmPwd);
    }
}