package com.kehua.energy.monitor.app.model;

import javax.inject.Inject;

import me.walten.fastgo.base.mvp.IModel;

import com.kehua.energy.monitor.app.model.http.HttpModel;
import com.kehua.energy.monitor.app.model.local.LocalModel;
import com.kehua.energy.monitor.app.model.remote.RemoteModel;

public class APPModel implements IModel {

    private RemoteModel remoteModel;
    private LocalModel localModel;

    private HttpModel httpModel;

    @Inject
    public APPModel(RemoteModel remoteModel, LocalModel localModel, HttpModel httpModel) {
        this.remoteModel = remoteModel;
        this.localModel = localModel;
        this.httpModel = httpModel;
    }

    public RemoteModel getRemoteModel() {
        return remoteModel;
    }

    public LocalModel getLocalModel() {
        return localModel;
    }

    public HttpModel getHttpModel() {
        return httpModel;
    }

    @Override
    public void destroy() {
        if (remoteModel != null)
            remoteModel.destroy();
        if (localModel != null)
            localModel.destroy();
        if (httpModel != null)
            httpModel.destroy();
    }


}