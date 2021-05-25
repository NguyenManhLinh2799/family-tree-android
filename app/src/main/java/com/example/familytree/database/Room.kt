package com.example.familytree.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AuthDataDao {
    @Query("select * from databaseauthdata")
    fun getAuthData(): DatabaseAuthData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(authData: DatabaseAuthData)
}

@Database(entities = [DatabaseAuthData::class], version = 1)
abstract class FamilyTreeDatabase: RoomDatabase() {
    abstract val authDataDao: AuthDataDao
}

private lateinit var INSTANCE: FamilyTreeDatabase

fun getDatabase(context: Context): FamilyTreeDatabase {
    synchronized(FamilyTreeDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context, FamilyTreeDatabase::class.java, "familytree")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}