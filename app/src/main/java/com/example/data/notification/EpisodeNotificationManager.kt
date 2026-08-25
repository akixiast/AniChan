package com.example.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.MainActivity
import com.example.R
import com.example.data.model.AiringScheduleItem
import com.example.data.model.UserMediaEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EpisodeNotificationManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anichan_notification_prefs", Context.MODE_PRIVATE)

    companion object {
        const val CHANNEL_ID = "anichan_airing_episodes"
        const val CHANNEL_NAME = "Airing Episode Alerts"
        const val CHANNEL_DESC = "Notifications when new anime episodes air from your watchlist"
        const val UPDATE_CHANNEL_ID = "anichan_app_updates"
        const val UPDATE_CHANNEL_NAME = "App Updates"

        @Volatile
        private var INSTANCE: EpisodeNotificationManager? = null

        fun getInstance(context: Context): EpisodeNotificationManager {
            return INSTANCE ?: synchronized(this) {
                val instance = EpisodeNotificationManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val episodeChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }

            val updateChannel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                UPDATE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for AniChan app version updates"
            }

            notificationManager.createNotificationChannel(episodeChannel)
            notificationManager.createNotificationChannel(updateChannel)
        }
    }

    fun isEpisodeNotificationsEnabled(): Boolean {
        return prefs.getBoolean("episode_notifications_enabled", true)
    }

    fun setEpisodeNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("episode_notifications_enabled", enabled).apply()
    }

    fun isWatchingOnly(): Boolean {
        return prefs.getBoolean("notify_watching_only", false)
    }

    fun setWatchingOnly(watchingOnly: Boolean) {
        prefs.edit().putBoolean("notify_watching_only", watchingOnly).apply()
    }

    fun isNotifyWatchingOnly(): Boolean = isWatchingOnly()

    fun setNotifyWatchingOnly(watchingOnly: Boolean) = setWatchingOnly(watchingOnly)

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Check if a specific schedule item has already been notified
     */
    private fun isAlreadyNotified(scheduleId: Int): Boolean {
        val notifiedSet = prefs.getStringSet("notified_schedule_ids", emptySet()) ?: emptySet()
        return notifiedSet.contains(scheduleId.toString())
    }

    private fun markAsNotified(scheduleId: Int) {
        val notifiedSet = prefs.getStringSet("notified_schedule_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        notifiedSet.add(scheduleId.toString())
        // Keep set size manageable (max 200 items)
        if (notifiedSet.size > 200) {
            val trimmed = notifiedSet.toList().takeLast(100).toSet()
            prefs.edit().putStringSet("notified_schedule_ids", trimmed).apply()
        } else {
            prefs.edit().putStringSet("notified_schedule_ids", notifiedSet).apply()
        }
    }

    /**
     * Scans upcoming airing schedule items against user's watchlist entries and notifies
     */
    suspend fun checkAndNotifyUpcomingEpisodes(
        scheduleList: List<AiringScheduleItem>,
        userEntries: List<UserMediaEntry>
    ): Int = withContext(Dispatchers.IO) {
        if (!isEpisodeNotificationsEnabled() || !hasNotificationPermission()) {
            return@withContext 0
        }

        val watchingOnly = isNotifyWatchingOnly()
        val trackedMap = userEntries
            .filter { entry ->
                if (watchingOnly) entry.status == "WATCHING"
                else entry.status == "WATCHING" || entry.status == "PLANNING" || entry.status == "REWATCHING"
            }
            .associateBy { it.mediaId }

        var count = 0
        val currentTimeSec = System.currentTimeMillis() / 1000

        for (schedule in scheduleList) {
            val userEntry = trackedMap[schedule.media.id] ?: continue
            // If aired in last 24 hours or airing within next 2 hours
            val timeDiff = schedule.airingAt - currentTimeSec
            val shouldNotify = timeDiff in -86400..7200 // Between -24h and +2h

            if (shouldNotify && !isAlreadyNotified(schedule.id)) {
                sendEpisodeNotification(
                    schedule = schedule,
                    userEntry = userEntry
                )
                markAsNotified(schedule.id)
                count++
            }
        }
        return@withContext count
    }

    /**
     * Sends an airing episode notification
     */
    suspend fun sendEpisodeNotification(
        schedule: AiringScheduleItem,
        userEntry: UserMediaEntry?
    ) = withContext(Dispatchers.IO) {
        if (!hasNotificationPermission()) return@withContext

        val media = schedule.media
        val title = media.displayTitle
        val epText = "Episode ${schedule.episode} is now airing!"
        val extraText = if (userEntry != null) "Your watchlist progress: ${userEntry.progress} ep" else "Check it out in AniChan"

        // Load anime cover image bitmap for rich big picture style
        var largeIconBitmap: Bitmap? = null
        try {
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(media.coverImageLarge)
                .allowHardware(false)
                .build()
            val result = loader.execute(req)
            if (result is SuccessResult) {
                largeIconBitmap = (result.drawable as? BitmapDrawable)?.bitmap
            }
        } catch (e: Exception) {
            Log.w("NotificationManager", "Could not load cover bitmap: ${e.message}")
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_media_id", media.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("📺 $title")
            .setContentText(epText)
            .setSubText(extraText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (largeIconBitmap != null) {
            builder.setLargeIcon(largeIconBitmap)
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(largeIconBitmap)
                    .setBigContentTitle("📺 $title - Episode ${schedule.episode}")
                    .setSummaryText(epText)
            )
        } else {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$epText\n$extraText")
            )
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(schedule.id, builder.build())
        } catch (e: SecurityException) {
            Log.e("NotificationManager", "SecurityException posting notification", e)
        }
    }

    /**
     * Send a test notification so user can confirm everything is working
     */
    fun showEpisodeNotification(
        animeTitle: String,
        episode: Int,
        mediaId: Int = 1,
        imageUrl: String? = null,
        airingTimeFormatted: String = "Now"
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_media_id", mediaId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            mediaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("📺 $animeTitle")
            .setContentText("Episode $episode is now airing ($airingTimeFormatted)")
            .setSubText("AniChan Airing Alert")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Episode $episode of $animeTitle is airing now! Tap to view details and update your tracking progress."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(mediaId, builder.build())
        } catch (e: SecurityException) {
            Log.e("NotificationManager", "SecurityException posting notification", e)
        }
    }

    suspend fun sendTestNotification() = withContext(Dispatchers.IO) {
        if (!hasNotificationPermission()) return@withContext

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            99999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("✨ AniChan Episode Alerts Active!")
            .setContentText("You will receive notifications whenever tracked anime episodes air.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "AniChan will notify you when new episodes from your watchlist air on schedule. You can manage frequency and filters in Settings."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(99999, builder.build())
        } catch (e: SecurityException) {
            Log.e("NotificationManager", "SecurityException posting test notification", e)
        }
    }
}
