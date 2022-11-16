package com.shuoxd.charge.base

import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.service.charge.ChargeManager
import com.shuoxd.lib.LibApplication
import com.shuoxd.lib.service.ServiceManager
import com.shuoxd.lib.service.account.IAccountService
import com.shuoxd.lib.service.device.IDeviceService
import com.shuoxd.lib.service.device.Language
import com.shuoxd.lib.service.http.IHttpService
import com.shuoxd.lib.service.location.ILocationService
import com.shuoxd.lib.service.storage.IStorageService

abstract class BaseActivity : AppCompatActivity(), ServiceManager.ServiceInterface, IDisplay,
    ChargeManager.ServiceInterface {

    private val display: IDisplay by lazy(LazyThreadSafetyMode.NONE) {
        AndroidDisplay(this)
    }



    /**
     * 多语言初始化
     */
    override fun attachBaseContext(newBase: Context?) {
        val appLanguage = LibApplication.instance().deviceService().getAppLanguage()
        if (appLanguage == Language.FOLLOW_SYSTEM) {
            super.attachBaseContext(newBase)
            return
        }

        super.attachBaseContext(newBase?.resources?.configuration?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setLocales(LocaleList(appLanguage.locale))
            } else {
                setLocale(appLanguage.locale)
            }
        }?.let { newBase.createConfigurationContext(it) })
    }

    override fun apiService(): IHttpService {
        return LibApplication.instance().apiService()
    }

    override fun storageService(): IStorageService {
        return LibApplication.instance().storageService()
    }

    override fun deviceService(): IDeviceService {
        return LibApplication.instance().deviceService()
    }

    override fun accountService(): IAccountService {
        return LibApplication.instance().accountService()
    }

    override fun locationService(): ILocationService {
        return LibApplication.instance().locationService()
    }


    override fun setChargeList(list: MutableList<ChargeModel>) {
        ChargeManager.getInstance().setChargeList(list)
    }


    override fun getChargeList(): MutableList<ChargeModel> {
        return ChargeManager.getInstance().getChargeList()
    }


    override fun setCurrenChargeModel(chargeModel: ChargeModel) {
        ChargeManager.getInstance().setCurrentCharge(chargeModel)

    }

    override fun getCurrentChargeModel(): ChargeModel? {

        return ChargeManager.getInstance().getCurrentCharge()
    }



    override fun showDialog() {
        display.showDialog()
    }

    override fun dismissDialog() {
        display.dismissDialog()
    }

    override fun showPageErrorView(onRetry: ((view: View) -> Unit)) {
        display.showPageErrorView(onRetry)
    }

    override fun hidePageErrorView() {
        display.hidePageErrorView()
    }

    override fun showPageLoadingView() {
        display.showPageLoadingView()
    }

    override fun hidePageLoadingView() {
        display.hidePageLoadingView()
    }


    override fun showResultDialog(
        result: String?,
        onCancelClick: (() -> Unit)?,
        onComfirClick: (() -> Unit)?
    ) {
        display.showResultDialog(result,onCancelClick,onComfirClick)
    }



}