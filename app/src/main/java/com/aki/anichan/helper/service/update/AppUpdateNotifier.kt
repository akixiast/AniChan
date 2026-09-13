package com.aki.anichan.helper.service.update

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aki.anichan.R
import com.aki.anichan.helper.utils.DeepLink
import com.aki.anichan.helper.utils.PushNotificationUtil

object AppUpdateNotifier {

    private const val UPDATE_NOTIFICATION_ID = 1018

    fun notifyIfNeeded(context: Context, checker: AppUpdateChecker, info: AppUpdateInfo) {
        if (checker.getLastNotifiedTag() == info.latestTag) return
        checker.setLastNotifiedTag(info.latestTag)
        showNotification(context, info)
    }

    fun showNotification(context: Context, info: AppUpdateInfo) {
        val notificationIntent = Intent(context, com.aki.anichan.ui.deeplink.DeepLinkActivity::class.java)
        notificationIntent.data = DeepLink.generateAppSettings().uri
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, flags)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        val message = context.getString(R.string.update_available_message, info.releaseName)
        val builder = NotificationCompat.Builder(context, "Notifications")
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(context.getString(R.string.new_update_is_available))
            .setContentText(message)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.yellow))
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        PushNotificationUtil.createNotificationChannel(context)
        notificationManager.notify(UPDATE_NOTIFICATION_ID, builder.build())
    }
}
