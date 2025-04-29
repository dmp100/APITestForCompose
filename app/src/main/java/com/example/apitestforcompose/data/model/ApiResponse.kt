// 위치: com.example.apitestforcompose.data.model.ApiResponse.kt
package com.example.apitestforcompose.data.model

data class ApiResponse(
    val response: Response
)

data class Response(
    val header: Header,
    val body: Body
)

data class Header(
    val resultCode: String,
    val resultMsg: String
)

data class Body(
    val items: Items,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class Items(
    val item: List<Item>
)

data class Item(
    val galContentId: String,
    val galTitle: String,
    val galWebImageUrl: String,
    val galContentTypeId: String? = null,
    val galCreatedtime: String? = null,
    val galModifiedtime: String? = null,
    val galPhotographyMonth: String? = null,
    val galPhotographyLocation: String? = null,
    val galPhotographer: String? = null,
    val galSearchKeyword: String? = null
)