package com.kehua.energy.monitor.app.business.personal;

import android.content.Context;

import com.blankj.utilcode.util.ActivityUtils;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.model.entity.InvInfoList;
import com.kehua.energy.monitor.app.route.RouterMgr;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;
import me.walten.fastgo.utils.XToast;

public class PersonalPresenter extends PersonalContract.Presenter {

    PersonalContract.View mView;

    @Inject
    APPModel mModel;

    WeakReference<Context> localContext = null;
    
    @Inject
    public PersonalPresenter() {
    }

    @Override
    public void attachView(PersonalContract.View view) {
        mView = view;
        localContext = new WeakReference<Context>(ActivityUtils.getTopActivity() == null ? Fastgo.getContext() : ActivityUtils.getTopActivity());
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    public void loadPersonalMainInfo() {
        // TODO: 2018/9/10  
    }

    @Override
    public void invinfo() {
        invinfo(null);
    }

    @Override
    public void invinfo(final Consumer<InvInfoList> consumer) {
        mView.startWaiting(localContext.get().getString(R.string.检测中));
        mModel.getRemoteModel().invinfo(new Consumer<InvInfoList>() {
            @Override
            public void accept(InvInfoList invInfoList) throws Exception {
                mView.stopWaiting();

                /*if(invInfoList.getNum()>0){
                    //目前采集器与设备关系为1 对 1 所以直接取 index = 0
                    //int devAddr = invInfoList.getInv().get(0).getAddr();
                    //跳转角色选择页面
                    RouterMgr.get().localLogin();
                }else {
                    //采集器未连接设备
                    XToast.error(localContext.get().getString(R.string.采集器未连接设备));
                    return;
                }*/

                RouterMgr.get().localLogin();
                //
                if(consumer!=null)
                    consumer.accept(invInfoList);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.stopWaiting();
                XToast.error(localContext.get().getString(R.string.无法获取设备信息));

                //跳转采集器连接界面
                RouterMgr.get().hotspot(RouterMgr.TYPE_OFF_NETWORK);
            }
        });
    }
}