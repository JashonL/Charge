package com.shuoxd.charge.ui.charge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.model.charge.TransactionModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class ChargeViewModel : BaseViewModel() {

    val chargeListLiveData = MutableLiveData<Pair<List<ChargeModel>, String?>>()
    val chargeInfoLiveData = MutableLiveData<Pair<TransactionModel?, String?>>()
    val chargeUnlockLiveData = MutableLiveData<String>()
    val chargeTransactionLiveData = MutableLiveData<Pair<Boolean, String?>>()
    val chargeSechLiveData = MutableLiveData<String>()


    var chargeList = mutableListOf<ChargeModel>()
    val chargeids = mutableListOf<String>()
    var chargerId: String? = ""
    var connectorId: String? = ""
    var status: Int = 0
    var isListCallback = false


    var valueCurrent: Pair<String, String>? = null
    var valueVoltage: Pair<String, String>? = null
    var valuePower: Pair<String, String>? = null


    fun getChargeList(
        email: String,
    ) {
        val params = hashMapOf<String, String>().apply {
            put("email", email)
        }
        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.CHARGE_LIST,
                params,
                object : HttpCallback<HttpResult<Array<ChargeModel>>>() {
                    /*         override fun success(result: HttpResult<List<ChargeModel>>) {
                                 val mutableListOf = mutableListOf<ChargeModel>()
                                 if (result.isBusinessSuccess()) {
                                     val obj = result.obj
                                     obj?.let {
                                         mutableListOf.addAll(it)
                                     }
                                     chargeListLiveData.value = Pair(mutableListOf, null)
                                 } else {
                                     chargeListLiveData.value = Pair(emptyList(), result.msg ?: "")
                                 }
                             }*/

                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeListLiveData.value = Pair(emptyList(), errorModel.errorMsg ?: "")
                    }

                    override fun success(result: HttpResult<Array<ChargeModel>>) {
                        isListCallback = true
                        val mutableListOf = mutableListOf<ChargeModel>()
                        if (result.isBusinessSuccess()) {
                            val obj = result.obj
                            obj?.let {
                                mutableListOf.addAll(it)
                            }
                            chargeList = mutableListOf
                            chargeListLiveData.value = Pair(mutableListOf, null)
                        } else {
                            chargeListLiveData.value = Pair(emptyList(), result.msg ?: "")
                        }

                    }
                })
        }

    }


    fun getChargeInfo() {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")

        }
        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.TRANSACTION_OVERVIEW,
                params,
                object : HttpCallback<HttpResult<TransactionModel>>() {
                    override fun success(result: HttpResult<TransactionModel>) {
                        if (result.isBusinessSuccess()) {
                            chargeInfoLiveData.value = Pair(result.obj, null)
                        } else {
                            chargeInfoLiveData.value = Pair(null, result.msg ?: "")
                        }

                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeInfoLiveData.value = Pair(null, errorModel.errorMsg ?: "")
                    }

                })
        }
    }


    fun unlockConnector() {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")
        }


        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.UNLOCKCONNECTOR,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        val msg = result.msg
                        if (result.isBusinessSuccess()) {
                            chargeUnlockLiveData.value =
                                msg ?: MainApplication.instance().getString(
                                    R.string.m105_unlock_success
                                )
                        } else {
                            chargeUnlockLiveData.value =
                                msg ?: MainApplication.instance().getString(
                                    R.string.m106_unlock_fail
                                )
                        }
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeUnlockLiveData.value = errorModel.errorMsg ?: ""
                    }

                })

        }

    }


    fun unStartOrStopCharge(url: String) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")
        }

        viewModelScope.launch {
            apiService().postForm(
                url,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        chargeTransactionLiveData.value =
                            Pair(result.isBusinessSuccess(), result.msg)
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeUnlockLiveData.value = errorModel.errorMsg ?: ""
                    }
                })

        }

    }


    fun setScheduledChargingStatus(status: String) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")
            put("status", status)
        }


        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.SETSCHEDULEDCHARGINGSTATUS,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        val msg = result.msg
                        chargeSechLiveData.value = msg ?: ""

                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeSechLiveData.value = errorModel.errorMsg ?: ""
                    }

                })

        }

    }


}