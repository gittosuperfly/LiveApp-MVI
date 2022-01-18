package cn.cai.star.liveapp.feature.live.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import cai.lib.toast.ToastUtils
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.dialog.BaseDialog
import cn.cai.star.liveapp.dialog.dialogs.ConfirmDialog
import cn.cai.star.liveapp.dialog.dialogs.LoadingDialog
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewAction
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewEvent
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveCameraViewState
import cn.cai.star.liveapp.feature.live.camera.mvi.LiveStatus
import cn.cai.star.liveapp.utils.observeEvent
import cn.cai.star.liveapp.utils.observeState
import cn.cai.star.liveapp.utils.toast
import cn.cai.star.liveapp.utils.ui.setOnFilterClickListener
import dagger.hilt.android.AndroidEntryPoint
import me.lake.librestreaming.ws.StreamLiveCameraView

import me.lake.librestreaming.ws.StreamAVOption
import java.util.*

@AndroidEntryPoint
class LiveCameraActivity : AppCompatActivity() {

    private lateinit var cameraView: StreamLiveCameraView
    private lateinit var startBtn: View
    private lateinit var titleEdit: EditText
    private lateinit var titleTv: TextView
    private lateinit var closeBtn: View
    private lateinit var messageTv: TextView

    private val viewModel: LiveCameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_camera)
        initView()

        viewModel.viewStates.run {
            observeState(this@LiveCameraActivity, LiveCameraViewState::status) {
                when (it) {
                    is LiveStatus.Initial -> setInitUI()
                    is LiveStatus.Preparing -> setPreparingUI()
                    is LiveStatus.Living -> setLivingUI()
                }
            }
        }

        viewModel.viewEvents.observeEvent(this) {
            when (it) {
                is LiveCameraViewEvent.ShowToast -> toast(it.message)
                is LiveCameraViewEvent.StartPushStream -> startPushStream(it.pushUrl)
                is LiveCameraViewEvent.CheckAndBack -> backCheck(it.hasIntercept)
            }
        }
    }


    private fun initView() {
        cameraView = findViewById(R.id.stream_previewView)
        startBtn = findViewById(R.id.startBtn)
        titleEdit = findViewById(R.id.titleEdit)
        titleTv = findViewById(R.id.titleTv)
        closeBtn = findViewById(R.id.closeBtn)
        messageTv = findViewById(R.id.msg)

        cameraView.init(this, StreamAVOption())

        closeBtn.setOnFilterClickListener {
            viewModel.dispatch(LiveCameraViewAction.ClickBack)
        }
        startBtn.setOnFilterClickListener {
            viewModel.dispatch(LiveCameraViewAction.ChangeLiveRoomName(titleEdit.text.toString()))
            viewModel.dispatch(LiveCameraViewAction.StartLive)
        }
    }

    private fun setInitUI() {
        LoadingDialog.close()
        titleEdit.visibility = View.VISIBLE
        startBtn.visibility = View.VISIBLE
        titleTv.visibility = View.INVISIBLE
        messageTv.visibility = View.INVISIBLE
    }

    private fun setPreparingUI() {
        LoadingDialog.show()
        messageTv.text = "直播连线中..."
        titleEdit.visibility = View.INVISIBLE
        startBtn.visibility = View.INVISIBLE
        titleTv.visibility = View.VISIBLE
        messageTv.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun setLivingUI() {
        LoadingDialog.close()
        messageTv.text = "正在直播中, 直播间ID : ${viewModel.key}"
        titleEdit.visibility = View.INVISIBLE
        startBtn.visibility = View.INVISIBLE
        titleTv.visibility = View.VISIBLE
        messageTv.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispatch(LiveCameraViewAction.Destroy)
        cameraView.stopStreaming()
        cameraView.stopRecord()
        cameraView.destroy()
    }

    override fun onBackPressed() {
        viewModel.dispatch(LiveCameraViewAction.ClickBack)
    }

    private fun backCheck(hasInterceptBack: Boolean) {
        if (hasInterceptBack) {
            ConfirmDialog.Builder()
                .setTitle("警告")
                .setContent("直播正在进行中，您确定要退出直播吗？")
                .build(object : ConfirmDialog.DialogClickListener {
                    override fun onClickConfirm(dialog: BaseDialog) {
                        finish()
                        dialog.dismiss()
                    }

                    override fun onClickCancel(dialog: BaseDialog) {
                        ToastUtils.show("已取消")
                        dialog.dismiss()
                    }
                }).show(this)
        } else {
            finish()
        }
    }


    private fun startPushStream(pushUrl: String) {
        cameraView.startStreaming(pushUrl)
    }


    companion object {
        private const val TAG = "LiveCameraActivity"
        fun start(context: Activity) {
            context.startActivity(Intent(context, LiveCameraActivity::class.java))
        }
    }


}