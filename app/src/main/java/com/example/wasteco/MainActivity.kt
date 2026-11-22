package com.example.wasteco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.wasteco.ui.navigation.WasteCoNavHost
import com.example.wasteco.ui.theme.WasteCoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WasteCoTheme {
                WasteCoApp()
            }
        }
    }
}

@Composable
fun WasteCoApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        WasteCoNavHost()
    }
}
