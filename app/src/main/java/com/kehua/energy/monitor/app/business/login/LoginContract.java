package com.kehua.energy.monitor.app.business.login;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface LoginContract {

    interface View extends BaseView {


    }

    abstract class Presenter extends BasePresenter<View> {
        abstract boolean checkLoginParam(String account, String password);

        abstract void login(String account, String password);

        abstract void loadPlatforms();

        abstract void qqAuthor();

        abstract void weChatAuthor();

    }
}