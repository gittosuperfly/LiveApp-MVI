package cn.cai.star.liveapp.http

import cn.cai.star.liveapp.bean.LiveGoData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveApi {
    @GET("control/reset")
    fun start(
        @Query("room") room: String
    ): Observable<LiveGoData>
}