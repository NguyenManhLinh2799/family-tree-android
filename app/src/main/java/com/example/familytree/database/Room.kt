package com.example.familytree.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TreeDao {
    @Query("select * from databasetree")
    fun getTrees(): LiveData<List<DatabaseTree>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg data: DatabaseTree)

    @Insert
    fun insert(tree: DatabaseTree)

    @Query("update databasetree set name = :name, description = :description where id = :id")
    fun update(id: Int?, name: String, description: String?)

    @Query("delete from databasetree where id = :id")
    fun delete(id: Int?)
}

@Database(entities = [DatabaseTree::class], version = 1)
abstract class FamilyTreeDatabase: RoomDatabase() {
    abstract val treeDao: TreeDao
}

private lateinit var INSTANCE: FamilyTreeDatabase

fun getDatabase(context: Context): FamilyTreeDatabase {
    synchronized(FamilyTreeDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context, FamilyTreeDatabase::class.java, "familytree").build()
        }
    }
    return INSTANCE
}