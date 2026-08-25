package com.example.data.util

import android.content.Context
import coil.Coil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DecimalFormat

object CacheManager {

    suspend fun calculateCacheSize(context: Context): Long = withContext(Dispatchers.IO) {
        var size: Long = 0
        try {
            context.cacheDir?.let { size += getFolderSize(it) }
            context.codeCacheDir?.let { size += getFolderSize(it) }
            context.externalCacheDir?.let { size += getFolderSize(it) }

            // Coil disk cache size
            try {
                val coilDiskCache = Coil.imageLoader(context).diskCache
                if (coilDiskCache != null) {
                    size = maxOf(size, coilDiskCache.size)
                }
            } catch (_: Exception) {}
        } catch (_: Exception) {}
        size
    }

    suspend fun clearCache(context: Context): Long = withContext(Dispatchers.IO) {
        val initialSize = calculateCacheSize(context)

        try {
            // 1. Clear Coil memory and disk caches
            try {
                val imageLoader = Coil.imageLoader(context)
                imageLoader.memoryCache?.clear()
                imageLoader.diskCache?.clear()
            } catch (_: Exception) {}

            // 2. Delete internal cache files
            context.cacheDir?.let { deleteDir(it) }

            // 3. Delete external cache files
            context.externalCacheDir?.let { deleteDir(it) }

            // 4. Delete code cache files
            context.codeCacheDir?.let { deleteDir(it) }
        } catch (_: Exception) {}

        initialSize
    }

    private fun getFolderSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0
        var size: Long = 0
        val files = dir.listFiles() ?: return 0
        for (file in files) {
            size += if (file.isDirectory) {
                getFolderSize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir == null || !dir.exists()) return true
        val children = dir.listFiles() ?: return true
        var success = true
        for (child in children) {
            if (child.isDirectory) {
                success = success && deleteDir(child)
            } else {
                success = success && child.delete()
            }
        }
        return success
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, index.toDouble())
        return DecimalFormat("#,##0.#").format(value) + " " + units[index]
    }
}
