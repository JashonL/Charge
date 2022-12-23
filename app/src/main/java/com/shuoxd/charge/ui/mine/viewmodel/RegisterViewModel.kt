package com.shuoxd.charge.ui.mine.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.account.User
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.util.MD5Util
import kotlinx.coroutines.launch

class RegisterViewModel : BaseViewModel() {

    val registerLiveData = MutableLiveData<User?>()

    /**
     * 是否同意隐私政策
     */
    var isAgree = false


    /**
     * 注册
     */
    fun register(
        email: String, password: String, country: String,
        city: String, phoneNum: String, timeZone: String
    ) {
        viewModelScope.launch {
            val params = hashMapOf<String, String>().apply {
                put("email", email)
                put("password", MD5Util.md5(password) ?: "")
                put("country", country)
                put("city", city)
                put("phoneNum", phoneNum)
                put("timeZone", timeZone)
            }
            apiService().postForm(ApiPath.Mine.REGISTER, params, object :
                HttpCallback<HttpResult<User>>() {
                override fun success(result: HttpResult<User>) {
                    if (result.isBusinessSuccess()) {

                        val user = result.obj
                        user?.password = password
                        registerLiveData.value = user
                    } else {
                        registerLiveData.value = null
                    }
                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    registerLiveData.value = null
                }

            })
        }
    }


}