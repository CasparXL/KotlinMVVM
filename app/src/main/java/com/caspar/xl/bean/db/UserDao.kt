package com.caspar.xl.bean.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 *  @Create 2020/7/4.
 *  @Use
 */
@Dao
interface UserDao {

    @Query("SELECT * from user_table ORDER BY id ASC")
    suspend fun getAllUser(): List<UserBean>

    @Query("SELECT * from user_table where t_id = :tId")
    suspend fun getUserByTid(tId:Long): List<UserBean>

    @Query("SELECT * from user_table where id = :uId")
    suspend fun getUserById(uId:Long): UserBean

    /**
     * 如果操作失败，事务回滚
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserBean) : Long

    /**
     * 如果操作失败，事务回滚
     */
    @Query("Delete from user_table")
    suspend fun deleteAll()
}