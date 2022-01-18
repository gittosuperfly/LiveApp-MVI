package cn.cai.star.liveapp.feature.live.list

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.base.BaseFragment
import cn.cai.star.liveapp.base.BaseRecyclerAdapter
import cn.cai.star.liveapp.bean.LiveInfo
import cn.cai.star.liveapp.feature.live.player.LivePlayerActivity
import cn.cai.star.liveapp.http.ServerApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class LiveListFragment : BaseFragment() {
    companion object {
        private const val TAG = "LivePlazaFragment"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LiveListAdapter
    private lateinit var nullLayout: View

    private val viewModel: LiveListViewModel by activityViewModels()

    override fun getLayoutResId(): Int = R.layout.fragment_live_plaza
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nullLayout = view.findViewById(R.id.nullStateLayout)
        nullLayout.visibility = View.GONE
        adapter = LiveListAdapter(listOf())
        recyclerView = view.findViewById(R.id.liveListRv)
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        recyclerView.adapter = adapter
        adapter.setOnItemDataClickListener { v, position, t ->
            LivePlayerActivity.start(requireActivity(), t.title, t.key)
        }
        nullLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        refresh()
    }

    fun refresh() {
        viewModel.getAllLive().subscribe({
            adapter.refresh(it)
            if (it.isEmpty()) {
                nullLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                nullLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }, {
            Log.e(TAG, "onViewCreated: 加载错误", it)
        })
    }

    class LiveListAdapter constructor(list: List<LiveInfo>) :
        BaseRecyclerAdapter<LiveInfo>(list, R.layout.rv_item_live_list) {
        override fun bindData(holder: BaseViewHolder, position: Int, t: LiveInfo) {
            val titleTv = holder.getView(R.id.liveTitleTv) as TextView
            titleTv.text = t.title

            val imageView: ImageView = holder.getView(R.id.cover) as ImageView

            Glide.with(imageView)
                .asBitmap()
                .load(R.mipmap.im1)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, trans: Transition<in Bitmap>?) {
                        val viewWidth = imageView.width
                        val proportion = resource.height.toFloat() / resource.width.toFloat()
                        imageView.layoutParams.height = (proportion * viewWidth.toFloat()).toInt()
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

        }

    }

}