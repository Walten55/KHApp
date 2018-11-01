package com.kehua.energy.monitor.app.model.remote;

import com.google.gson.internal.LinkedTreeMap;
import com.kehua.energy.monitor.app.model.entity.Collector;
import com.kehua.energy.monitor.app.model.entity.InvInfoList;
import com.kehua.energy.monitor.app.model.entity.ModbusResponse;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CollectorAPIService {

    @POST("http://10.10.10.1/staset.cgi")
    Flowable<LinkedTreeMap<String,String>> apset(@Body Map req);

    @POST("http://10.10.10.1/fdbg.cgi")
    Flowable<ModbusResponse> fdbg(@Body Map req);

    @GET("http://10.10.10.1/invinfo.cgi")
    Flowable<InvInfoList> invinfo();

    @GET("http://10.10.10.1/getdev.cgi")
    Flowable<Collector> getdev();
}
