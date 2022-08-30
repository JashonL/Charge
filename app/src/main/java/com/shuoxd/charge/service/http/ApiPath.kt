package com.shuoxd.charge.service.http

object ApiPath {

    object Mine {
        /**
         * 登录
         */

        const val LOGIN = "v1/user/login"

        /**
         * 登出
         */
        const val LOGOUT = "v1/user/logOut"

        /**
         * 注册
         */
        const val REGISTER = "v1/user/register"

        /**
         * 通过邮箱或手机号发送验证码
         */
        const val GET_VERIFY_CODE = "ATSregister/sendValidCode"

        /**
         * 获取国家列表
         */
        const val GET_COUNTRY_LIST = "ATSregister/getCountryList"

        /**
         * 校验验证码
         */
        const val VERIFY_CODE = "ATSregister/validCode"

        /**
         * 获取用户头像
         */
        const val GET_USER_AVATAR = "ATSSetting/getUserIcon"

        /**
         * 找回密码-修改密码
         */
        const val MODIFY_PASSWORD_BY_PHONE_OR_EMAIL =
            "ATSregister/changePasswordByPhoneOrEmail"

        /**
         * 设置-修改密码
         */
        const val MODIFY_PASSWORD = "ATSSetting/changePWD"

        /**
         * 更换邮箱
         */
        const val CHANGE_EMAIL = "ATSSetting/updateEmail"

        /**
         * 更换手机号
         */
        const val CHANGE_PHONE = "ATSSetting/updatePhoneNum"

        /**
         * 修改安装商编号
         */
        const val MODIFY_INSTALLER_NO = "ATSSetting/updateAgentCode"

        /**
         * 注销账号
         */
        const val CANCEL_ACCOUNT = "ATSSetting/eraseUser"

        /**
         * 上传用户头像
         */
        const val UPLOAD_USER_ICON = "ATSSetting/uploadUserIcon"

        /**
         * 获取消息列表
         */
        const val GET_MESSAGE_LIST = "ATSSetting/getUserMsgList"

        /**
         * 获取未读消息数量
         */
        const val GET_MESSAGE_UNREAD_NUM = "ATSSetting/getUserUnreadMsgNum"

        /**
         * 删除消息
         */
        const val DELETE_MESSAGE = "ATSSetting/delMsgById"

    }

    object Charge {
        /**
         * 电桩列表
         */
        const val CHARGE_LIST = "v1/charger/chargerList"

        /**
         * 获取交易枪的详情
         */
        const val TRANSACTION_OVERVIEW = "v1/transaction/getTransactionOverview"

        /**
         * 解锁
         */
        const val UNLOCKCONNECTOR = "/v1/charger/unlockConnector"

        /**
         * 开始充电
         */
        const val REMOTESTARTTRANSACTION = "v1/transaction/remoteStartTransaction"

        /**
         * 停止充电
         */
        const val REMOTESTOPTRANSACTION = "v1/transaction/remoteStopTransaction"

        /**
         * 绑定充电桩
         */
        const val ADDCHARGER = "v1/charger/addCharger"
        /**
         * 获取充电记录
         */
        const val TRANSACTIONLIST="v1/transaction/transactionList/"
        /**
         * 设置费率
         */
        const val SETRATE="v1/charger/modifyChargerInfo"
        /**
         * 获取授权列表
         */
        const val GETAUTHLIST="v1/charger/authChargerList/1"

        /**
         * 添加授权用户
         */
        const val AUTHCHARGER="v1/charger/authCharger"

    }

    object Service {
        /**
         * 服务-使用手册列表
         */
        const val GET_SERVICE_MANUAL = "ATService/getManual"

        /**
         * 服务-安装视频列表
         */
        const val GET_INSTALL_VIDEO = "ATService/getInstallVideo"
    }

}