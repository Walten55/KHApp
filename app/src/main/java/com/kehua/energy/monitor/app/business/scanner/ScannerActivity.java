package com.kehua.energy.monitor.app.business.scanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.Result;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XSimpleActivity;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;

import butterknife.BindView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.walten.fastgo.di.component.AppComponent;
@Route(path = RouterMgr.LOCAL_SCAN)
public class ScannerActivity extends XSimpleActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @BindView(R.id.fl_container)
    FrameLayout mContainer;

    @BindView(R.id.tv_scan_light)
    TextView mLightTv;

    boolean light;

    @Override
    protected boolean enableImmersive(ImmersionBar immersionBar) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("handleResult", rawResult.getText()); // Prints scan results
        Log.v("handleResult", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        RxBus.get().post(Config.EVENT_CODE_SCAN_RESULT,rawResult.getText());
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);

        finish();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_scanner;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(this);
        mScannerView.setBorderColor(ContextCompat.getColor(this,R.color.colorPrimary));
        mContainer.addView(mScannerView);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setupComponent(@NonNull AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @OnClick(R.id.rv_iv_back)
    public void clickBack(View view){
        finish();
    }

    @OnClick(R.id.rl_scan_light)
    public void clickLight(View view){
        mScannerView.setFlash(light = !light);
        if(light){
            mLightTv.setText(R.string.关灯);
        }else {
            mLightTv.setText(R.string.开灯);
        }
    }


}
