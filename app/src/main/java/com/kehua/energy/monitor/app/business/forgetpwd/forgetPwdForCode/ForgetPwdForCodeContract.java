package com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForCode;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface ForgetPwdForCodeContract {

    interface View extends BaseView {

        void clearAllInput();

        void requestVerCodeOnClickAble(boolean clickAble);

        void updateRequestCodeText(String text);
    }

    abstract class Presenter extends BasePresenter<View> {

        abstract void verCode(String code);

        abstract void loadVerCode();

        abstract void countDown();
    }
}