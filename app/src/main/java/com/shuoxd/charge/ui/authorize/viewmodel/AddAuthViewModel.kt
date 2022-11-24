package com.shuoxd.charge.ui.authorize.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.AuthModel
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.service.http.PageModel
import kotlinx.coroutines.launch

class AddAuthViewModel : BaseViewModel() {

    val chargeListLiveData = MutableLiveData<Pair<List<ChargeModel>, String?>>()

    var addAuthLiveData = MutableLiveData<Pair<Boolean, String?>>()


    var chargeList: MutableList<ChargeModel>? = null


    fun addAuthlist(chargerId: String?, email: String?) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
            put("email", email ?: "")
        }

        apiService().postForm(ApiPath.Charge.AUTHCHARGER, params,
            object : HttpCallback<HttpResult<String>>() {

                override fun success(result: HttpResult<String>) {
                    val msg = result.msg
                    if (result.isBusinessSuccess()) {
                        addAuthLiveData.value = Pair(
                            true,
                            msg ?: MainApplication.instance().getString(
                                R.string.m137_add_success
                            )
                        )
                    } else {
                        addAuthLiveData.value = Pair(
                            false,
                            msg ?: MainApplication.instance().getString(
                                R.string.m138_add_fail
                            )
                        )
                    }

                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    addAuthLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                }


            })


    }


    fun getChargeList(
        email: String?,
    ) {
        val params = hashMapOf<String, String>().apply {
            put("email", email ?: "")
        }
        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.CHARGER_LIST_ORIGINAL,
                params,
                object : HttpCallback<HttpResult<Array<ChargeModel>>>() {
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


}