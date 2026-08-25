package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserMediaEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMediaDao {
    @Query("SELECT * FROM user_media_entries ORDER BY updatedAt DESC")
    fun getAllEntries(): Flow<List<UserMediaEntry>>

    @Query("SELECT * FROM user_media_entries WHERE type = :type ORDER BY updatedAt DESC")
    fun getEntriesByType(type: String): Flow<List<UserMediaEntry>>

    @Query("SELECT * FROM user_media_entries WHERE status = :status ORDER BY updatedAt DESC")
    fun getEntriesByStatus(status: String): Flow<List<UserMediaEntry>>

    @Query("SELECT * FROM user_media_entries WHERE type = :type AND status = :status ORDER BY updatedAt DESC")
    fun getEntriesByTypeAndStatus(type: String, status: String): Flow<List<UserMediaEntry>>

    @Query("SELECT * FROM user_media_entries WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flow<List<UserMediaEntry>>

    @Query("SELECT * FROM user_media_entries WHERE mediaId = :mediaId LIMIT 1")
    fun getEntryFlow(mediaId: Int): Flow<UserMediaEntry?>

    @Query("SELECT * FROM user_media_entries WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getEntryById(mediaId: Int): UserMediaEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: UserMediaEntry)

    @Update
    suspend fun update(entry: UserMediaEntry)

    @Query("DELETE FROM user_media_entries WHERE mediaId = :mediaId")
    suspend fun deleteById(mediaId: Int)

    @Query("UPDATE user_media_entries SET progress = :progress, updatedAt = :updatedAt WHERE mediaId = :mediaId")
    suspend fun updateProgress(mediaId: Int, progress: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE user_media_entries SET status = :status, updatedAt = :updatedAt WHERE mediaId = :mediaId")
    suspend fun updateStatus(mediaId: Int, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE user_media_entries SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE mediaId = :mediaId")
    suspend fun updateFavorite(mediaId: Int, isFavorite: Boolean, updatedAt: Long = System.currentTimeMillis())
}
