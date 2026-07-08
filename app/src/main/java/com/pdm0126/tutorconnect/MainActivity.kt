package com.pdm0126.tutorconnectproyect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pdm0126.tutorconnectproyect.core.navigation.AppNavigation
import com.pdm0126.tutorconnectproyect.core.theme.TutorConnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TutorConnectTheme {
                AppNavigation()
            }
        }
    }
}
