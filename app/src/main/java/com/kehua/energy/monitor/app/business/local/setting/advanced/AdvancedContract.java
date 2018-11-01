package com.kehua.energy.monitor.app.business.local.setting.advanced;

import com.flyco.dialog.listener.OnBtnClickL;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface AdvancedContract {

    interface View extends BaseView {
        void onSetupData(List<SettingEntity> data);
        void onUpdateData(Object o);
        void showTipDialog(String title,String content,final OnBtnClickL onBtnClickL);
        void onStandardChoose(Standard standard);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setupData();
        public abstract void toggle(int address,boolean open,Consumer<Boolean> consumer);
        public abstract void updateStatusData();
        public abstract void save(int address,int value,Consumer<Boolean> consumer);
        public abstract void save(int address,int end,int value,final Consumer<Boolean> consumer);
        public abstract void save(int address,int end,int[] values,final Consumer<Boolean> consumer);
        public abstract void save(int address,int end,String value,final Consumer<Boolean> consumer);
        public abstract void save(String ssn,int sno,final Consumer<Boolean> consumer);
        public abstract void save(String sn,int powerOnF,int probationPeriodF,int day,final Consumer<Boolean> consumer);
    }
}