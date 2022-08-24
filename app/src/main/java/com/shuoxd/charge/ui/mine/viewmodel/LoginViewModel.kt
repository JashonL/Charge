package com.shuoxd.charge.ui.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.account.User
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    val loginLiveData = MutableLiveData<Pair<User?, String?>>()


    fun login(
        email: String,
        password: String,
        phoneOs: Int,
        phoneModel: String,
        appVersion: String
    ) {

        val params = hashMapOf<String, String>().apply {
            put("email", email)
            put("password", password)
            put("phoneOs", phoneOs.toString())
            put("phoneModel", phoneModel)
            put("appVersion", appVersion)

        }
        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Mine.LOGIN,
                params,
                object : HttpCallback<HttpResult<User>>() {
                    override fun success(result: HttpResult<User>) {
                        val user = result.data
                        if (result.isBusinessSuccess()&&user!=null){
                            loginLiveData.value = Pair(user, null)
                        }else{
                            loginLiveData.value = Pair(null, result.msg ?: "")
                        }

                    }


                    override fun onFailure(errorModel: HttpErrorModel) {
                        loginLiveData.value = Pair(null, errorModel.errorMsg ?: "")

                    }

                })





        }


    }


}