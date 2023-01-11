package com.ssafy.popcon.dto

data class User(
    val email: String?,
    val type: Int
){
    constructor() : this("", 0)
}