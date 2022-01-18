package cn.cai.star.liveapp.views

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.utils.AnimUtil
import kotlin.random.Random

/**
 *
 */
@SuppressLint("UseCompatLoadingForDrawables")
class LikeView(context: Context) : ConstraintLayout(context) {

    private var likeListener: OnLikeListener? = null

    //随机爱心的旋转角度
    private var num = floatArrayOf(-35f, -25f, 0f, 25f, 35f)

    private var lastClickTime = 0L
    private var loveDrawable: Drawable = context.getDrawable(R.mipmap.love_red)!!

    constructor(context: Context, attrs: AttributeSet) : this(context) {}

    fun setOnLikeListener(likeListener: OnLikeListener) {
        this.likeListener = likeListener
    }

    //用这个来判断是否是双击事件，判断数组中pos=1的点击事件的时间与数组中pos=0的点击事件的时间差值是否小于500，若是小于500认为是双击事件，这时需要绘制爱心图片
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val currentClickTime = SystemClock.uptimeMillis()

        if (lastClickTime >= currentClickTime - 500) {

            likeListener?.like()

            val loveIv = ImageView(context)

            //设置展示图片的大小
            val loveIvParams = LayoutParams(300, 300)

            //设置图片的相对坐标是父布局的左上角开始的，防止RTL问题
            loveIvParams.leftToLeft = 0
            loveIvParams.topToTop = 0

            //设置图片相对于点击位置的坐标
            loveIvParams.leftMargin = (event?.x!! - 150F).toInt()
            loveIvParams.topMargin = (event.y - 230F).toInt()

            //设置图片资源
            loveIv.setImageDrawable(loveDrawable)
            loveIv.layoutParams = loveIvParams

            //把IV添加到父布局中
            addView(loveIv)

            val animatorSet = AnimatorSet()
            animatorSet.play(
                AnimUtil.scaleX(loveIv, 2f, 0.9f, 100, 0)
            )
                .with(AnimUtil.scaleY(loveIv, 2f, 0.9f, 100, 0))
                .with(AnimUtil.rotation(loveIv, 0, 0, num[Random.nextInt(4)]))
                .with(AnimUtil.alpha(loveIv, 0F, 1F, 100, 0))
                .with(AnimUtil.scaleX(loveIv, 0.9f, 1F, 50, 150))
                .with(AnimUtil.scaleY(loveIv, 0.9f, 1F, 50, 150))
                .with(AnimUtil.translationY(loveIv, 0f, -600F, 800, 400))
                .with(AnimUtil.alpha(loveIv, 1F, 0F, 300, 400))
                .with(AnimUtil.scaleX(loveIv, 1F, 3f, 700, 400))
                .with(AnimUtil.scaleY(loveIv, 1F, 3f, 700, 400))

            animatorSet.start()

            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    //当动画结束，把控件从父布局移除
                    removeViewInLayout(loveIv)
                }
            })

        }
        lastClickTime = SystemClock.uptimeMillis()
        return super.onTouchEvent(event)
    }


    interface OnLikeListener {
        fun like()
    }


}