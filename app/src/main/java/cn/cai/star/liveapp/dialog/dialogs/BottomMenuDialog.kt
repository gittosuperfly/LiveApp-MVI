package cn.cai.star.liveapp.dialog.dialogs

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cai.star.liveapp.R
import cn.cai.star.liveapp.app.App
import cn.cai.star.liveapp.dialog.BaseDialog
import cn.cai.star.liveapp.dialog.DialogViewHolder
import cn.cai.star.liveapp.utils.ui.ViewUtils
import cn.cai.star.liveapp.utils.ui.setOnFilterClickListener


class BottomMenuDialog private constructor() : BaseDialog() {

    private var itemList = ArrayList<MenuItem>()

    override fun intLayoutId(): Int = R.layout.dialog_bottom_menu

    override fun convertView(holder: DialogViewHolder, dialog: BaseDialog) {
        val recyclerView = holder.getView<RecyclerView>(R.id.dialogRv)!!
        val adapter = BottomMenuAdapter(itemList, this)
        val manager = LinearLayoutManager(activity)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        holder.getView<View>(R.id.cancelTv)!!.setOnClickListener {
            dismiss()
        }
    }

    class Builder {
        private val instance = BottomMenuDialog()
        private var itemList = ArrayList<MenuItem>()

        fun addItem(text: String, onClick: () -> Unit) = apply {
            itemList.add(MenuItem(text, null, onClick))
        }

        fun addItem(text: String, @ColorRes color: Int, onClick: () -> Unit) = apply {
            itemList.add(MenuItem(text, color, onClick))
        }

        fun build(): BaseDialog = instance
            .setOutCancel(true)
            .setGravity(Gravity.BOTTOM)
            .apply {
                instance.itemList = itemList
            }
    }

    class BottomMenuAdapter(
        private val itemList: ArrayList<MenuItem>,
        private val dialog: BaseDialog,
    ) :
        RecyclerView.Adapter<BottomMenuAdapter.ViewHolder>() {

        class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val item: View = itemView
            val line: View = itemView.findViewById(R.id.line)
            val text: TextView = itemView.findViewById(R.id.text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ViewUtils.inflate(parent, R.layout.item_menu_dialog_rv_item))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            if (position == itemList.size - 1) {
//                holder.line.visibility = View.INVISIBLE
//            }
            holder.text.text = itemList[position].text
            if (itemList[position].color != null) {
                holder.text.setTextColor(App.getColor(itemList[position].color!!))
            }
            holder.item.setOnFilterClickListener {
                itemList[position].onClick()
                dialog.dismiss()
            }
        }

        override fun getItemCount(): Int = itemList.size
    }

    data class MenuItem(val text: String, @ColorRes val color: Int? = null, val onClick: () -> Unit)

}