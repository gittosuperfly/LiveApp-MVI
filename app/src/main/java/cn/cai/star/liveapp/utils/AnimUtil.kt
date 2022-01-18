package cn.cai.star.liveapp.utils

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.LinearInterpolator

object AnimUtil {

    //vararg可变参数修饰符，此处可以传入多个Float类型值
    fun rotation(view: View, time: Long, delayTime: Long, vararg values: Float): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "rotation", *values)
        ani.duration = time
        ani.startDelay = delayTime
        ani.interpolator = TimeInterpolator { input -> input }
        return ani
    }

    fun alpha(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "alpha", from, to)
        ani.interpolator = LinearInterpolator()
        ani.duration = time
        ani.startDelay = delayTime
        return ani
    }

    fun translationX(
        view: View,
        from: Float,
        time: Long,
        to: Float,
        delayTime: Long
    ): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "translationX", from, to)
        ani.startDelay = delayTime
        ani.duration = time
        ani.interpolator = LinearInterpolator()
        return ani
    }

    fun translationY(
        view: View,
        from: Float,
        to: Float,
        time: Long,
        delayTime: Long
    ): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "translationY", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    fun scaleX(
        view: View,
        from: Float,
        to: Float,
        time: Long,
        delayTime: Long
    ): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "scaleX", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    fun scaleY(
        view: View,
        from: Float,
        to: Float,
        time: Long,
        delayTime: Long
    ): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "scaleY", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }
}