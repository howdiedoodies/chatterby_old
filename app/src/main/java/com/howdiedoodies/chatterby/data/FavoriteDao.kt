package com.howdiedoodies.chatterby.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): List<Favorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favorites: List<Favorite>)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("UPDATE favorites SET isOnline = :online, lastOnline = :timestamp WHERE username = :username")
    suspend fun updateStatus(username: String, online: Boolean, timestamp: Long?)

    @Query("UPDATE favorites SET thumbnailPath = :path WHERE username = :username")
    suspend fun updateThumbnail(username: String, path: String?)

    @Query("UPDATE favorites SET currentGoal = :current, targetGoal = :target WHERE username = :username")
    suspend fun updateGoal(username: String, current: Int?, target: Int?)

    @Query("UPDATE favorites SET lastOnline = :timestamp WHERE username = :username")
    suspend fun updateLastOnline(username: String, timestamp: Long)
}