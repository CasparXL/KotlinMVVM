package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.repository.RoomRepository

/**
 *  @Create 2020/7/4.
 *  @Use
 */
class RoomViewModel(application: Application) : AndroidViewModel(application) {
    val user by lazy {
        RoomRepository.userDao
    }
    val teacher by lazy {
        RoomRepository.teacherDao
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherSize(): Int {
        return teacher.getAllTeacher().size
    }

    /**
     * 获取老师对应的Id
     */
    suspend fun getTeacherForId(index: Int): Long {
        return teacher.getAllTeacher()[index].id
    }
    /**
     * 获取老师数量
     */
    suspend fun getTeacherById(index: Long): TeacherBean {
        return teacher.getTeacherById(index)
    }

    /**
     * 新增老师
     */
    suspend fun insertTeacher(index: TeacherBean): Long {
        return teacher.insert(index)
    }

    /**
     * 根据老师id获取学生信息
     */
    suspend fun getUserByTid(index: Long): UserBean {
        return user.getUserByTid(index)
    }

    /**
     * 根据用户id获取学生信息
     */
    suspend fun getUserById(index: Long): UserBean {
        return user.getUserById(index)
    }

    /**
     * 新增学生
     */
    suspend fun insertUser(index: UserBean): Long {
        return user.insert(index)
    }
}