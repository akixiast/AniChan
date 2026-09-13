package com.aki.anichan.helper.service.update

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object AppUpdateDownloader {

    fun startDownload(context: Context, downloadUrl: String, fileName: String): Long {
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle(context.getString(com.aki.anichan.R.string.app_name))
            setDescription(context.getString(com.aki.anichan.R.string.downloading_update))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setMimeType("application/vnd.android.package-archive")
        }
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return manager.enqueue(request)
    }
}
