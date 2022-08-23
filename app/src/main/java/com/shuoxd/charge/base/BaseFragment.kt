package com.shuoxd.charge.base

import android.view.View
import androidx.fragment.app.Fragment
import com.shuoxd.charge.application.MainApplication
import com.growatt.lib.LibApplication
import com.growatt.lib.service.ServiceManager
import com.growatt.lib.service.account.IAccountService
import com.growatt.lib.service.device.IDeviceService
import com.growatt.lib.service.http.IHttpService
import com.growatt.lib.service.location.ILocationService
import com.growatt.lib.service.storage.IStorageService

abstract class BaseFragment : Fragment(), ServiceManager.ServiceInterface, IDisplay {

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
        return LibApplication.instance().locationService()
    }

    override fun showDialog() {
        (activity as? BaseActivity)?.showDialog()
    }

    override fun dismissDialog() {
        (activity as? BaseActivity)?.dismissDialog()
    }

    override fun showPageErrorView(onRetry: ((view: View) -> Unit)) {
        (activity as? BaseActivity)?.showPageErrorView(onRetry)
    }

    override fun hidePageErrorView() {
        (activity as? BaseActivity)?.hidePageErrorView()
    }

    override fun showPageLoadingView() {
        (activity as? BaseActivity)?.showPageLoadingView()
    }

    override fun hidePageLoadingView() {
        (activity as? BaseActivity)?.hidePageLoadingView()
    }
}