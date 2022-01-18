package cn.cai.star.liveapp.feature.live.camera.mvi

sealed class LiveCameraViewEvent {
    data class ShowToast(val message: String) : LiveCameraViewEvent()
    data class StartPushStream(val pushUrl: String) : LiveCameraViewEvent()
    data class CheckAndBack(val hasIntercept: Boolean) : LiveCameraViewEvent()
}

data class LiveCameraViewState(
    val status: LiveStatus = LiveStatus.Initial,
    val roomName: String = ""
)

sealed class LiveCameraViewAction {
    object StartLive : LiveCameraViewAction()
    data class ChangeLiveRoomName(val roomName: String) : LiveCameraViewAction()
    object ClickBack : LiveCameraViewAction()
    object Destroy : LiveCameraViewAction()
}

sealed class LiveStatus {
    object Initial : LiveStatus()
    object Preparing : LiveStatus()
    object Living : LiveStatus()
}