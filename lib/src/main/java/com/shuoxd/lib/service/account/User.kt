package com.shuoxd.lib.service.account

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
    val email: String,
    var userType: String,
    var phoneNum: String,
    var country: String,
    var city: String,
    var timeZone: String,
    var password:String,
    var address:String
)
