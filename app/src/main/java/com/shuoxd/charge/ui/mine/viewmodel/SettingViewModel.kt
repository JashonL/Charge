package com.shuoxd.charge.ui.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shuoxd.charge.base.BaseViewModel
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.lib.service.http.HttpCallback
import com.shuoxd.lib.service.http.HttpErrorModel
import com.shuoxd.lib.service.http.HttpResult
import kotlinx.coroutines.launch
import java.io.File

/**
 * 选择国家/地区
 */
class SettingViewModel : BaseViewModel() {


    val logoutLiveData = MutableLiveData<String?>()

    val deleteLiveData = MutableLiveData<Pair<Boolean, String?>>()

    var position: Int = 0

    val uploadUserAvatarLiveData = MutableLiveData<Pair<String?, String?>>()


    /**
     * 登出
     */
    fun logout(email: String?) {

        val params = hashMapOf<String, String>().apply {
            put("email", email ?: "")
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Mine.LOGOUT,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        if (result.isBusinessSuccess()) {
                            logoutLiveData.value = null
                        } else {
                            logoutLiveData.value = result.msg ?: ""
                        }
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        logoutLiveData.value = errorModel.errorMsg ?: ""
                    }

                })
        }
    }

    /**
     * 删除充电桩
     */
    fun delete(chargerId: String?) {

        val params = hashMapOf<String, String>().apply {
            put("chargerId", chargerId ?: "")
        }

        viewModelScope.launch {
            apiService().postForm(
                ApiPath.Charge.DELETECHARGER,
                params,
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        deleteLiveData.value = Pair(result.isBusinessSuccess(), result.msg ?: "")
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        deleteLiveData.value = Pair(false, errorModel.errorMsg ?: "")
                    }

                })
        }


    }



    /**
     * 设置-上传用户头像
     */
    fun uploadUserAvatar(filePath: String) {
        viewModelScope.launch {
            apiService().postFile(
                ApiPath.Mine.UPLOADAVATAR, hashMapOf(), File(filePath),
                object : HttpCallback<HttpResult<String>>() {
                    override fun success(result: HttpResult<String>) {
                        if (result.isBusinessSuccess()) {
                            uploadUserAvatarLiveData.value = Pair(result.obj, result.msg ?: "")
                        } else {
                            uploadUserAvatarLiveData.value = Pair(null, result.msg ?: "")
                        }
                    }

                    override fun onFailure(errorModel: HttpErrorModel) {
                        uploadUserAvatarLiveData.value = Pair(null, errorModel.errorMsg ?: "")
                    }

                })
        }
    }


}