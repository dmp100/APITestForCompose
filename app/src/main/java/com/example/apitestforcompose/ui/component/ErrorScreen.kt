// 위치: com.example.apitestforcompose.ui.component.ErrorScreen.kt
package com.example.apitestforcompose.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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