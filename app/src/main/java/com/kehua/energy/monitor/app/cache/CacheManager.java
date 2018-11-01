package com.kehua.energy.monitor.app.cache;

import android.util.SparseArray;

import com.kehua.energy.monitor.app.model.entity.DeviceData;

public class CacheManager {

    private static CacheManager instance;

    private SparseArray<DeviceData> cache = new SparseArray<>();

    private CacheManager() {

    }

    public static synchronized CacheManager getInstance() {
        if (instance == null)
            instance = new CacheManager();

        return instance;
    }

    public void put(int address,DeviceData data){
        synchronized (cache){
            cache.put(address,data);
        }
    }

    public DeviceData get(int address){
        return cache.get(address);
    }

    public void remove(int address){
       cache.remove(address);
    }

    public void destroy(){
        cache.clear();
    }
}
