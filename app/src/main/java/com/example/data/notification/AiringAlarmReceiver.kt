package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.remote.AniListApiService
import com.example.data.repository.AniListRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class AiringAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AiringAlarmReceiver", "Alarm triggered - checking for airing episodes in background")
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val userEntries = db.userMediaDao().getAllEntriesDirect()

                if (userEntries.isNotEmpty()) {
                    val client = OkHttpClient()
                    val apiService = AniListApiService(client)
                    val repository = AniListRepository(apiService, db.userMediaDao())
                    
                    val nowSec = System.currentTimeMillis() / 1000
                    val scheduleResult = repository.getAiringSchedule(
                        startTimeSeconds = nowSec - 86400,
                        endTimeSeconds = nowSec + 14400
                    )

                    scheduleResult.onSuccess { scheduleList ->
                        val manager = EpisodeNotificationManager.getInstance(context)
                        val notifiedCount = manager.checkAndNotifyUpcomingEpisodes(scheduleList, userEntries)
                        Log.d("AiringAlarmReceiver", "Background check finished. Notified $notifiedCount episodes.")
                    }
                }
            } catch (e: Exception) {
                Log.e("AiringAlarmReceiver", "Error checking background airing episodes", e)
            } finally {
                EpisodeNotificationManager.getInstance(context).scheduleNextAiringCheck()
                pendingResult.finish()
            }
        }
    }
}
