package cn.cai.star.liveapp.utils

import android.content.Context

object AppUtils {

    private lateinit var context: Context

    @JvmStatic
    fun init(context: Context) {
        AppUtils.context = context
    }

    @JvmStatic
    fun getContext() = context
}