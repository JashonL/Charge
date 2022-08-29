package com.shuoxd.charge.ui.chargesetting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.TransactionModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class RateViewModel : BaseViewModel() {

    var rateLiveData = MutableLiveData<Pair<Boolean, String>>()


    fun setRate(chargeId: String, operationType: String, operationValue: String) {
        val params = hashMapOf<String, String>().apply {
            put("chargeId", chargeId)
            put("operationType", operationType)
            put("operationValue", operationValue)
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.SETRATE,
                params,
                object : HttpCallback<HttpResult<TransactionModel>>() {
                    override fun success(result: HttpResult<TransactionModel>) {
                        val msg = result.msg
                        if (result.isBusinessSuccess()) {
                            rateLiveData.value = Pair(
                                true,
                                msg ?: MainApplication.instance().getString(
                                    R.string.m131_set_success
                                )
                            )

                        } else {
                            rateLiveData.value = Pair(
                                true,
                                msg ?: MainApplication.instance().getString(
                                    R.string.m132_set_fail
                                )
                            )
                        }
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        rateLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                    }

                })

        }


    }


}