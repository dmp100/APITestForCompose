// 위치: com.example.apitestforcompose.data.model.ApiRequest.kt
package com.example.apitestforcompose.data.model

data class ApiRequest(
    val numOfRows: Int?,
    val pageNo: Int?,
    val MobileOS: String,
    val MobileApp: String,
    val arrange: String?,
    val _type: String?,
    val serviceKey: String
)