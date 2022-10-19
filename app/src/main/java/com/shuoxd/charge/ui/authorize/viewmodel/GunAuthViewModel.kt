package com.shuoxd.charge.ui.authorize.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.AuthModel
import com.shuoxd.charge.model.charge.GunAuthModel
import com.shuoxd.charge.model.charge.ScheduledModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.service.http.PageModel
import kotlinx.coroutines.launch

class GunAuthViewModel : BaseViewModel() {


    var gunAuthLiveData = MutableLiveData<Pair<GunAuthModel?, String?>>()
    val setGunAuthLiveData=MutableLiveData<Pair<Boolean,String>>()

    fun connectorAuthorizeStatus(chargerId: String?, connectorId: Int) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId.toString())
        }

        apiService().postForm(ApiPath.Charge.CONNECTORAUTHORIZESTATUS, params,
            object : HttpCallback<HttpResult<GunAuthModel>>() {

                override fun success(result: HttpResult<GunAuthModel>) {
                    if (result.isBusinessSuccess()) {
                        gunAuthLiveData.value = Pair(result.obj, null)
                    } else {
                        gunAuthLiveData.value = Pair(null, result.msg ?: "")
                    }
                }
                override fun onFailure(errorModel: HttpErrorModel) {
                    gunAuthLiveData.value = Pair(null, errorModel.errorMsg ?: "")
                }

            })


    }




    /**
     * 设置预约充电数据
     */
    fun setScheduledCharging(chargerId: String,
                             connectorId:String,
                             authorize:String
    ) {

        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId)
            put("connectorId", connectorId)
            put("authorize", authorize)
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.AUTHORIZECONNECTOR,
                params,
                object : HttpCallback< HttpResult<ScheduledModel>>() {
                    override fun success(result: HttpResult<ScheduledModel>) {
                        val msg = result.msg
                        if (result.isBusinessSuccess()) {
                            setGunAuthLiveData.value = Pair(result.isBusinessSuccess(),
                                msg?: MainApplication.instance().getString(
                                    R.string.m131_set_success))
                        }else{
                            setGunAuthLiveData.value = Pair(result.isBusinessSuccess(),
                                msg?: MainApplication.instance().getString(
                                    R.string.m132_set_fail))
                        }

                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        setGunAuthLiveData.value = Pair(false,errorModel.errorMsg ?: "")
                    }

                })
        }


    }



}