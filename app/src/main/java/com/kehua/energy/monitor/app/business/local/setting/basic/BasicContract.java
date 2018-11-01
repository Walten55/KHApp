package com.kehua.energy.monitor.app.business.local.setting.basic;

import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface BasicContract {

    interface View extends BaseView {
        void onClickWorkPattern(android.view.View view);

        void onClickSystemTimeSetting(android.view.View view);

        void onClickAddChargeTime(android.view.View view);

        void onClickAddDischargeTime(android.view.View view);

        void onClickSubmitTimeFrame(android.view.View view);

        void addTimeFrame(LinearLayout container);

        void onClickDelete(android.view.View view);

        void onUpdateData(Object o);

        void initTimeFrameView(int count,LinearLayout container);

        void initTimeFrameView(LinearLayout container);

        List<String> getTimeFrameList(LinearLayout container,List<String> slotList);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();

        public abstract void power(boolean open, Consumer<Boolean> consumer);

        public abstract void setWorkPattern(int mode, Consumer<Boolean> consumer);

        public abstract void setSystemTime(Date date, Consumer<Boolean> consumer);

        public abstract void setTimeFrame(List<String> chargeTimeFrames,List<String> dischargeTimeFrames, Consumer<Boolean> consumer);

    }
}