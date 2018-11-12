package com.kehua.energy.monitor.app.business.local.monitor;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.GroupInfo;
import com.kehua.energy.monitor.app.model.entity.MonitorEntity;
import com.kehua.energy.monitor.app.model.entity.PointInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
public class LocalMonitorPresenter extends LocalMonitorContract.Presenter {

    LocalMonitorContract.View mView;

    private List<MonitorEntity> result = new ArrayList<>();

    private int runningInfoPosition = 0;
    private int deviceInfoPosition = 0;

    @Inject
    APPModel mModel;



    @Inject
    public LocalMonitorPresenter() {

    }

    @Override
    public void attachView(LocalMonitorContract.View view) {
        mView = view;

    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void setupData() {
        DeviceData expireData = CacheManager.getInstance().get(Frame.试用期到期临近采集()[0]);
        DeviceData nearData = CacheManager.getInstance().get(Frame.试用期到期临近采集()[1]);
        if (expireData != null && nearData != null) {
            if (expireData.getIntValue() == 1) {
                //到期
                RxBus.get().post(Config.EVENT_CODE_PROBATION_EXPIRE, "");
            } else if (nearData.getIntValue() == 1) {
                //临近
                RxBus.get().post(Config.EVENT_CODE_PROBATION_NEAR, "");
            } else {
                //正常
                RxBus.get().post(Config.EVENT_CODE_PROBATION_NORMAL, "");
            }
        }

        result.clear();
        result.add(new MonitorEntity(MonitorEntity.OVERVIEW, ""));
        result.add(new MonitorEntity(MonitorEntity.CENTER_TITLE, Fastgo.getContext().getString(R.string.运行数据)));
        runningInfoPosition = result.size() - 1;

        List<GroupInfo> groupInfos = initGroups();
        for (GroupInfo groupInfo : groupInfos) {
            result.add(new MonitorEntity(MonitorEntity.LEFT_TITLE, groupInfo.getGroupName()));
            List<PointInfo> pointInfos = getPointInfoListWith(groupInfo.getGroup());
            for (PointInfo pointInfo : pointInfos) {
                result.add(new MonitorEntity(MonitorEntity.SIMPLE_DATA, pointInfo));
            }

            if (Frame.单相协议 == LocalUserManager.getPn()) {
                //单相协议特殊排版
                if (Frame.光伏信息.equals(groupInfo.getGroup())) {
                    DeviceData deviceData = CacheManager.getInstance().get(Frame.MPPT路数地址);
                    if (deviceData != null) {
                        result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                        result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                                "",
                                Fastgo.getContext().getString(R.string.电压_单位),
                                Fastgo.getContext().getString(R.string.电流_单位),
                                Fastgo.getContext().getString(R.string.功率_W)}));
                        result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                "PV1",
                                "4520",
                                "4521",
                                "4522"}));
                        result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                "PV2",
                                "4523",
                                "4524",
                                "4525"}));
                        result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                "PV3",
                                "4560",
                                "4561",
                                "4562"}));
                        result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                "PV4",
                                "4563",
                                "4564",
                                "4565"}));
                        for (int i = 0; i < deviceData.getIntValue(); i++) {
                            result.remove(result.size() - 1);
                        }
                    }

                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
                } else {
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
                }
            } else {
                //三相协议特殊排版

                if (Frame.电网信息.equals(groupInfo.getGroup())) {
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                    result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                            "",
                            Fastgo.getContext().getString(R.string.电网电压_单位),
                            Fastgo.getContext().getString(R.string.电网电流_单位),
                            ""}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "U",
                            "4512",
                            "4515"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "V",
                            "4513",
                            "4516"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "W",
                            "4514",
                            "4517"}));

                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                } else if (Frame.负载信息.equals(groupInfo.getGroup())) {
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                    result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                            "",
                            Fastgo.getContext().getString(R.string.负载电压_单位),
                            Fastgo.getContext().getString(R.string.负载电流_单位),
                            ""}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "U",
                            "4601",
                            "4604"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "V",
                            "4602",
                            "4605"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "W",
                            "4603",
                            "4606"}));


                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));


                    result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                            "",
                            Fastgo.getContext().getString(R.string.有功功率_单位),
                            Fastgo.getContext().getString(R.string.无功功率_单位),
                            Fastgo.getContext().getString(R.string.视在功率_单位)}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "U",
                            "4610",
                            "4613",
                            "4616"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "V",
                            "4611",
                            "4614",
                            "4617"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "W",
                            "4612",
                            "4615",
                            "4618"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "负载(总)",
                            "4607",
                            "4608",
                            "4609"}));

                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                } else if (Frame.光伏信息.equals(groupInfo.getGroup())) {

                    DeviceData mpptData = CacheManager.getInstance().get(Frame.MPPT路数地址);
                    if (mpptData != null) {
                        result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                        result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                                "",
                                Fastgo.getContext().getString(R.string.电压_单位),
                                Fastgo.getContext().getString(R.string.电流_单位),
                                ""}));

                        for (int i = 0; i < mpptData.getIntValue(); i++) {
                            result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                    "MPPT" + (i + 1),
                                    "" + (4661 + i),
                                    "" + (4669 + i)}));
                        }
                    }

                    DeviceData pvBranchData = CacheManager.getInstance().get(Frame.PV支路数地址);
                    if (pvBranchData != null) {
                        result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                        result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                                "",
                                Fastgo.getContext().getString(R.string.电压_单位),
                                Fastgo.getContext().getString(R.string.电流_单位),
                                Fastgo.getContext().getString(R.string.功率_Kw)}));

                        for (int i = 0; i < pvBranchData.getIntValue(); i++) {
                            result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                                    "PV" + (i + 1),
                                    "" + (4701 + i),
                                    "" + (4733 + i),
                                    "" + (4765 + i)}));
                        }
                    }
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
                } else if (Frame.内部信息.equals(groupInfo.getGroup())) {

                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_HEAD, new String[]{
                            "",
                            Fastgo.getContext().getString(R.string.逆变电压_单位),
                            Fastgo.getContext().getString(R.string.逆变电流_单位),
                            ""}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "U",
                            "5500",
                            "5503"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "V",
                            "5501",
                            "5504"}));
                    result.add(new MonitorEntity(MonitorEntity.TABLE_CONTENT, new String[]{
                            "W",
                            "5502",
                            "5505"}));
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));

                } else {
                    result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
                }

            }

        }

        result.remove(result.size() - 1);
        result.add(new MonitorEntity(MonitorEntity.CENTER_TITLE, Fastgo.getContext().getString(R.string.设备信息)));
        deviceInfoPosition = result.size() - 1;

        List<PointInfo> pointInfos = getPointInfoListWith(Frame.设备信息);
        for (PointInfo pointInfo : pointInfos) {
            if (LocalUserManager.getRole() != LocalUserManager.ROLE_NORMAL
                    && (Integer.valueOf(pointInfo.getAddress()) == Frame.控制软件1地址()
                    || Integer.valueOf(pointInfo.getAddress()) == Frame.控制软件2地址())) {
                //do nothing
            } else {
                result.add(new MonitorEntity(MonitorEntity.SIMPLE_DATA, pointInfo));
            }

        }
        for (int i = 0; i < 15; i++) {
            result.add(new MonitorEntity(MonitorEntity.MARGIN, ""));
        }

        mView.onSetupPosition(runningInfoPosition, deviceInfoPosition);
        mView.onSetupData(result);
    }

    @Override
    public List<GroupInfo> initGroups() {
        List<GroupInfo> groups = mModel.getLocalModel().getGroupsWith(LocalUserManager.getPn());
        mView.setupTabLayout(groups);
        return groups;
    }

    @Override
    public List<PointInfo> getPointInfoListWith(String group) {
        return mModel.getLocalModel().getPointInfosWith(LocalUserManager.getPn(), group, LocalUserManager.getRoleAuthority());
    }

}