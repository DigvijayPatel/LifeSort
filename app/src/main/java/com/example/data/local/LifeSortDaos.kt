package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND status != 'DELETED' ORDER BY computedRank DESC, createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    @Query("SELECT * FROM tasks WHERE status = 'DELETED' ORDER BY updatedAt DESC")
    fun getDeletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeTaskById(id: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE status = 'PAUSED' ORDER BY updatedAt DESC LIMIT 1")
    fun getLatestPausedTask(): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE status = 'IN_PROGRESS' ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveRunningTask(): Flow<TaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}

@Dao
interface PhaseDao {
    @Query("SELECT * FROM phases WHERE taskId = :taskId ORDER BY orderIndex ASC")
    fun getPhasesForTask(taskId: String): Flow<List<TaskPhaseEntity>>

    @Query("SELECT * FROM phases WHERE taskId = :taskId ORDER BY orderIndex ASC")
    suspend fun getPhasesForTaskSync(taskId: String): List<TaskPhaseEntity>

    @Query("SELECT * FROM phases")
    fun getAllPhases(): Flow<List<TaskPhaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhase(phase: TaskPhaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhases(phases: List<TaskPhaseEntity>)

    @Update
    suspend fun updatePhase(phase: TaskPhaseEntity)

    @Query("DELETE FROM phases WHERE id = :id")
    suspend fun deletePhaseById(id: String)

    @Query("DELETE FROM phases WHERE taskId = :taskId")
    suspend fun deletePhasesByTaskId(taskId: String)

    @Query("DELETE FROM phases")
    suspend fun clearAllPhases()
}

@Dao
interface TaskNoteDao {
    @Query("SELECT * FROM task_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<TaskNoteEntity>>

    @Query("SELECT * FROM task_notes WHERE taskId = :taskId ORDER BY createdAt DESC")
    fun getNotesForTask(taskId: String): Flow<List<TaskNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TaskNoteEntity)

    @Query("DELETE FROM task_notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("DELETE FROM task_notes")
    suspend fun clearAllNotes()
}

@Dao
interface LifeGrowthDao {
    @Query("SELECT * FROM life_growth_history ORDER BY timestamp ASC")
    fun getGrowthHistory(): Flow<List<LifeGrowthSnapshotEntity>>

    @Query("SELECT * FROM life_growth_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSnapshot(): Flow<LifeGrowthSnapshotEntity?>

    @Query("SELECT * FROM life_growth_history ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSnapshotSync(): LifeGrowthSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: LifeGrowthSnapshotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshots(snapshots: List<LifeGrowthSnapshotEntity>)

    @Query("DELETE FROM life_growth_history")
    suspend fun clearAllHistory()
}
