package cn.cai.star.liveapp.http

import cn.cai.star.liveapp.bean.LiveInfo
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {

    @GET("start")
    fun start(
        @Query("key") key: String,
        @Query("title") title: String,
        @Query("cover") cover: String,
    ): Observable<Int>

    @GET("ping")
    fun ping(
        @Query("key") key: String
    ): Observable<Int>

    @GET("all")
    fun all(): Observable<List<LiveInfo>>


    companion object {
        fun get() = Retrofit.Builder()
            .client(HttpProvider.getInstance().get())
            .baseUrl("http://49.232.112.170:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(ServerApi::class.java)


    }


}