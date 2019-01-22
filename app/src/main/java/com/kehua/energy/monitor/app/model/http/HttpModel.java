package com.kehua.energy.monitor.app.model.http;

import javax.inject.Inject;

import me.walten.fastgo.base.mvp.BaseModel;
import me.walten.fastgo.base.mvp.IModel;
import me.walten.fastgo.integration.IRepositoryManager;

/**
 * 网络请求管理
 * Created by linyixian on 2019/1/22.
 */

public class HttpModel extends BaseModel implements IModel {

    @Inject
    IRepositoryManager repositoryManager;

    @Inject
    public HttpModel() {
        super(null);

    }

}
