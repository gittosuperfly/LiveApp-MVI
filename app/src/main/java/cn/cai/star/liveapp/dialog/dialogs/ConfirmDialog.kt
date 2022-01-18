package cn.cai.star.liveapp.dialog.dialogs

import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.IntDef
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.dialog.BaseDialog
import cn.cai.star.liveapp.dialog.DialogViewHolder
import cn.cai.star.liveapp.utils.ui.setOnFilterClickListener

class ConfirmDialog private constructor() : BaseDialog() {

    private lateinit var listener: DialogClickListener

    private var title = ""
    private var content = ""

    private var cancelText = ""
    private var confirmText = ""

    private var mode: Int = Mode.ALL

    override fun intLayoutId(): Int = R.layout.dialog_confirm

    override fun convertView(holder: DialogViewHolder, dialog: BaseDialog) {
        holder.getView<TextView>(R.id.dialogTitle)!!.apply {
            if (title.isEmpty()) {
                visibility = View.GONE
            } else {
                text = title
            }
        }

        holder.getView<TextView>(R.id.dialogContent)!!.apply {
            if (content.isEmpty()) {
                visibility = View.GONE
            } else {
                text = content
            }
        }

        holder.getView<TextView>(R.id.dialogYesTv)!!.apply {
            if (mode == Mode.CANCEL) {
                visibility = View.GONE
            } else {
                if (confirmText.isNotEmpty()) {
                    text = confirmText
                }
                setOnFilterClickListener {
                    listener.onClickConfirm(this@ConfirmDialog)
                }
            }
        }

        holder.getView<TextView>(R.id.dialogNoTv)!!.apply {
            if (mode == Mode.CONFIRM) {
                visibility = View.GONE
            } else {
                if (cancelText.isNotEmpty()) {
                    text = cancelText
                }
                setOnFilterClickListener {
                    listener.onClickCancel(this@ConfirmDialog)
                }
            }
        }
    }

    class Builder {
        private val instance = ConfirmDialog()

        fun build(listener: DialogClickListener) = instance
            .setOutCancel(false)
            .setGravity(Gravity.CENTER)
            .setMargin(48)!!
            .apply {
                instance.listener = listener
            }

        fun setTitle(title: String) = apply {
            instance.title = title
        }

        fun setContent(content: String) = apply {
            instance.content = content
        }

        fun setMode(@Mode mode: Int) = apply {
            instance.mode = mode
        }

        fun setConfirmText(text: String) = apply {
            instance.confirmText = text
        }

        fun setCancelText(text: String) = apply {
            instance.cancelText = text
        }
    }

    interface DialogClickListener {
        fun onClickConfirm(dialog: BaseDialog)
        fun onClickCancel(dialog: BaseDialog)
    }

    @IntDef(
        Mode.CANCEL,
        Mode.CONFIRM,
        Mode.ALL,
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode {
        companion object {
            const val CANCEL = 0
            const val CONFIRM = 1
            const val ALL = 2
        }
    }
}