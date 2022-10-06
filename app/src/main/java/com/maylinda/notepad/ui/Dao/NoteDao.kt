package com.maylinda.notepad.ui.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.maylinda.notepad.ui.Model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(note: Note)

    @Query("SELECT * FROM note ORDER BY id DESC")
    fun getCardsData():LiveData<List<Note>>

    @Update
    suspend fun update (note:Note)

    @Query("SELECT * FROM note WHERE data LIKE :data")
    fun search(data:String):LiveData<List<Note>>

    @Delete
   suspend fun delete(note:Note)

}