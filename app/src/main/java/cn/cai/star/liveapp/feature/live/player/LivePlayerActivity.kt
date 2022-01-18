package cn.cai.star.liveapp.feature.live.player

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cai.lib.toast.ToastUtils
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.dialog.BaseDialog
import cn.cai.star.liveapp.dialog.dialogs.ConfirmDialog
import cn.cai.star.liveapp.feature.live.LiveUtils
import cn.cai.star.liveapp.utils.ui.setOnFilterClickListener
import dagger.hilt.android.AndroidEntryPoint
import tcking.github.com.giraffeplayer2.GiraffePlayer
import tcking.github.com.giraffeplayer2.PlayerListener
import tcking.github.com.giraffeplayer2.ProxyPlayerListener
import tcking.github.com.giraffeplayer2.VideoView

@AndroidEntryPoint
class LivePlayerActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var titleTv: TextView
    private lateinit var closeBtn: View

    private lateinit var title: String
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_player)
        videoView = findViewById(R.id.videoView)
        titleTv = findViewById(R.id.titleTv)
        closeBtn = findViewById(R.id.closeBtn)

        if (intent.hasExtra(EXTRA_TITLE) && intent.hasExtra(EXTRA_KEY)) {
            title = intent.getStringExtra(EXTRA_TITLE)!!
            key = intent.getStringExtra(EXTRA_KEY)!!
        } else {
            ToastUtils.show("参数不正确")
        }
        ToastUtils.show("进入直播间: $title , 直播间key = $key")
        videoView.setVideoPath(LiveUtils.appendUrl(key)).player.start()
        titleTv.text = title
        closeBtn.setOnFilterClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.player.stop()
        videoView.player.release()
    }

    companion object {
        private const val EXTRA_TITLE = "ex_title"
        private const val EXTRA_KEY = "ex_key"
        fun start(context: Activity, title: String, key: String) {
            val intent = Intent(context, LivePlayerActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_KEY, key)
            context.startActivity(intent)
        }
    }

}