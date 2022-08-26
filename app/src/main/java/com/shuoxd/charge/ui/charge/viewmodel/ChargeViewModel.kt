package com.shuoxd.charge.ui.charge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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


    val chargeids = mutableListOf<String>()
    var chargerId: String? = ""
    var connectorId: String? = ""

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
                        if (result.isBusinessSuccess()){
                            chargeInfoLiveData.value=Pair(result.obj,"")
                        }else{
                            chargeInfoLiveData.value=Pair(null,result.msg?:"")
                        }

                    }
                    override fun onFailure(errorModel: HttpErrorModel) {
                        chargeInfoLiveData.value = Pair(null, errorModel.errorMsg?:"")
                    }

                })
        }
    }

}