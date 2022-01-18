package cn.cai.star.liveapp.feature.live.camera.data

import cn.cai.star.liveapp.base.BaseRepository
import cn.cai.star.liveapp.http.LiveApi
import cn.cai.star.liveapp.http.ServerApi
import javax.inject.Inject


class LiveCameraDataRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val liveApi: LiveApi
) : BaseRepository() {
    fun start(key: String) = composeRequest(liveApi.start(key))
    fun ping(key: String) = composeRequest(serverApi.ping(key))
    fun start(key: String, title: String) = composeRequest(serverApi.start(key, title, ""))
}