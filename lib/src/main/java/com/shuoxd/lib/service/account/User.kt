package com.growatt.lib.service.account

/**
 * 用户ID
 * 用户名
 * 手机号码
 * 安装商编号
 * 邮件
 * token
 */
data class User(
    val id: String,
    val userName: String,
    var phoneNum: String?,
    var agentCode: String?,
    var email: String?,
    val token: String
)
