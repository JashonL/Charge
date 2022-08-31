package com.shuoxd.lib.service.account

import android.content.Context
import com.shuoxd.lib.service.Service

interface IAccountService : Service {

    /**
     * 获取token
     */
    fun token(): String?

    fun saveToken(token: String?)

    fun user(): User?

    fun saveUserInfo(user: User?)

    fun saveUserAvatar(userAvatar: String?)

    fun userAvatar(): String?

    fun login(context: Context)

    fun logout()

    fun tokenExpired()

    fun id(): String?

    fun isLogin(): Boolean

    fun isLogout():Boolean



    /**
     * 是否是体验账号
     */
    fun isGuest(): Boolean

    fun addAccountListener(accountListener: AccountListener)

    fun removeAccountListener(accountListener: AccountListener)

    fun dispatchAccountChanged()

    fun addUserProfileChangeListener(userProfileChangeListener: OnUserProfileChangeListener)

    fun removeUserProfileChangeListener(userProfileChangeListener: OnUserProfileChangeListener)

    fun dispatchUserProfileChanged()

    /**
     * 登录状态修改后触发
     */
    interface AccountListener {
        fun onAccountChange(account: IAccountService)
    }

    /**
     * 用户信息修改后触发
     * 1.修改手机号
     * 2.修改头像
     * 3.修改安装商编号
     * 4.修改邮箱
     */
    interface OnUserProfileChangeListener {
        fun onUserProfileChange(account: IAccountService)
    }

}