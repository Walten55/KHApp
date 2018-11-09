package com.kehua.energy.monitor.app.business.local.setting.upgrade;

import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.Upgrade;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.di.scope.ActivityScope;
import me.walten.fastgo.utils.XToast;

@ActivityScope
public class UpgradePresenter extends UpgradeContract.Presenter {

    UpgradeContract.View mView;

    private Disposable mCollDisposable;

    @Inject
    APPModel mModel;

    @Inject
    public UpgradePresenter() {
    }

    @Override
    public void attachView(UpgradeContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
        //结束采集
        if(mCollDisposable!=null)
            mCollDisposable.dispose();
    }

    @Override
    public void upload(String path, final Consumer<Boolean> consumer) {
        mView.startWaiting(Fastgo.getContext().getString(R.string.上传中));
        mModel.getRemoteModel().upload(path, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean success) throws Exception {
                mView.stopWaiting();
                if(!success){
                    XToast.error(Fastgo.getContext().getString(R.string.上传失败));
                }
                if(consumer!=null)
                    consumer.accept(success);
            }
        });
    }

    @Override
    public void upgrade() {
        mModel.getRemoteModel().upgrade(new Consumer<Upgrade>() {
            @Override
            public void accept(Upgrade upgrade) throws Exception {
                String status = "";
                String upProgress = "";
                String dnProgress = "";
                switch (upgrade.getStatus()){
                    case 0:
                        status = Fastgo.getContext().getString(R.string.未升级);
                        break;
                    case 1:
                        status = Fastgo.getContext().getString(R.string.升级包下载中);
                        break;
                    case 2:
                        status = Fastgo.getContext().getString(R.string.下载完成);
                        break;
                    case 3:
                        status = Fastgo.getContext().getString(R.string.升级成功);
                        break;
                    case 4:
                        status = Fastgo.getContext().getString(R.string.升级失败);
                        break;
                }
                upProgress = upgrade.getUpprogress()+"%";
                dnProgress = upgrade.getDnprogress()+"%";

                mView.onUpgrade(String.format(Fastgo.getContext().getString(R.string.升级状态),status,dnProgress));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Logger.e(throwable.getMessage());
            }
        });
    }

    @Override
    public void startUpgrade() {
        if(mCollDisposable==null||mCollDisposable.isDisposed()){
            upgrade();
            mCollDisposable = Flowable.interval(3, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            upgrade();
                        }
                    });
        }
    }
}