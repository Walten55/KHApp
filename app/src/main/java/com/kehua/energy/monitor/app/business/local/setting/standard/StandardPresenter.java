package com.kehua.energy.monitor.app.business.local.setting.standard;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.di.scope.ActivityScope;

import static com.kehua.energy.monitor.app.configuration.Frame.getStandardList;

@ActivityScope
public class StandardPresenter extends StandardContract.Presenter {

    StandardContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public StandardPresenter() {
    }

    @Override
    public void attachView(StandardContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setupData() {
        //    [0,23]
//            0-德国；1-英国；2-中国；
//            3-澳洲；4-新西兰；5-法国VDE；
//            6-意大利；7-荷兰；8-西班牙；
//            9-泰国PEA；10-泰国MEA；
//            11-美国；12-美国加州；13-加拿大；14-农村电网；
//            15-城镇电网；16-法国VFR；
//            17-法国SEI；18-法国CRAE；
//            19-定制1；20-定制2；
//            21-定制3；22-定制4；
//            23-定制5；

//    0-中国标准；1-美国；2-美国加州；3-德国；4-澳洲；5-新西兰；6-英国；
//            7-泰国PEA；8-泰国MEA；9-意大利；10-法国，11-加拿大，12-其他。

        mView.onSetupData(getStandardList());
    }


}