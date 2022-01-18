package cn.cai.star.liveapp.dialog.dialogs

import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.app.App
import cn.cai.star.liveapp.dialog.BaseDialog
import cn.cai.star.liveapp.dialog.DialogViewHolder

class LoadingDialog private constructor() : BaseDialog() {

    private lateinit var loadingImage: ImageView

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private lateinit var thread: Thread

    private var angle = 0f

    override fun intLayoutId(): Int = R.layout.dialog_loading

    override fun convertView(holder: DialogViewHolder, dialog: BaseDialog) {
        loadingImage = holder.getView(R.id.loadingImg)!!
        thread = Thread {
            while (true) {
                handler.post {
                    loadingImage.rotation = angle
                    changeAngle()
                }
                try {
                    Thread.sleep(30)
                } catch (ex: InterruptedException) {
                    return@Thread
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        thread.start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        thread.interrupt()
    }

    private fun changeAngle() {
        if (angle == -360f) {
            angle = 0f
        }
        angle -= 15
    }

    companion object {

        @Volatile
        private var hasShow = false

        private val instance = LoadingDialog()
            .setDimAmount(0f)
            .setOutCancel(false)
            .setMargin(60)

        @JvmStatic
        fun show() {
            if (!hasShow) {
                hasShow = true
                instance.show(App.getCurrentActivity())
            }
        }

        @JvmStatic
        fun close() {
            if (hasShow) {
                instance.dismiss()
            }
            hasShow = false
        }
    }

}