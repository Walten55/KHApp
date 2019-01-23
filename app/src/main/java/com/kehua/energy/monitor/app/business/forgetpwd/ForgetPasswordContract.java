package com.kehua.energy.monitor.app.business.forgetpwd;

import android.widget.TextView;

import com.flyco.roundview.RoundTextView;

import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface ForgetPasswordContract {

    interface View extends BaseView {

        void toCodeFragment(Object object);

        void toNewPasswordFragment(Object object);


    }

    abstract class Presenter extends BasePresenter<View> {


    }
}