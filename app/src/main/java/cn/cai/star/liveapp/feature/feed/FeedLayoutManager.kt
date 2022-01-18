package cn.cai.star.liveapp.feature.feed

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class FeedLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false),
    RecyclerView.OnChildAttachStateChangeListener {

    var snapHelper: PagerSnapHelper = PagerSnapHelper()
    var viewPagerListener: OnViewPagerListener? = null
    var diffY = 0

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        view.addOnChildAttachStateChangeListener(this)
        snapHelper.attachToRecyclerView(view)
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        val position = getPosition(view)
        if (0 < diffY) {
            viewPagerListener?.onPageRelease(true, position)
        } else {
            viewPagerListener?.onPageRelease(false, position)
        }
    }

    override fun onChildViewAttachedToWindow(view: View) {

        val position = getPosition(view)
        if (0 == position) {
            viewPagerListener?.onPageSelected(position, false)
        }


    }

    override fun onScrollStateChanged(state: Int) {
        if (RecyclerView.SCROLL_STATE_IDLE == state) {
            val view = snapHelper.findSnapView(this)!!
            val position = getPosition(view)
            viewPagerListener?.onPageSelected(position, position == itemCount - 1)
        }
        super.onScrollStateChanged(state)
    }

    fun setOnViewPagerListener(listener: OnViewPagerListener) {
        viewPagerListener = listener
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        diffY = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

}