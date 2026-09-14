package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.LifeGrowthSnapshotEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskNoteEntity
import com.example.data.model.TaskPhaseEntity

@Database(
    entities = [
        TaskEntity::class,
        TaskPhaseEntity::class,
        TaskNoteEntity::class,
        LifeGrowthSnapshotEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LifeSortDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun phaseDao(): PhaseDao
    abstract fun noteDao(): TaskNoteDao
    abstract fun lifeGrowthDao(): LifeGrowthDao

    companion object {
        @Volatile
        private var INSTANCE: LifeSortDatabase? = null

        fun getInstance(context: Context): LifeSortDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeSortDatabase::class.java,
                    "life_sort_cosmic.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
