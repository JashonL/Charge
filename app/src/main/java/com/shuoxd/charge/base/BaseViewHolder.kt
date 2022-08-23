package com.shuoxd.charge.base

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.shuoxd.charge.application.MainApplication
import com.growatt.lib.service.ServiceManager
import com.growatt.lib.service.account.IAccountService
import com.growatt.lib.service.device.IDeviceService
import com.growatt.lib.service.http.IHttpService
import com.growatt.lib.service.location.ILocationService
import com.growatt.lib.service.storage.IStorageService
import com.shuoxd.charge.base.BaseActivity

open class BaseViewHolder(
    itemView: View,
    private val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.ViewHolder(itemView),
    ServiceManager.ServiceInterface, View.OnClickListener, View.OnLongClickListener {

    fun showDialog() {
        (itemView.context as? BaseActivity)?.showDialog()
    }

    fun dismissDialog() {
        (itemView.context as? BaseActivity)?.dismissDialog()
    }

    override fun apiService(): IHttpService {
        return MainApplication.instance().apiService()
    }

    override fun storageService(): IStorageService {
        return MainApplication.instance().storageService()
    }

    override fun deviceService(): IDeviceService {
        return MainApplication.instance().deviceService()
    }

    override fun accountService(): IAccountService {
        return MainApplication.instance().accountService()
    }

    override fun locationService(): ILocationService {
        return MainApplication.instance().locationService()
    }

    override fun onClick(v: View?) {
        onItemClickListener?.onItemClick(v, bindingAdapterPosition)
    }

    fun getColor(@ColorRes colorId: Int): Int {
        return MainApplication.instance().resources.getColor(colorId)
    }

    fun getString(@StringRes stringId: Int): String {
        return MainApplication.instance().getString(stringId)
    }

    override fun onLongClick(v: View?): Boolean {
        onItemClickListener?.onItemLongClick(v, bindingAdapterPosition)
        return true
    }

}

interface OnItemClickListener {

    fun onItemClick(v: View?, position: Int) {}

    fun onItemLongClick(v: View?, position: Int) {}
}