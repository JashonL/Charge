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
         * 设置-修改密码
         */
        const val MODIFY_PASSWORD = "/v1/user/modifyPassword"

        /**
         * 修改用户信息
         */
        const val MODIFYUSERINFO = "/v1/user/modifyUserInfo"

        /**
         * 找回密码
         */
        const val FINDPASSWORD ="/v1/user/findPassword"

        /**
         * 获取邮箱验证码
         */
        const val SENDEMAILCODE ="/v1/user/sendEmailCode"

        /**
         * 上传用户头像
         */
        const val UPLOADAVATAR ="/v1/user/uploadAvatar"

    }

    object Charge {

        /**
         * 未授权的电桩列表
         */
        const val CHARGER_LIST_ORIGINAL = "v1/charger/chargerListOriginal"


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
        const val GETAUTHLIST="v1/charger/authChargerList/"

        /**
         * 添加授权用户
         */
        const val AUTHCHARGER="v1/charger/authCharger"


        /**
         * 删除充电桩
         */
        const val DELETECHARGER="v1/charger/removeCharger"

        /**
         * 获取计划充电信息
         */
        const val GETSCHEDULEDCHARGINGBYUSERID="v1/global/getScheduledChargingByUserId"



        /**
         * 获取计划充电信息
         */
        const val SETSCHEDULEDCHARGING="v1/global/setScheduledCharging"



        /**
         * 获取非高峰充电
         */
        const val GETOFFPEAKCHARGINGBYUSERID="v1/global/getOffPeakChargingByUserId"

        /**
         * 设置非高峰充电
         */
        const val SETOFFPEAKCHARGING="v1/global/setOffPeakCharging"


        /**
         * 获取充电桩授权状态
         */
        const val CONNECTORAUTHORIZESTATUS="v1/charger/connectorAuthorizeStatus"



        /**
         * 修改充电桩授权状态
         */
        const val AUTHORIZECONNECTOR="v1/charger/authorizeConnector"




        /**
         * 设置计划充电状态
         */

        const val SETSCHEDULEDCHARGINGSTATUS="v1/global/setScheduledChargingStatus"




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