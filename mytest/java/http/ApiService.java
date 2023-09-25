package com.example.aidltest.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Observable<String> getAddress(@Url String url);
}
