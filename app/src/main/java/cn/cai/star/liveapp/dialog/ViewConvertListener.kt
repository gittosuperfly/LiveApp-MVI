package cn.cai.star.liveapp.dialog

import android.os.Parcel
import android.os.Parcelable

abstract class ViewConvertListener : Parcelable {
    abstract fun convertView(holder: DialogViewHolder, dialog: BaseDialog)
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {}
}