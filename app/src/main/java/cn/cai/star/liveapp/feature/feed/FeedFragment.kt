package cn.cai.star.liveapp.feature.feed

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.base.BaseFragment
import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : BaseFragment() {

    private var curPosition = 0

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var feedLayoutManager: FeedLayoutManager
    private lateinit var feedAdapter: FeedRecyclerAdapter

    private var mediaPlayer: MediaPlayer? = null

    private var hasInit = false

    override fun getLayoutResId(): Int = R.layout.fragment_feed
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedRecyclerView = view.findViewById(R.id.feedRv)
        feedLayoutManager = FeedLayoutManager(requireActivity())
        feedAdapter = FeedRecyclerAdapter(requireActivity())

        feedLayoutManager.setOnViewPagerListener(object : OnViewPagerListener {
            override fun onPageRelease(isNext: Boolean, position: Int) {
                releaseVideo(position)
            }

            override fun onPageSelected(position: Int, isBottom: Boolean) {
                curPosition = position
                playVideo(position)
            }
        })

        feedRecyclerView.layoutManager = feedLayoutManager
        feedRecyclerView.adapter = feedAdapter

        hasInit = true
    }

    fun releaseVideo(position: Int) {
        val itemView = feedRecyclerView.layoutManager!!.findViewByPosition(position)!!
        val videoView = itemView.findViewById<VideoView>(R.id.video)
        val imageView = itemView.findViewById<ImageView>(R.id.cover)
        imageView.alpha = 1f
        videoView.alpha = 0f
        videoView.stopPlayback()
    }

    fun playVideo(position: Int) {
        val itemView = feedRecyclerView.layoutManager!!.findViewByPosition(position)!!
        val videoView = itemView.findViewById<VideoView>(R.id.video)
        val imageView = itemView.findViewById<ImageView>(R.id.cover)
        videoView.setOnInfoListener { mp, what, extra ->
            mediaPlayer = mp
            mp.isLooping = true
            imageView.alpha = 0f
            videoView.alpha = 1f
            false
        }
        videoView.start()
    }

    fun replay() {
        if (hasInit) {
            playVideo(curPosition)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        mediaPlayer?.release()
    }
}