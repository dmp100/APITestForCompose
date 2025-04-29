package com.example.apitestforcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.apitestforcompose.ui.theme.APITestForComposeTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.apitestforcompose.ui.gallery.GalleryScreen


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
