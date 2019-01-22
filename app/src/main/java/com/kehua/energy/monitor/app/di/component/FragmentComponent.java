package com.kehua.energy.monitor.app.di.component;

import android.app.Activity;

import com.kehua.energy.monitor.app.business.alarm.AlarmListFragment;
import com.kehua.energy.monitor.app.business.favorite.FavoriteFragment;
import com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForCode.ForgetPwdForCodeFragment;
import com.kehua.energy.monitor.app.business.forgetpwd.forgetPwdForEmail.ForgetPwdForEmailFragment;
import com.kehua.energy.monitor.app.business.forgetpwd.newPwd.NewPwdFragment;
import com.kehua.energy.monitor.app.business.home.HomeFragment;
import com.kehua.energy.monitor.app.business.local.alarm.LocalAlarmFragment;
import com.kehua.energy.monitor.app.business.local.data.DataInfoFragment;
import com.kehua.energy.monitor.app.business.local.history.HistoryFragment;
import com.kehua.energy.monitor.app.business.local.monitor.LocalMonitorFragment;
import com.kehua.energy.monitor.app.business.local.setting.LocalSettingFragment;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedFragment;
import com.kehua.energy.monitor.app.business.local.setting.basic.BasicFragment;
import com.kehua.energy.monitor.app.business.local.setting.battery.BatteryFragment;
import com.kehua.energy.monitor.app.business.local.setting.calibration.CalibrationFragment;
import com.kehua.energy.monitor.app.business.local.setting.device.Device2Fragment;
import com.kehua.energy.monitor.app.business.local.setting.device.DeviceFragment;
import com.kehua.energy.monitor.app.business.local.setting.grid.GridFragment;
import com.kehua.energy.monitor.app.business.local.setting.pattern.PatternFragment;
import com.kehua.energy.monitor.app.business.personal.PersonalFragment;
import com.kehua.energy.monitor.app.di.module.FragmentModule;

import dagger.Component;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.di.scope.FragmentScope;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    Activity getActivity();

    void inject(HomeFragment homeFragment);

    void inject(AlarmListFragment alarmListFragment);

    void inject(FavoriteFragment favoriteFragment);

    void inject(PersonalFragment personalFragment);

    void inject(LocalAlarmFragment localAlarmFragment);

    void inject(LocalMonitorFragment localMonitorFragment);

    void inject(LocalSettingFragment localSettingFragment);

    void inject(DataInfoFragment dataInfoFragment);

    void inject(AdvancedFragment advancedFragment);

    void inject(BasicFragment basicFragment);

    void inject(BatteryFragment batteryFragment);

    void inject(CalibrationFragment calibrationFragment);

    void inject(DeviceFragment deviceFragment);

    void inject(GridFragment gridFragment);

    void inject(PatternFragment patternFragment);

    void inject(Device2Fragment device2Fragment);

    void inject(HistoryFragment historyFragment);

    void inject(ForgetPwdForEmailFragment forgetPwdForEmailFragment);

    void inject(ForgetPwdForCodeFragment forgetPwdForCodeFragment);

    void inject(NewPwdFragment newPwdFragment);
}