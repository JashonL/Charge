package com.shuoxd.charge.ui.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class ForgotPwdViewModel : BaseViewModel() {

    val getVerifyCodeLiveData = MutableLiveData<Pair<Boolean, String?>>()
    val findPasswordLiveData = MutableLiveData<Pair<Boolean, String?>>()


    /**
     * 获取验证码
     */

    fun fetchVerifyCode(email: String) {
        val params = hashMapOf<String, String>().apply {
            put("email", email)
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Mine.SENDEMAILCODE,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        getVerifyCodeLiveData.value = Pair(result.isBusinessSuccess(), result.msg ?: "")
                    }


                    override fun onFailure(errorModel: HttpErrorModel) {
                        getVerifyCodeLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                    }
                })


        }
    }



    fun findPassword(email:String,emailCode:String){

        val params = hashMapOf<String, String>().apply {
            put("email", email)
            put("emailCode", emailCode)
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Mine.FINDPASSWORD,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        findPasswordLiveData.value = Pair(result.isBusinessSuccess(), result.msg ?: "")
                    }


                    override fun onFailure(errorModel: HttpErrorModel) {
                        findPasswordLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                    }
                })


        }

    }



}