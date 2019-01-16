package com.kehua.energy.monitor.app.business.scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.gyf.barlibrary.ImmersionBar;
import com.hwangjr.rxbus.RxBus;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.base.XSimpleActivity;
import com.kehua.energy.monitor.app.configuration.Config;
import com.kehua.energy.monitor.app.di.component.DaggerActivityComponent;
import com.kehua.energy.monitor.app.di.module.ActivityModule;
import com.kehua.energy.monitor.app.route.RouterMgr;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.component.AppComponent;
import me.walten.fastgo.utils.XRxUtil;
import me.walten.fastgo.utils.XToast;

@Route(path = RouterMgr.LOCAL_SCAN)
public class ScannerActivity extends XSimpleActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @BindView(R.id.fl_container)
    FrameLayout mContainer;

    @BindView(R.id.tv_scan_light)
    TextView mLightTv;

    boolean light;
    final static int PhotoRequestCode = 10000;

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

        RxBus.get().post(Config.EVENT_CODE_SCAN_RESULT, rawResult.getText());
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
        mScannerView.setBorderColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
    public void clickBack(View view) {
        finish();
    }

    @OnClick(R.id.rl_scan_light)
    public void clickLight(View view) {
        mScannerView.setFlash(light = !light);
        if (light) {
            mLightTv.setText(R.string.关灯);
        } else {
            mLightTv.setText(R.string.开灯);
        }
    }

    @OnClick(R.id.tv_photo_album)
    public void checkPermission() {
        //相关权限是否缺失
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 没有获得授权，申请授权
            new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                toPhotoAlbum();
                            } else {
                                XToast.error(getString(R.string.缺少读取外部存储权限));
                            }
                        }
                    });
        } else {
            toPhotoAlbum();
        }
    }

    private void toPhotoAlbum() {
        // 已经获得授权，可以获取图片
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PhotoRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
//            String[] filePathColumns = {MediaStore.Images.Media.DATA};
//            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//            c.moveToFirst();
//            int columnIndex = c.getColumnIndex(filePathColumns[0]);
//            String imagePath = c.getString(columnIndex);
            //解析图片
            scanningImage(selectedImage);
        }
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param imgUri
     * @return
     */
    public void scanningImage(Uri imgUri) {
        if (imgUri != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(imgUri)
                    .into(new SimpleTarget<Bitmap>(800, 600) {//需要指定大小，否则容易OOM
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //再将Bitmap转BindBitmap
                            int[] intArray = new int[resource.getWidth() * resource.getHeight()];
                            //copy pixel data from the Bitmap into the 'intArray' array
                            resource.getPixels(intArray, 0, resource.getWidth(), 0, 0, resource.getWidth(), resource.getHeight());

                            LuminanceSource source = new RGBLuminanceSource(resource.getWidth(), resource.getHeight(), intArray);
                            final BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                            //解析
                            Flowable.fromCallable(new Callable<Result>() {

                                @Override
                                public Result call() throws Exception {
                                    // 解析二维码/条码
                                    QRCodeReader qrCodeReader = new QRCodeReader();
                                    Result result = new Result("", null, null, null);
                                    try {
                                        result = qrCodeReader.decode(binaryBitmap);
                                    } catch (Exception e) {

                                    }
                                    return result;
                                }
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Result>() {
                                        @Override
                                        public void accept(Result rawResult) throws Exception {
                                            if (rawResult != null && rawResult.getRawBytes() != null) {
                                                handleResult(rawResult);
                                            } else {
                                                XToast.error(Fastgo.getContext().getString(R.string.图片扫描失败));
                                            }
                                        }
                                    });
                        }
                    });

        }
    }

}
