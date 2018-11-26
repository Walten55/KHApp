package com.kehua.energy.monitor.app.utils;

import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.view.EnergyFlowChart;

/*
 * -----------------------------------------------------------------
 * Copyright by 2018 Walten, All rights reserved.
 * -----------------------------------------------------------------
 * desc:
 * -----------------------------------------------------------------
 * 2018/10/9 : Create EnergyFlowChartHelper.java (Walten);
 * -----------------------------------------------------------------
 */
public class EnergyFlowChartHelper {

    //PV状态值*3*3*2+BAT状态值*3*2+ GRID 状态值*2+ LOAD 状态值*1
    public static void setupData(EnergyFlowChart mChart) {

        if (mChart == null)
            return;

        mChart.stopAll();
        mChart.getBLTextView().setText("0.0");
        mChart.getBRTextView().setText("0.0");
        mChart.getTLTextView().setText("0.0");
        mChart.getTRTextView().setText("0.0");

        double pvPower = 0;
        double gridPower = 0;
        double chargePower = 0;
        double dischargePower = 0;
        double loadPower = 0;
        double secondaryLoadPower = 0;

        if (CacheManager.getInstance().get(Frame.PV总功率地址()) != null) {
            pvPower = Double.valueOf(CacheManager.getInstance().get(Frame.PV总功率地址()).getParseValue());
        }
        if (CacheManager.getInstance().get(Frame.并网有功功率地址()) != null) {
            gridPower = Double.valueOf(CacheManager.getInstance().get(Frame.并网有功功率地址()).getParseValue());
        }
        if (CacheManager.getInstance().get(Frame.电池充电功率地址()) != null) {
            chargePower = Double.valueOf(CacheManager.getInstance().get(Frame.电池充电功率地址()).getParseValue());
        }
        if (CacheManager.getInstance().get(Frame.电池放电功率地址()) != null) {
            dischargePower = Double.valueOf(CacheManager.getInstance().get(Frame.电池放电功率地址()).getParseValue());
        }
        if (CacheManager.getInstance().get(Frame.负载有功功率地址()) != null) {
            loadPower = Double.valueOf(CacheManager.getInstance().get(Frame.负载有功功率地址()).getParseValue());
        }
        if (CacheManager.getInstance().get(Frame.次要负载有功功率地址()) != null) {
            secondaryLoadPower = Double.valueOf(CacheManager.getInstance().get(Frame.次要负载有功功率地址()).getParseValue());
        }

        int result = getPVStatus(pvPower) * 3 * 3 * 2 + getBatStatus(chargePower,dischargePower) * 3 * 2 + getGridStatus(gridPower) * 2 + getLoadStatus(loadPower+secondaryLoadPower);

        loadPower+=secondaryLoadPower;

        double batPower = chargePower>0?chargePower:dischargePower>0?dischargePower:0;

        switch (result) {

            case 0:
            case 1:
            case 2:
            case 3:
            case 4:

                break;
            case 5:
                //GRID->INV、INV->LOAD
                //电网功率绝对值≥负载功率，否则将电网功率赋值为负载功率*(-1)
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(Math.abs(gridPower)<loadPower){
                    gridPower = loadPower*-1;
                }
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 6:

                break;
            case 7:
                //BAT->INV、INV->LOAD
                //BAT功率≥负载功率，否则将BAT功率赋值为负载功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(batPower<loadPower){
                    batPower = loadPower;
                }

                mChart.getBLTextView().setText(batPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 8:
                //BAT->INV、INV->GRID
                //BAT功率≥电网功率，否则将电池功率赋值为电网功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);

                if(batPower<gridPower){
                    batPower = gridPower;
                }

                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 9:
                //BAT->INV、INV->GRID、INV->LOAD
                //BAT功率≥电网功率+负载功率，否则将电池功率赋值为电网功率+负载功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(batPower<(gridPower+loadPower)){
                    batPower = gridPower+loadPower;
                }
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 10:

                break;
            case 11:
                //BAT->INV、GRID->INV、INV->LOAD
                //BAT功率+电网功率绝对值≥负载功率，否则将电网功率赋值为(负载功率-BAT功率)*(-1)
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if((batPower+Math.abs(gridPower))<loadPower){
                    gridPower = (loadPower-batPower)*-1;
                }
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 12:
            case 13:
            case 14:
            case 15:

                break;
            case 16:
                //INV->BAT、GRID->INV
                //电网功率绝对值≥BAT功率绝对值，否则将电网功率赋值为BAT功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                if(Math.abs(gridPower)<Math.abs(batPower)){
                    gridPower = batPower;
                }
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 17:
                //INV->BAT、GRID->INV、INV->LOAD
                //电网功率绝对值≥BAT功率绝对值+负载功率，否则将电网功率赋值为（BAT功率绝对值+负载功率）*（-1）
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(Math.abs(gridPower)<(Math.abs(batPower)+loadPower)){
                    gridPower = (Math.abs(batPower)+loadPower)*-1;
                }
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getTRTextView().setText(loadPower+"");

                break;
            case 18:

                break;
            case 19:
                //PV->INV、INV->LOAD
                //PV功率≥负载功率，否则将PV功率赋值为负载功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower<loadPower){
                    pvPower = loadPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 20:
                //PV->INV、INV->GRID
                //PV功率≥电网功率，否则将PV功率赋值为电网功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                if(pvPower<gridPower){
                    pvPower = gridPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 21:
                //PV->INV、INV->GRID、INV->LOAD
                //PV功率≥电网功率+负载功率，否则将PV功率赋值为电网功率+负载功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower<(gridPower+loadPower)){
                    pvPower = gridPower+loadPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");

