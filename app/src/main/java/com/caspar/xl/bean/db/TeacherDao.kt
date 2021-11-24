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
    @Query("SELECT * FROM TEACHER_TABLE ORDER BY id ASC")
    suspend fun getAllTeacher(): List<TeacherBean>

    @Query("SELECT * FROM TEACHER_TABLE where id = :id")
    suspend fun getTeacherById(id:Long): TeacherBean
    /**
     * 如果操作失败，事务回滚
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(teacher: TeacherBean): Long

    /**
     * 如果操作失败，事务回滚
     */
    @Query("DELETE from TEACHER_TABLE")
    suspend fun deleteAll()
}