// data/repository/GalleryRepository.kt
package com.example.apitestforcompose.data.repository

import com.example.apitestforcompose.data.api.ApiClient
import com.example.apitestforcompose.data.model.ApiResponse
import com.example.apitestforcompose.data.model.Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryRepository {
    // API 호출 함수
    fun getGalleryItems(
        pageNo: Int = 1,
        numOfRows: Int = 10,
        onSuccess: (List<Item>) -> Unit,
        onError: (String) -> Unit
    ) {
        // API 서비스 호출
        ApiClient.service.getUser(
            pageNo = pageNo,
            numOfRows = numOfRows,
            MobileOS = "AND",  // Android
            MobileApp = "app",  // 앱 이름
            arrange = "A",      // 정렬 기준
            _type = "json",     // 응답 형식
            serviceKey = "KVLNantZhhbecolXsyBcwYgdnmPDo0poEXjIFRJLMG4adbgyavxDN9aKnwgKfeRsvG46veAKSktHS1e8mI/yKQ=="  // API 키
        ).enqueue(object : Callback<ApiResponse> {
            // 응답 처리
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    // 성공적인 응답 처리
                    response.body()?.response?.body?.items?.item?.let { items ->
                        onSuccess(items)  // 성공 콜백 호출
                    } ?: run {
                        onError("데이터가 없습니다")  // 데이터 없음
                    }
                } else {
                    // 에러 응답 처리
                    onError("서버 오류: ${response.code()}")
                }
            }

            // 네트워크 실패 처리
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}