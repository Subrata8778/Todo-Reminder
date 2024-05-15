package com.example.todoreminder.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todoreminder.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY due DESC LIMIT 3")
    fun getTasksByStatus(status: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY due DESC")
    fun getAllTasksByStatus(status: String): LiveData<List<Task>>
}
