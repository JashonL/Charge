package com.shuoxd.charge.ui.smartcharge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.OffPeakModel
import com.shuoxd.charge.model.charge.ScheduledModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class OffpeakViewModel : BaseViewModel() {

    val offpeakLiveData = MutableLiveData<Pair<Boolean, OffPeakModel?>>()

    val setOffpeakLiveData = MutableLiveData<Pair<Boolean, String>>()


    /**
     * 获取非高峰充电数据
     */
    fun getOffPeakChargingByUserId(userId: String?, chargerId: String?, connectorId: String?) {

        val params = hashMapOf<String, String>().apply {
            put("userId", userId ?: "")
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.GETOFFPEAKCHARGINGBYUSERID,
                params,
                object : HttpCallback<HttpResult<OffPeakModel>>() {
                    override fun success(result: HttpResult<OffPeakModel>) {
                        offpeakLiveData.value = Pair(result.isBusinessSuccess(), result.obj)
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        offpeakLiveData.value = Pair(false, null)
                    }

                })
        }


    }


    /**
     * 设置非高峰充电数据
     */
    fun setOffPeakCharging(
        timeList: String,
        chargerId: String?,
        connectorId: String,
        status: String,
    ) {

        val params = hashMapOf<String, String>().apply {
            put("timeList", timeList)
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId)
            put("status", status)

        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.SETOFFPEAKCHARGING,
                params,
                object : HttpCallback<HttpResult<ScheduledModel>>() {
                    override fun success(result: HttpResult<ScheduledModel>) {
                        val msg = result.msg
                        if (result.isBusinessSuccess()) {
                            setOffpeakLiveData.value = Pair(
                                result.isBusinessSuccess(),
                                msg ?: MainApplication.instance().getString(
                                    R.string.m131_set_success
                                )
                            )
                        } else {
                            setOffpeakLiveData.value = Pair(
                                result.isBusinessSuccess(),
                                msg ?: MainApplication.instance().getString(
                                    R.string.m132_set_fail
                                )
                            )
                        }
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        setOffpeakLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                    }

                })
        }


    }


}