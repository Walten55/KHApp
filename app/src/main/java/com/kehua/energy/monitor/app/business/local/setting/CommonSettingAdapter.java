package com.kehua.energy.monitor.app.business.local.setting;

import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.kehua.energy.monitor.app.R;
import com.kehua.energy.monitor.app.application.LocalUserManager;
import com.kehua.energy.monitor.app.business.local.setting.advanced.AdvancedPresenter;
import com.kehua.energy.monitor.app.cache.CacheManager;
import com.kehua.energy.monitor.app.configuration.Frame;
import com.kehua.energy.monitor.app.model.entity.DeviceData;
import com.kehua.energy.monitor.app.model.entity.PointInfo;
import com.kehua.energy.monitor.app.model.entity.SettingEntity;
import com.kehua.energy.monitor.app.model.entity.Standard;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.walten.fastgo.common.Fastgo;

import static com.kehua.energy.monitor.app.configuration.Frame.getStandardList;

public class CommonSettingAdapter extends BaseMultiItemQuickAdapter<SettingEntity, BaseViewHolder> {

    private AdvancedPresenter presenter;

    private List<Standard> standardList;

    public CommonSettingAdapter(List data, AdvancedPresenter presenter) {
        super(data);
        addItemType(SettingEntity.SWITCH, R.layout.item_local_setting_switch_with_hint);
        addItemType(SettingEntity.TEXT, R.layout.item_local_setting_text);
        this.presenter = presenter;
        standardList = getStandardList();
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingEntity item) {
        final PointInfo data = item.getData();
        
        switch (helper.getItemViewType()) {
            case SettingEntity.SWITCH:
                helper.setText(R.id.tv_name, data.getDescription());
                helper.setText(R.id.tv_hint, data.getUnit());
                final SwitchButton switchButton = helper.getView(R.id.sb_value);
                if (CacheManager.getInstance().get(Integer.valueOf(data.getAddress().trim())) != null) {
                    helper.setGone(R.id.tv_reading, false);
                    helper.setGone(R.id.sb_value, true);

                    final DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(data.getAddress()));
                    switchButton.setCheckedImmediatelyNoEvent(deviceData.getIntValue() != Frame.OFF);

                    switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
                            final NormalDialog dialog = new NormalDialog(mContext);
                            dialog.content(Fastgo.getContext().getString(R.string.是否更改设置))
                                    .title(Fastgo.getContext().getString(R.string.温馨提示))
                                    .style(NormalDialog.STYLE_TWO)//
                                    .titleTextSize(23);
                            dialog.btnText(mContext.getString(R.string.取消), mContext.getString(R.string.确定));
                            dialog.titleTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    dialog.dismiss();
                                    switchButton.setCheckedImmediatelyNoEvent(!isChecked);
                                }
                            }, new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    dialog.dismiss();
                                    presenter.toggle(Integer.valueOf(deviceData.getRegisterAddress().trim()), isChecked, new Consumer<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) throws Exception {
                                            if(!success){
                                                switchButton.setCheckedImmediatelyNoEvent(!isChecked);
                                            }
                                        }
                                    });
                                    CacheManager.getInstance().remove(Integer.valueOf(deviceData.getRegisterAddress().trim()));
                                }
                            });
                            dialog.show();
                        }
                    });

                } else {
                    helper.setGone(R.id.sb_value, false);
                    helper.setGone(R.id.tv_reading, true);
                }
                break;

            case SettingEntity.TEXT:
                helper.setText(R.id.tv_name, data.getDescription());
                ((TextView)helper.getView(R.id.tv_value)).setHint("");
                
                if (CacheManager.getInstance().get(Integer.valueOf(data.getAddress().trim())) != null) {
                    helper.setGone(R.id.tv_reading,false);

                    DeviceData deviceData = CacheManager.getInstance().get(Integer.valueOf(data.getAddress()));

                    if(item.getData().getGroup().equals(Frame.校准设置)){
                        //校准设置

                        DeviceData correspondingDeviceData = CacheManager.getInstance().get(Integer.valueOf(data.getSgroup()));
                        if(correspondingDeviceData!=null)
                            helper.setText(R.id.tv_value, correspondingDeviceData.getParseValue() + " " + correspondingDeviceData.getUnit());
                        else
                            helper.setText(R.id.tv_value, "");

                    }else {
                        if (deviceData.getRegisterAddress().equals(Frame.标准类型地址() + "") && deviceData.getIntValue() <= (standardList.size() - 1)) {
                            //标准类型
                            helper.setText(R.id.tv_value, standardList.get(deviceData.getIntValue()).getName());
                        } else if (deviceData.getRegisterAddress().equals(Frame.电池类型地址() + "")) {
                            //电池类型
                            helper.setText(R.id.tv_value, Frame.getBatteryType(deviceData.getIntValue()));
                        } else if (deviceData.getRegisterAddress().equals(Frame.充电倍率地址() + "")) {
                            //充电倍率
                            helper.setText(R.id.tv_value, Frame.getChargeRate(deviceData.getIntValue()));
                        } else if (deviceData.getRegisterAddress().equals("6320") && LocalUserManager.getPn() == Frame.单相协议) {
                            //仅单相协议有 外接传感器
                            helper.setText(R.id.tv_value, Frame.getCXNT(deviceData.getIntValue()));
                        }else if (deviceData.getRegisterAddress().equals("6321") && LocalUserManager.getPn() == Frame.单相协议) {
                            //仅单相协议有 CT
                            helper.setText(R.id.tv_value, Frame.getCT(deviceData.getIntValue()));
                        }else if (deviceData.getRegisterAddress().equals("6303") && LocalUserManager.getPn() == Frame.三相协议) {
                            //仅三相协议有 MPPT并联模式
                            helper.setText(R.id.tv_value, Frame.getMPPTShunt(deviceData.getIntValue()));
                        }  else if (deviceData.getRegisterAddress().equals("6310") && LocalUserManager.getPn() == Frame.三相协议) {
                            //仅三相协议有 引用机型
                            helper.setText(R.id.tv_value, Frame.getReferenceModel(deviceData.getIntValue()));
                        }else if (deviceData.getRegisterAddress().equals(Frame.开机密码功能地址+"") ||deviceData.getRegisterAddress().equals(Frame.试用期功能地址+"") ) {
                            //开机密码功能 试用期功能
                            helper.setText(R.id.tv_value, Frame.getToggleName(deviceData.getIntValue()));
                        }else if (deviceData.getRegisterAddress().equals(Frame.开机密码地址[0]+"") ||deviceData.getRegisterAddress().equals(Frame.试用期密码地址[0]+"") ) {
                            //开机密码 试用期密码
                            helper.setText(R.id.tv_value, "");
                            ((TextView)helper.getView(R.id.tv_value)).setHint(Fastgo.getContext().getString(R.string.点击设置));
                        }else {
                            //默认
                            helper.setText(R.id.tv_value, deviceData.getParseValue() + " " + deviceData.getUnit());
                        }

                        if(item.getData().getAddress().equals(Frame.恢复出厂设置地址()+"")
                                ||item.getData().getAddress().equals(Frame.清除所有发电量地址+"")
                                ||item.getData().getAddress().equals(Frame.清除历史记录地址+"")
                                ||item.getData().getAddress().equals(Frame.开机密码地址[0]+"")
                                ||item.getData().getAddress().equals(Frame.试用期密码地址[0]+"")
                                ){
                            helper.setText(R.id.tv_value, "");
                        }
                    }
                } else {
                    helper.setText(R.id.tv_value, "");
                    if(item.getData().getAddress().equals(Frame.恢复出厂设置地址()+"")
                            ||item.getData().getAddress().equals(Frame.清除所有发电量地址+"")
                            ||item.getData().getAddress().equals(Frame.清除历史记录地址+"")
                            ||item.getData().getAddress().equals(Frame.开机密码地址[0]+"")
                            ||item.getData().getAddress().equals(Frame.试用期密码地址[0]+"")
                            ){
                        //do nothing
                    }else {
                        helper.setGone(R.id.tv_reading,true);
                    }
                }
                break;
        }
    }
}

