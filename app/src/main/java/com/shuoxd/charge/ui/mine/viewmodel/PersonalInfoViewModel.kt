package com.shuoxd.charge.ui.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.account.User
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult

class PersonalInfoViewModel : BaseViewModel() {

    var personalLiveData = MutableLiveData<Triple<User?, String?,Boolean>>()

    fun modifyUserInfo(phoneNum: String?, country: String?, city: String) {
        val params = hashMapOf<String, String>().apply {
            put("phoneNum", phoneNum ?: "")
            put("country", country ?: "")
            put("city", city)
        }


        apiService().postForm(
            ApiPath.Mine.MODIFYUSERINFO, params,
            object : HttpCallback<HttpResult<User>>() {

                override fun success(result: HttpResult<User>) {
                    val msg = result.msg
                    val user = result.obj
                    if (result.isBusinessSuccess()) {
                        personalLiveData.value = Triple(user, msg,true)
                    } else {
                        personalLiveData.value = Triple(null, msg ?: "",false)
                    }


                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    personalLiveData.value = Triple(null, errorModel.errorMsg ?: "",false)
                }


            })


    }


}