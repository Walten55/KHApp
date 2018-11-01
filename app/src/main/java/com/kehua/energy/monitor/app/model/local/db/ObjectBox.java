package com.kehua.energy.monitor.app.model.local.db;

import android.app.Application;

import com.kehua.energy.monitor.app.model.entity.MyObjectBox;

import io.objectbox.BoxStore;

/**
 * Created by walten on 2018/6/22.
 */
public class ObjectBox {

    private static ObjectBox _Instance;

    private BoxStore boxStore;

    private ObjectBox(){

    }

    public static ObjectBox get(){
        if(_Instance == null)
            _Instance = new ObjectBox();

        return _Instance;
    }

    public void init(Application application){
        boxStore = MyObjectBox.builder().androidContext(application).build();
    }

    public BoxStore getBoxStore(){
        return boxStore;
    }
}
