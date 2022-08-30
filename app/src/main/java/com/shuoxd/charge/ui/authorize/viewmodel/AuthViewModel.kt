package com.shuoxd.charge.ui.authorize.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.AuthModel
import com.shuoxd.charge.model.charge.RecordModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.service.http.PageModel

class AuthViewModel:BaseViewModel() {



    var authListLiveData = MutableLiveData<Pair<PageModel<AuthModel>?, String?>>()



    fun getAuthlist(chargerId: String?,currentPage:Int) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
        }

        var url = ApiPath.Charge.GETAUTHLIST + currentPage

        apiService().postForm(url, params,
            object : HttpCallback<HttpResult<PageModel<AuthModel>>>() {

                override fun success(result: HttpResult<PageModel<AuthModel>>) {
                    if (result.isBusinessSuccess()) {
                        authListLiveData.value = Pair(result.obj, null)
                    } else {
                        authListLiveData.value = Pair(null, result.msg ?: "")
                    }

                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    authListLiveData.value = Pair(null, errorModel.errorMsg ?: "")
                }



            })


    }


}