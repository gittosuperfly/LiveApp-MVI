package cn.cai.star.liveapp.bean

import cn.cai.star.liveapp.R

class FeedInfo constructor(val imgResId: Int, val videoResId: Int) {
    companion object {
        fun getMockList() = ArrayList<FeedInfo>().apply {
            add(FeedInfo(R.mipmap.im, R.raw.v))
            add(FeedInfo(R.mipmap.im1, R.raw.v1))
            add(FeedInfo(R.mipmap.im2, R.raw.v2))
            add(FeedInfo(R.mipmap.im3, R.raw.v3))
            add(FeedInfo(R.mipmap.im4, R.raw.v4))
            add(FeedInfo(R.mipmap.im5, R.raw.v5))
            add(FeedInfo(R.mipmap.im6, R.raw.v6))
        }
    }
}