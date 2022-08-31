package com.shuoxd.lib.service.account

import android.text.TextUtils
import com.shuoxd.lib.LibApplication
import com.shuoxd.lib.service.storage.DefaultStorageService
import com.shuoxd.lib.util.GsonManager

abstract class BaseAccountService : IAccountService {

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER = "key_user"
        private const val KEY_USER_AVATAR = "key_user_avatar"

        /**
         * 体验账号密码
         */
        const val INFO_SPACE_USER_NAME = "guest"
        const val INFO_SPACE_PASSWORD = "123456"
    }

    private var token: String? = null
    private var user: User? = null
    private var userAvatar: String? = null
    private val accountListeners = mutableSetOf<IAccountService.AccountListener>()
    private val userProfileChangeListeners =
        mutableSetOf<IAccountService.OnUserProfileChangeListener>()

    private var logout: Boolean = false

    private val userStorage by lazy {
        DefaultStorageService(LibApplication.instance(), "account")
    }

    init {
        token = userStorage.getString(KEY_TOKEN, "")
        userAvatar = userStorage.getString(KEY_USER_AVATAR, "")
        userStorage.getString(KEY_USER, "")?.let {
            user = GsonManager.fromJson(it, User::class.java)
        }
    }

    override fun token(): String? {
        return token
    }

    override fun saveToken(token: String?) {
        userStorage.put(KEY_TOKEN, token)
        this.token = token
    }

    override fun user(): User? {
        return user
    }

    override fun saveUserInfo(user: User?) {
        userStorage.put(KEY_USER, GsonManager.toJson(user))
        this.user = user
        logout=false
        dispatchUserProfileChanged()
    }

    override fun userAvatar(): String? {
        return userAvatar
    }

    override fun saveUserAvatar(userAvatar: String?) {
        userStorage.put(KEY_USER_AVATAR, userAvatar)
        this.userAvatar = userAvatar
        dispatchUserProfileChanged()
    }

    override fun logout() {
//        userStorage.clear()
//        token = null
//        user = null
        logout=true
        dispatchAccountChanged()
    }


    override fun isLogout(): Boolean {
        return logout
    }



    override fun id(): String? {
        return user?.id
    }

    override fun isLogin(): Boolean {
        return !TextUtils.isEmpty(token)
    }







    override fun isGuest(): Boolean {
        return user()?.email == INFO_SPACE_USER_NAME
    }

    override fun addAccountListener(accountListener: IAccountService.AccountListener) {
        accountListeners.add(accountListener)
    }

    override fun removeAccountListener(accountListener: IAccountService.AccountListener) {
        accountListeners.remove(accountListener)
    }

    override fun dispatchAccountChanged() {
        for (listener in accountListeners) {
            listener.onAccountChange(this)
        }
    }

    override fun addUserProfileChangeListener(userProfileChangeListener: IAccountService.OnUserProfileChangeListener) {
        userProfileChangeListeners.add(userProfileChangeListener)
    }

    override fun removeUserProfileChangeListener(userProfileChangeListener: IAccountService.OnUserProfileChangeListener) {
        userProfileChangeListeners.remove(userProfileChangeListener)
    }

    override fun dispatchUserProfileChanged() {
        for (listener in userProfileChangeListeners) {
            listener.onUserProfileChange(this)
        }
    }

}