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
    private val repository: RoomRepository by lazy {
        RoomRepository()
    }

    /**
     * 获取学生数量
     */
    suspend fun getAllUser(): List<UserBean> {
        return repository.userDao.getAllUser()
    }

    /**
     * 获取老师数量
     */
    suspend fun getAllTeacher(): List<TeacherBean> {
        return repository.teacherDao.getAllTeacher()
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherSize(): Int {
        return repository.teacherDao.getAllTeacher().size
    }

    /**
     * 获取老师对应的Id
     */
    suspend fun getTeacherForId(index: Int): Long {
        return repository.teacherDao.getAllTeacher()[index].id
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherById(index: Long): TeacherBean {
        return repository.teacherDao.getTeacherById(index)
    }

    /**
     * 新增老师
     */
    suspend fun insertTeacher(index: TeacherBean): Long {
        return repository.teacherDao.insert(index)
    }

    /**
     * 根据老师id获取学生信息
     */
    suspend fun getUserByTid(index: Long): List<UserBean> {
        return repository.userDao.getUserByTid(index)
    }

    /**
     * 根据用户id获取学生信息
     */
    suspend fun getUserById(index: Long): UserBean {
        return repository.userDao.getUserById(index)
    }

    /**
     * 新增学生
     */
    suspend fun insertUser(index: UserBean): Long {
        return repository.userDao.insert(index)
    }
}