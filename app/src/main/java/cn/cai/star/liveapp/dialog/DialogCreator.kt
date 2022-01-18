package cn.cai.star.liveapp.dialog

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes

class DialogCreator : BaseDialog() {
    private var convertListener: ViewConvertListener? = null

    override fun initTheme(): Int {
        return theme
    }

    override fun intLayoutId(): Int {
        return layoutId
    }

    override fun convertView(holder: DialogViewHolder, dialog: BaseDialog) {
        if (convertListener != null) {
            convertListener!!.convertView(holder, dialog)
        }
    }

    fun setTheme(@StyleRes theme: Int): DialogCreator {
        this.theme = theme
        return this
    }

    fun setLayoutId(@LayoutRes layoutId: Int): DialogCreator {
        this.layoutId = layoutId
        return this
    }

    fun setConvertListener(convertListener: ViewConvertListener?): DialogCreator {
        this.convertListener = convertListener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            convertListener = savedInstanceState.getParcelable("listener")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("listener", convertListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        convertListener = null
    }

    companion object {
        fun init(): DialogCreator {
            return DialogCreator()
        }
    }
}