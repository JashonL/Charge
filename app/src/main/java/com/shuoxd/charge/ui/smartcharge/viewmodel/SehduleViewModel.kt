package com.shuoxd.charge.ui.smartcharge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.model.charge.ScheduledModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch

class SehduleViewModel :BaseViewModel() {


    val scheduleLiveData = MutableLiveData<Pair<Boolean, ScheduledModel?>>()




    /**
     * 删除充电桩
     */
    fun getScheduledChargingByUserId(userId: String?,chargerId:String?,connectorId:String?) {

        val params = hashMapOf<String, String>().apply {
            put("userId", userId ?: "")
            put("chargerId", chargerId ?: "")
            put("connectorId", connectorId ?: "")
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.GETSCHEDULEDCHARGINGBYUSERID,
                params,
                object : HttpCallback< HttpResult<ScheduledModel>>() {
                    override fun success(result: HttpResult<ScheduledModel>) {
                        scheduleLiveData.value = Pair(result.isBusinessSuccess(),result.obj)
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        scheduleLiveData.value = Pair(false,null)
                    }

                })
        }


    }




}