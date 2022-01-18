package cn.cai.star.liveapp.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity

/**
 * 提供Context
 */
object App {

    private lateinit var context: Application

    @JvmStatic
    fun setAppContext(application: Application) {
        context = application
    }

    @JvmStatic
    fun getAppContext(): Context = context

    @JvmStatic
    fun getColor(@ColorRes resId: Int): Int = context.getColor(resId)

    @JvmStatic
    fun getString(@StringRes resId: Int): String = context.getString(resId)

    @JvmStatic
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(@DrawableRes resId: Int): Drawable? = context.getDrawable(resId)

    @JvmStatic
    fun setLogCallback(application: Application) {
        application.registerActivityLifecycleCallbacks(ActivityLifeLoggerCallback)
    }

    @JvmStatic
    fun getCurrentActivity(): FragmentActivity? {
        val activity = ActivityLifeLoggerCallback.currentActivity?.get()
        if (activity is FragmentActivity) {
            return activity
        }
        return null
    }

    fun finishCurrentActivity() {
        getCurrentActivity()?.finish()
    }

}