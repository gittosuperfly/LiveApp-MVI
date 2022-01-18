package cn.cai.star.liveapp.feature.live.camera

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.cai.star.liveapp.base.SingleLiveEvents
import cn.cai.star.liveapp.feature.live.LiveUtils
import cn.cai.star.liveapp.feature.live.camera.data.LiveCameraDataRepository
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewAction
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewEvent
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewState
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveStatus
import cn.cai.star.liveapp.utils.asLiveData
import cn.cai.star.liveapp.utils.setEvent
import cn.cai.star.liveapp.utils.setState
import java.util.*

class LiveCameraViewModel @ViewModelInject constructor(
    private val repo: LiveCameraDataRepository
) : ViewModel() {

    private val _viewStates: MutableLiveData<LiveCameraViewState> =
        MutableLiveData(LiveCameraViewState())
    val viewStates = _viewStates.asLiveData()
    private val _viewEvents: SingleLiveEvents<LiveCameraViewEvent> = SingleLiveEvents()
    val viewEvents = _viewEvents.asLiveData()

    val key: String = UUID.randomUUID().hashCode().toString()

    private var pingRunnable: Runnable? = null

    fun dispatch(viewAction: LiveCameraViewAction) {
        when (viewAction) {
            is LiveCameraViewAction.StartLive -> startLive()
            is LiveCameraViewAction.ChangeLiveRoomName -> changeLiveRoomName(viewAction.roomName)
            is LiveCameraViewAction.Destroy -> stopPing()
            is LiveCameraViewAction.ClickBack -> handleClickBack()
        }
    }

    private fun changeLiveRoomName(roomName: String) {
        _viewStates.setState {
            copy(roomName = roomName)
        }
    }

    private fun changeLiveStatus(status: LiveStatus) {
        _viewStates.setState {
            copy(status = status)
        }
    }

    private fun startStreaming(url: String) {
        _viewEvents.setEvent(LiveCameraViewEvent.StartPushStream(url))
    }

    private fun showToast(message: String) {
        _viewEvents.setEvent(LiveCameraViewEvent.ShowToast(message))
    }

    private fun handleClickBack() {
        _viewEvents.setEvent(LiveCameraViewEvent.CheckAndBack(viewStates.value!!.status is LiveStatus.Living))
    }

    private fun startLive() {
        if (_viewStates.value!!.roomName.isEmpty()) {
            showToast("给你的直播间起个响亮的名字吧~")
        } else {
            changeLiveStatus(status = LiveStatus.Preparing)
            repo.start(key).subscribe({
                val pushUrl = LiveUtils.appendUrl(it.data)
                val pullUrl = LiveUtils.appendUrl(key)
                startStreaming(pushUrl)
                Log.d(TAG, "startLive: 直播推流url : $pushUrl")
                Log.d(TAG, "startLive: 直播拉流url : $pullUrl")
                reportLiveInfo(_viewStates.value!!.roomName)
                startPing()
                changeLiveStatus(status = LiveStatus.Living)
                showToast("开始直播")
            }, {
                changeLiveStatus(status = LiveStatus.Initial)
                showToast("获取推流地址失败")
            })
        }
    }

    /**
     * ping-pong
     */
    private fun startPing() {
        pingRunnable = Runnable {
            repo.ping(key).subscribe({
                Log.d(TAG, "startPing: ping...")
            }, {
                Log.e(TAG, "startPing: ping error", it)
            })
        }
        Thread {
            while (pingRunnable != null) {
                Thread.sleep(5000)
                pingRunnable?.run()
            }
        }.start()
    }

    private fun stopPing() {
        pingRunnable = null
    }

    /**
     * 上报直播信息
     */
    private fun reportLiveInfo(title: String) {
        repo.start(key, title)
            .subscribe({
                Log.d(TAG, "reportLiveInfo: 直播信息上报成功")
            }, {
                Log.e(TAG, "reportLiveInfo: error", it)
            })
    }


    companion object {
        private const val TAG = "LiveCameraViewModel"
    }

}