package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.remote.AniListApiService
import com.example.data.repository.AniListRepository
import com.example.ui.navigation.AniChanApp
import com.example.ui.theme.AniChanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val apiService = AniListApiService()
        val repository = AniListRepository(
            apiService = apiService,
            userMediaDao = database.userMediaDao()
        )

        setContent {
            AniChanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AniChanApp(repository = repository)
                }
            }
        }
    }
}
