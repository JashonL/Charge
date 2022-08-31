package com.shuoxd.charge.ui.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.AuthModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.service.http.PageModel

class ModyfyViewModel:BaseViewModel() {

    var modyfiLiveData = MutableLiveData<Pair<Boolean,String?>>()



    fun modifyPassword(oldPassword: String?,newPassword:String?) {
        val params = hashMapOf<String, String>().apply {
            put("oldPassword", oldPassword ?: "")
            put("newPassword", newPassword ?: "")
        }


        apiService().postForm(ApiPath.Mine.MODIFY_PASSWORD, params,
            object : HttpCallback<HttpResult<PageModel<AuthModel>>>() {

                override fun success(result: HttpResult<PageModel<AuthModel>>) {
                    val msg = result.msg
                    if (result.isBusinessSuccess()) {
                        modyfiLiveData.value =Pair(true,
                            msg ?: MainApplication.instance().getString(
                                R.string.m137_add_success
                            ))
                    } else {
                        modyfiLiveData.value =Pair(false,
                            msg ?: MainApplication.instance().getString(
                                R.string.m138_add_fail
                            ))
                    }


                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    modyfiLiveData.value = Pair(false,errorModel.errorMsg ?: "")
                }



            })


    }

}