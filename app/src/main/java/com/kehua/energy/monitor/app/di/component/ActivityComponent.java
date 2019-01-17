package com.kehua.energy.monitor.app.di.component;

import android.app.Activity;

import com.kehua.energy.monitor.app.business.NetworkSetting.WifiConfig.WifiConfigActivity;
import com.kehua.energy.monitor.app.business.NetworkSetting.hotspot.HotspotActivity;
import com.kehua.energy.monitor.app.business.forgetpwd.ForgetPasswordActivity;
import com.kehua.energy.monitor.app.business.input.InputActivity;
import com.kehua.energy.monitor.app.business.local.LocalMain.LocalMainActivity;
import com.kehua.energy.monitor.app.business.local.first.FirstSettingActivity;
import com.kehua.energy.monitor.app.business.local.history.HistoryInfoActivity;
import com.kehua.energy.monitor.app.business.local.login.LocalLoginActivity;
import com.kehua.energy.monitor.app.business.local.setting.branch.BranchSettingActivity;
import com.kehua.energy.monitor.app.business.local.setting.pattern.patternModelChild.LocalPatternChildActivity;
import com.kehua.energy.monitor.app.business.local.setting.standard.StandardActivity;
import com.kehua.energy.monitor.app.business.local.setting.upgrade.UpgradeActivity;
import com.kehua.energy.monitor.app.business.local.setting.workPattern.WorkPatternActivity;
import com.kehua.energy.monitor.app.business.login.LoginActivity;
import com.kehua.energy.monitor.app.business.main.MainActivity;
import com.kehua.energy.monitor.app.business.map.MapForLocationActivity;
import com.kehua.energy.monitor.app.business.personal.about.AboutActivity;
import com.kehua.energy.monitor.app.business.personal.language.LanguageActivity;
import com.kehua.energy.monitor.app.business.register.RegisterActivity;
import com.kehua.energy.monitor.app.business.scanner.ScannerActivity;
import com.kehua.energy.monitor.app.di.module.ActivityModule;

import dagger.Component;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.di.scope.ActivityScope;


@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

    void inject(MainActivity mainActivity);

    void inject(HotspotActivity hotspotActivity);

    void inject(WifiConfigActivity wifiConfigActivity);

    void inject(LoginActivity aty);

    void inject(RegisterActivity aty);

    void inject(ForgetPasswordActivity aty);

    void inject(LocalMainActivity localMainActivity);

    void inject(LocalLoginActivity localLoginActivity);

    void inject(InputActivity inputActivity);

    void inject(WorkPatternActivity workPatternActivity);

    void inject(StandardActivity standardActivity);

    void inject(LanguageActivity aty);

    void inject(AboutActivity aty);

    void inject(LocalPatternChildActivity aty);

    void inject(FirstSettingActivity firstSettingActivity);

    void inject(HistoryInfoActivity historyInfoActivity);

    void inject(ScannerActivity scannerActivity);

    void inject(UpgradeActivity upgradeActivity);

    void inject(BranchSettingActivity branchSettingActivity);

    void inject(MapForLocationActivity mapForLocationActivity);
}