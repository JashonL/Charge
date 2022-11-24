package com.shuoxd.charge.ui.charge.viewmodel

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

class AddChargeViewModel : BaseViewModel() {

   val  addchargeLiveData=MutableLiveData<Pair<Boolean,String>>()


    fun addChage(chargeId:String?,pin:String){

        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargeId?:"")
            put("pin", pin)
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.ADDCHARGER,
                params,
                object : HttpCallback<HttpResult<TransactionModel>>() {
                    override fun success(result: HttpResult<TransactionModel>) {
                        val msg = result.msg
                        if (result.isBusinessSuccess()) {
                            addchargeLiveData.value =Pair(  true,msg ?: MainApplication.instance().getString(
                                R.string.m105_unlock_success
                            ))

                        } else {
                      /*      addchargeLiveData.value =
                                msg ?: MainApplication.instance().getString(
                                    R.string.m106_unlock_fail
                                )*/


                            addchargeLiveData.value =Pair(false,msg ?: MainApplication.instance().getString(
                                R.string.m106_unlock_fail))
                        }

                    }

                    override fun onFailure(errorModel: HttpErrorModel) {

                        addchargeLiveData.value =Pair(false,errorModel.errorMsg ?: "")
                    }

                })
        }


    }



}