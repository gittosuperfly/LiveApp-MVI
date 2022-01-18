package cn.cai.star.liveapp.feature.live.list

import cn.cai.star.liveapp.base.BaseRepository
import cn.cai.star.liveapp.http.LiveApi
import cn.cai.star.liveapp.http.ServerApi
import javax.inject.Inject

class LiveListRepository @Inject constructor(
    private val serverApi: ServerApi,
    private val liveApi: LiveApi
) : BaseRepository() {
    fun getAllLive() = composeRequest(serverApi.all())
}