                break;
            case 22:

                break;
            case 23:
                //PV->INV、GRID->INV、INV->LOAD
                //PV功率+电网功率绝对值≥负载功率，否则将电网功率赋值为（负载功率-PV功率）*（-1）
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);

                if (pvPower+Math.abs(gridPower)>loadPower){
                    gridPower = (loadPower-pvPower)*-1;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 24:

                break;
            case 25:
                //PV->INV、BAT->INV、INV->LOAD
                //PV功率+BAT功率≥负载功率，否则将BAT功率赋值为负载功率-PV功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower+batPower<loadPower){
                    batPower = loadPower-pvPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 26:
                //PV->INV、BAT->INV、INV->GRID
                //PV功率+BAT功率≥电网功率，否则将电网功率赋值为BAT功率+PV功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                if(pvPower+batPower<gridPower){
                    gridPower = batPower+pvPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 27:
                //PV->INV、BAT->INV、INV->GRID、INV->LOAD
                //"PV功率+BAT功率≥电网功率+负载功率，否则如果BAT功率+PV功率-负载功率≥0，则电网功率=BAT功率+PV功率-负载功率
                //如果BAT功率+PV功率-负载功率<0，则电网功率=0,负载功率=PV功率+BAT功率"
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower+batPower<gridPower+loadPower&&batPower+pvPower-loadPower>=0){
                    gridPower = batPower+pvPower-loadPower;
                }else if(pvPower+batPower<gridPower+loadPower&&batPower+pvPower-loadPower<0){
                    gridPower = 0;
                    loadPower = pvPower+batPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 28:

                break;
            case 29:
                //PV->INV、BAT->INV、GRID->INV、INV->LOAD
                //PV功率+BAT功率+电网功率绝对值≥负载功率，否则负载功率 = PV功率+BAT功率+电网功率绝对值
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower+batPower+Math.abs(gridPower)<loadPower){
                    loadPower = pvPower+batPower+Math.abs(gridPower);
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 30:
                //PV->INV、INV->BAT
                //PV功率≥BAT功率，否则PV功率=BAT功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                if(pvPower<batPower){
                    pvPower = batPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                break;
            case 31:
                //PV->INV、INV->BAT、INV->LOAD
                //PV功率≥BAT功率绝对值+负载功率，否则PV功率=BAT功率绝对值+负载功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower<Math.abs(batPower)+loadPower){
                    pvPower = Math.abs(batPower)+loadPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 32:
                //PV->INV、INV->BAT、INV->GRID
                //PV功率≥BAT功率绝对值+电网功率，否则PV功率=BAT功率绝对值+电网功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                if(pvPower<Math.abs(batPower)+gridPower){
                    pvPower = Math.abs(batPower)+gridPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 33:
                //PV->INV、INV->BAT、INV->GRID、INV->LOAD
                //PV功率≥BAT功率绝对值+负载功率+电网功率，否则PV功率=BAT功率绝对值+负载功率+电网功率
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower<Math.abs(batPower)+loadPower+gridPower){
                    pvPower = Math.abs(batPower)+loadPower+gridPower;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");
                break;
            case 34:
                //PV->INV、INV->BAT、GRID->INV
                //PV功率+电网功率绝对值≥BAT功率绝对值，否则BAT功率=(PV功率+电网功率)*(-1)
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                if(pvPower+Math.abs(gridPower)<Math.abs(batPower)){
                    batPower = (pvPower+gridPower)*-1;
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                break;
            case 35:
                //PV->INV、INV->BAT、GRID->INV、INV->LOAD
                //"PV功率+电网功率绝对值≥电池功率绝对值+负载功率，否则如果PV功率+电网功率绝对值-负载功率≥0，则电池功率=(PV功率+电网功率绝对值-负载功率)*(-1)
                //如果PV功率+电网功率绝对值-负载功率<0，则电池功率=0,负载功率=PV功率+电网功率绝对值"
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TL_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BL_OUT);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_TR_IN);
                mChart.start(EnergyFlowChart.FLOW_DIRECTION_BR_OUT);
                if(pvPower+Math.abs(gridPower)<Math.abs(batPower)+loadPower){
                    if(pvPower+Math.abs(gridPower)-loadPower>=0){
                        batPower = (pvPower+Math.abs(gridPower)-loadPower)*-1;
                    }else {
                        batPower = 0;
                        loadPower = pvPower+Math.abs(gridPower);
                    }
                }
                mChart.getTLTextView().setText(pvPower+"");
                mChart.getBLTextView().setText(batPower+"");
                mChart.getTRTextView().setText(gridPower+"");
                mChart.getBRTextView().setText(loadPower+"");

                break;
        }

        if(Frame.isStorageDevice(LocalUserManager.getDeviceType())){
            mChart.able(EnergyFlowChart.CHILD_BR);
            mChart.able(EnergyFlowChart.CHILD_BL);
        }else {
            mChart.disable(EnergyFlowChart.CHILD_BR);
            mChart.disable(EnergyFlowChart.CHILD_BL);
        }
    }

    private static int getPVStatus(double pvPower) {
        return pvPower > 0 ? 1 : 0;
    }

    private static int getGridStatus(double gridPower) {
        return gridPower>0?1:gridPower<0?2:0;
    }

    private static int getBatStatus(double chargePower,double dischargePower) {
        return chargePower>0?2:dischargePower>0?1:0;
    }

    private static int getLoadStatus(double loadPower) {
        return loadPower > 0 ? 1 : 0;
    }
}
