// ui/screen/gallery/GalleryScreen.kt
package com.example.apitestforcompose.ui.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apitestforcompose.data.api.ApiClient
import com.example.apitestforcompose.data.model.Item
import com.example.apitestforcompose.data.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun GalleryScreen() {
    // 상태 정의
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // API 호출을 처리하는 함수
    fun loadGalleryItems() {
        isLoading = true
        errorMessage = null

        // Retrofit 서비스 호출
        ApiClient.service.getUser(
            pageNo = 1,
            numOfRows = 10,
            MobileOS = "AND",
            MobileApp = "app",
            arrange = "A",
            _type = "json",
            serviceKey = "KVLNantZhhbecolXsyBcwYgdnmPDo0poEXjIFRJLMG4adbgyavxDN9aKnwgKfeRsvG46veAKSktHS1e8mI/yKQ=="
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                isLoading = false

                if (response.isSuccessful) {
                    val result = response.body()
                    result?.response?.body?.items?.item?.let { galleryItems ->
                        items = galleryItems
                    } ?: run {
                        errorMessage = "데이터가 없습니다"
                    }
                } else {
                    errorMessage = "서버 오류: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "네트워크 오류: ${t.message}"
            }
        })
    }

    // 컴포넌트가 처음 표시될 때 데이터 로드
    LaunchedEffect(key1 = true) {
        loadGalleryItems()
    }

    // UI 렌더링
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "오류가 발생했습니다",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = errorMessage ?: "",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    androidx.compose.material3.Button(
                        onClick = { loadGalleryItems() }
                    ) {
                        Text("다시 시도")
                    }
                }
            }
            items.isEmpty() -> {
                Text(
                    text = "표시할 아이템이 없습니다",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(items) { item ->
                        GalleryItem(item = item)
                    }
                }
            }
        }
    }
}

