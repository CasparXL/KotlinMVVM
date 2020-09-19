package com.caspar.xl.bean.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *  @Create 2020/7/4.
 *  @Use
 */
@Dao
interface TeacherDao {
    @Query("SELECT * from teacher_table ORDER BY id ASC")
    suspend fun getAllTeacher(): List<TeacherBean>

    @Query("SELECT * from teacher_table where id = :id")
    suspend fun getTeacherById(id:Long): TeacherBean
    /**
     * 如果操作失败，事务回滚
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher: TeacherBean): Long

    /**
     * 如果操作失败，事务回滚
     */
    @Query("Delete from teacher_table")
    suspend fun deleteAll()
}