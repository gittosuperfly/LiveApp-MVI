package cn.cai.star.liveapp.utils.ui

import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver

private const val FILTER_CLICK_INTERVAL = 1000L
private const val GLOBAL_FILTER_CLICK_INTERVAL = 300L

private var sGlobalLastClickTime = SystemClock.elapsedRealtime()

fun <T : View> T.setOnFilterClickListener(
    interval: Long = FILTER_CLICK_INTERVAL,
    block: (T) -> Unit
) {
    val lastClickTime = arrayOf(0L)
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastClickTime.first() > interval) {
            block(this)

            lastClickTime[0] = SystemClock.elapsedRealtime()
        }
    }
}

fun <T : View> T.setOnGlobalFilterClickListener(
    interval: Long = GLOBAL_FILTER_CLICK_INTERVAL,
    block: (T) -> Unit
) {
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - sGlobalLastClickTime > interval) {
            block(this)

            sGlobalLastClickTime = SystemClock.elapsedRealtime()
        }
    }
}

fun View.addOnGlobalLayoutListener(block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)

            block()
        }
    })
}

fun View.addOnPreDrawListener(block: () -> Boolean) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)

            return block()
        }
    })
}

fun View.addOnDrawListener(block: () -> Unit) {
    viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
        override fun onDraw() {
            viewTreeObserver.removeOnDrawListener(this)

            block()
        }
    })
}
