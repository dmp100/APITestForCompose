# 📝 Jetpack Compose API 연동 가이드

## 구현 순서 및 방법

### 1. 데이터 모델 구현

#### 1.1 API 요청 모델 (ApiRequest.kt)
```kotlin
data class ApiRequest(
    val numOfRows: Int?,     // 한 페이지 결과 수
    val pageNo: Int?,        // 페이지 번호
    val MobileOS: String,    // OS 구분
    val MobileApp: String,   // 앱 이름
    val arrange: String?,    // 정렬 기준
    val _type: String?,      // 응답 형식
    val serviceKey: String   // 인증키
)
```

#### 1.2 API 응답 모델 (ApiResponse.kt)
```kotlin
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
```

### 2. API 클라이언트 설정

#### 2.1 Retrofit 서비스 인터페이스 (RetrofitService.kt)
```kotlin
interface RetrofitService {
    @GET("galleryList1")
    fun getUser(
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("MobileOS") MobileOS: String,
        @Query("MobileApp") MobileApp: String,
        @Query("arrange") arrange: String,
        @Query("_type") _type: String,
        @Query("serviceKey") serviceKey: String
    ): Call<ApiResponse>
}
```

#### 2.2 API 클라이언트 구현 (ApiClient.kt)
```kotlin
object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apis.data.go.kr/B551011/PhotoGalleryService1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    val service: RetrofitService = retrofit.create(RetrofitService::class.java)
}
```

### 3. UI 컴포넌트 구현

#### 3.1 공통 컴포넌트 - 로딩 인디케이터 (LoadingIndicator.kt)
```kotlin
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}
```

#### 3.2 공통 컴포넌트 - 오류 화면 (ErrorScreen.kt)
```kotlin
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "오류가 발생했습니다", style = MaterialTheme.typography.titleLarge)
        Text(text = message, modifier = Modifier.padding(vertical = 8.dp))
        Button(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}
```

#### 3.3 갤러리 아이템 (GalleryItem.kt)
```kotlin
@Composable
fun GalleryItem(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.galWebImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.galTitle,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = item.galTitle,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
```

#### 3.4 갤러리 화면 (GalleryScreen.kt)
```kotlin
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
            serviceKey = "API 비밀키"
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
                    Button(
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
```

### 4. 앱 진입점 설정 (MainActivity.kt)
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APITestForComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GalleryScreen() // 갤러리 화면 Composable 호출
                }
            }
        }
    }
}
```

## 🛠 필요한 라이브러리 설정

### build.gradle.kts
```kotlin
dependencies {
    // 기본 Compose 라이브러리
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.material3:material3:1.1.1")
    
    // Retrofit 및 OkHttp 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.9.0") 
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    
    // Coil (이미지 로딩)
    implementation("io.coil-kt:coil-compose:2.4.0")
}
```

### AndroidManifest.xml
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 인터넷 권한 설정 -->  
    <uses-permission android:name="android.permission.INTERNET" />  
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />  
  
    <application
        ...
        android:usesCleartextTraffic="true">
        ...
    </application>
</manifest>
```

## 📋 API 응답 형식
```json
{
  "response": {
    "header": {
      "resultCode": "0000",
      "resultMsg": "OK"
    },
    "body": {
      "items": {
        "item": [
          {
            "galContentId": "3463303",
            "galTitle": "해남 오시아노 관광단지",
            "galWebImageUrl": "http://tong.visitkorea.or.kr/..."
          }
        ]
      },
      "numOfRows": 10,
      "pageNo": 1,
      "totalCount": 5794
    }
  }
}
```

## ⚠️ 주의사항
- HTTP 통신을 위한 네트워크 보안 설정 필수 (`android:usesCleartextTraffic="true"`)
- Coil 이미지 로딩 시 크로스페이드 및 컨텐츠 스케일 설정
- API 키는 보안상 별도 관리 필요 (프로덕션 환경에서는 BuildConfig 등을 활용)
- 응답 데이터의 null 처리 구현 (모델 클래스에서 nullable 필드 활용)
- LaunchedEffect 블록을 통한 컴포지션 생명주기 관리

## 🔄 에러 코드
- 10: 잘못된 요청 파라미터
- 11: 필수 요청 파라미터 누락
- 21: 서비스 키 일시적 사용 불가
- 33: 서명되지 않은 호출
- 200: 성공

## 🚀 향후 개선사항
- MVVM 패턴 적용 (ViewModel 도입)
- Repository 패턴 적용
- 페이징 처리 (Jetpack Paging 3 활용)
- 이미지 캐싱 최적화 (Coil의 디스크 캐싱 설정)
- 에러 처리 강화
- UI/UX 개선
- 다크 모드 지원

## 📈 XML vs Compose 비교

### XML 레이아웃
```xml
<androidx.constraintlayout.widget.ConstraintLayout>
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
        android:orientation="vertical"/>
</androidx.constraintlayout.widget.ConstraintLayout>

<androidx.constraintlayout.widget.ConstraintLayout>
    <ImageView 
        android:id="@+id/imageView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:scaleType="centerCrop"/>
    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</androidx.constraintlayout.widget.ConstraintLayout>
```

### Compose UI
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        items(galleryItems) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                AsyncImage(
                    model = item.galWebImageUrl,
                    contentDescription = item.galTitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = item.galTitle,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
```
