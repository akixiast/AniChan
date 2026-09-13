package com.aki.anichan.helper.service.update

import com.aki.anichan.data.response.github.GitHubReleaseResponse

data class AppUpdateInfo(
    val latestTag: String,
    val releaseName: String,
    val releaseNotes: String,
    val releasePageUrl: String,
    val apkDownloadUrl: String,
    val isNewer: Boolean
) {
    companion object {
        fun from(response: GitHubReleaseResponse, currentVersionName: String): AppUpdateInfo {
            val apkAsset = response.assets.firstOrNull {
                it.name.endsWith(".apk", ignoreCase = true)
            }
            val downloadUrl = apkAsset?.browserDownloadUrl ?: response.htmlUrl
            return AppUpdateInfo(
                latestTag = response.tagName,
                releaseName = response.name.ifBlank { response.tagName },
                releaseNotes = response.body,
                releasePageUrl = response.htmlUrl,
                apkDownloadUrl = downloadUrl,
                isNewer = isNewerVersion(response.tagName, currentVersionName)
            )
        }

        fun isNewerVersion(latestTag: String, currentVersionName: String): Boolean {
            val latest = latestTag.trim().removePrefix("v").removePrefix("V")
            val current = currentVersionName.trim().removePrefix("v").removePrefix("V")
            if (latest.isBlank() || current.isBlank()) return false
            if (latest.equals(current, ignoreCase = true)) return false
            val latestParts = latest.split(".", "-")
            val currentParts = current.split(".", "-")
            val size = maxOf(latestParts.size, currentParts.size)
            for (i in 0 until size) {
                val l = latestParts.getOrNull(i)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                val c = currentParts.getOrNull(i)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                if (l != c) return l > c
            }
            return latest > current
        }
    }
}
