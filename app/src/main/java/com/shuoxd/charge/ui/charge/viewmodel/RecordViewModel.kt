package com.shuoxd.charge.ui.charge.viewmodel

import androidx.lifecycle.MutableLiveData
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.RecordModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import com.shuoxd.lib.service.http.PageModel

class RecordViewModel : BaseViewModel() {

    var recordLiveData = MutableLiveData<Pair<PageModel<RecordModel>?, String?>>()



    fun getTransactionlist(chargerId: String?,currentPage:Int) {
        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
        }

        var url = ApiPath.Charge.TRANSACTIONLIST + currentPage

        apiService().postForm(url, params,
            object : HttpCallback<HttpResult<PageModel<RecordModel>>>() {

                override fun success(result: HttpResult<PageModel<RecordModel>>) {
                    if (result.isBusinessSuccess()) {
                        recordLiveData.value = Pair(result.obj, null)
                    } else {
                        recordLiveData.value = Pair(null, result.msg ?: "")
                    }

                }

                override fun onFailure(errorModel: HttpErrorModel) {
                    recordLiveData.value = Pair(null, errorModel.errorMsg ?: "")
                }



            })


    }


}