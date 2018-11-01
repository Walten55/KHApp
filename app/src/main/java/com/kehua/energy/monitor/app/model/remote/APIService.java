package com.kehua.energy.monitor.app.model.remote;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIService {
    @POST
    @FormUrlEncoded
    Flowable<Object> testPost(@Url String url, @FieldMap Map<String,Object> data);

    @GET
    Flowable<Object> testGet(@Url String url,@QueryMap Map<String,Object> data);
}
