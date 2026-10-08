package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.TechAstraApp
import com.example.ui.theme.TechAstraTheme
import com.example.ui.theme.TechBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechAstraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TechBackground
                ) {
                    TechAstraApp()
                }
            }
        }
    }
}
