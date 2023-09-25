package com.csp.mynew.net

/**
 * created by dongdaqing 2022/3/22 3:28 下午
 */
data class Base<T>(
    val `data`: T?,
    val errorCode: Int,
    val errorMsg: String
)