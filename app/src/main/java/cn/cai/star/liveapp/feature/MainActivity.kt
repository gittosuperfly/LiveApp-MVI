package cn.cai.star.liveapp.feature

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cai.lib.toast.ToastUtils
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.dialog.dialogs.BottomMenuDialog
import cn.cai.star.liveapp.feature.feed.FeedFragment
import cn.cai.star.liveapp.feature.live.list.LiveListFragment
import cn.cai.star.liveapp.feature.live.camera.LiveCameraActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val feedPage = FeedFragment()
    private val livePage = LiveListFragment()

    private lateinit var viewPager: ViewPager2
    private lateinit var feedBtn: TextView
    private lateinit var liveBtn: TextView
    private lateinit var addBtn: View
    private lateinit var lineView: View

    private val rxPermissions = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAndRequestPermissions {
            init()
        }
    }

    private fun init() {
        viewPager = findViewById(R.id.homeVP)
        feedBtn = findViewById(R.id.feedBtn)
        liveBtn = findViewById(R.id.liveBtn)
        addBtn = findViewById(R.id.addBtn)
        lineView = findViewById(R.id.line)

        feedBtn.setOnClickListener {
            viewPager.currentItem = 0
        }
        liveBtn.setOnClickListener {
            viewPager.currentItem = 1
        }

        addBtn.setOnClickListener {
            BottomMenuDialog.Builder()
                .addItem("拍视频") { ToastUtils.show("敬请期待") }
                .addItem("开直播") {
                    LiveCameraActivity.start(this)
                }
                .build().show(this)
        }

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    feedPage
                } else {
                    livePage
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position == 0) {
                    lineView.translationX = (liveBtn.x - feedBtn.x) * positionOffset
                } else {
                    lineView.translationX = liveBtn.x - feedBtn.x
                }
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    setFeedState()
                    feedPage.replay()
                } else {
                    setLiveState()
                    livePage.refresh()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (viewPager.currentItem == 0) {
            feedPage.replay()
        } else {
            livePage.refresh()
        }
    }


    private fun setFeedState() {
        feedBtn.setTextColor(getColor(R.color.theme))
        liveBtn.setTextColor(getColor(R.color.white_1))
    }

    private fun setLiveState() {
        liveBtn.setTextColor(getColor(R.color.theme))
        feedBtn.setTextColor(getColor(R.color.white_1))
    }

    private fun checkAndRequestPermissions(success: () -> Unit) {
        rxPermissions.request(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_PHONE_STATE
        ).subscribe {
            if (it) {
                success()
            } else {
                ToastUtils.show("请给我权限")
            }
        }
    }
}