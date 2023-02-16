package com.ssafy.popcon.dto

data class User(
    val email: String?,
    val social: String,
    val nday: Int = 1,
    val alarm: Int = 1,
    val secret: String?,
    val manner_temp: Int = 0,
    val term: Int = 1,
    val timezone: Int = 1,
    var token: String
) {
    constructor(email: String?, social: String, token: String) : this(email, social, 1, 1, "the world",0, 1, 1, token)
    constructor(email: String?, social: String, nday: Int, alarm: Int, manner_temp: Int, term: Int, timezone: Int, token: String): this(
        email, social, nday,  alarm,"the world", manner_temp, term, timezone, token
    )
}