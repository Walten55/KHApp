package com.kehua.energy.monitor.app.business.local.setting.branch;

import android.util.ArrayMap;

import com.kehua.energy.monitor.app.model.APPModel;
import com.kehua.energy.monitor.app.utils.ByteUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.walten.fastgo.di.scope.ActivityScope;

@ActivityScope
public class BranchSettingPresenter extends BranchSettingContract.Presenter {

    BranchSettingContract.View mView;

    @Inject
    APPModel mModel;

    @Inject
    public BranchSettingPresenter() {
    }

    @Override
    public void attachView(BranchSettingContract.View view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
        mModel.destroy();
    }

    @Override
    void setupData(String hexValue) {
        List<ArrayMap<String,Boolean>> data = new ArrayList<>();

        byte[] dataBytes = ByteUtils.hexStringToBytes(hexValue);
         if(dataBytes.length!=2){
            for(int i=0;i<16;i++){
                ArrayMap<String,Boolean> map = new ArrayMap<>();
                map.put("boolean",false);
                data.add(map);
            }
            mView.showData(data);
            return;
        }

        byte[] temp1 = sub2Byte(dataBytes);
        for (int i = 0; i < 8; i++) {
            ArrayMap<String,Boolean> map = new ArrayMap<>();
            map.put("boolean",((temp1[0] >> (7-i)) & 0x1) == 1);
            data.add(map);
        }

        dataBytes = ByteUtils.subArray(dataBytes, 1);
        byte[] temp2 = sub2Byte(dataBytes);
        for (int i = 0; i < 8; i++) {
            ArrayMap<String,Boolean> map = new ArrayMap<>();
            map.put("boolean",((temp2[0] >> (7-i)) & 0x1) == 1);
            data.add(map);
        }

        Collections.reverse(data);
        mView.showData(data);
    }

    @Override
    int getResult(List<ArrayMap<String, Boolean>> data) {

        StringBuffer sb = new StringBuffer();
        for(ArrayMap<String, Boolean> map : data){
            sb.append(map.get("boolean")?1:0);
        }

        int value = new BigInteger(sb.toString(), 2).intValue();
        return value;
    }

    private byte[] sub2Byte(byte[] source){
        byte[] temp = new byte[1];
        System.arraycopy(source, 0, temp, 0, temp.length);
        return temp;
    }
}