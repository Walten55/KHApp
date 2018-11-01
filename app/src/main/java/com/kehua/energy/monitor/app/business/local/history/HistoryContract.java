package com.kehua.energy.monitor.app.business.local.history;

import com.kehua.energy.monitor.app.model.entity.RecordData;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.base.mvp.BasePresenter;
import me.walten.fastgo.base.mvp.BaseView;

public interface HistoryContract {

    interface View extends BaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void recordCount(Consumer<Boolean> consumer);
        public abstract void recordConfig(int recordType,int index,int size,Consumer<Boolean> consumer);
        public abstract void records(int template, int index,int size,Consumer<List<RecordData>> consumer);
    }
}