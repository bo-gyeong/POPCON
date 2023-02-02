package com.ssafy.popcon.dto

data class User(
    val email: String?,
    val social: String,
    val nday: Int = 1,
    val alarm: Int = 1,
    val secret: String?,
    val manner_temp: Int = 1,
    val term: Int = 1,
    val timezone: Int = 1,
    var token: String?
) {
    constructor(email: String?, social: String) : this(email, social, 1, 1, "the world",1, 1, 1, "")
    constructor(email: String?, social: String, nday: Int, alarm: Int, term: Int, timezone: Int): this(
        email, social, nday,  alarm,"the world", 1, term, timezone, ""
    )
}