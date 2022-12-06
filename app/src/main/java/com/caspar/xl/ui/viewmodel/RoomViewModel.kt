package com.caspar.xl.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.TeacherDao
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.bean.db.UserDao
import com.caspar.xl.db.RoomManager
import com.caspar.xl.eventandstate.RoomViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 *  @Create 2020/7/4.
 *  @Use
 */
@HiltViewModel
class RoomViewModel @Inject constructor(
    application: Application,
    private val teacherDao: TeacherDao,
    private val userDao: UserDao
) : AndroidViewModel(application) {
    private val _viewStates = MutableStateFlow(RoomViewState())
    val viewStates = _viewStates.asStateFlow()

    fun updateStr(str: String) {
        _viewStates.tryEmit(viewStates.value.copy(str = str))
    }

    fun updateUserId(userId: Long) {
        _viewStates.tryEmit(viewStates.value.copy(userId = userId))
    }

    fun updateTeacherId(teacherId: Long) {
        _viewStates.tryEmit(viewStates.value.copy(teacherId = teacherId))
    }

    /**
     * 获取学生数量
     */
    suspend fun getAllUser(): List<UserBean> {
        return userDao.getAllUser()
    }

    /**
     * 获取老师数量
     */
    suspend fun getAllTeacher(): List<TeacherBean> {
        return teacherDao.getAllTeacher()
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherSize(): Int {
        return teacherDao.getAllTeacher().size
    }

    /**
     * 获取老师对应的Id
     */
    suspend fun getTeacherForId(index: Int): Long {
        return teacherDao.getAllTeacher()[index].id
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherById(index: Long): TeacherBean {
        return teacherDao.getTeacherById(index)
    }

    /**
     * 新增老师
     */
    suspend fun insertTeacher(index: TeacherBean): Long {
        return teacherDao.insert(index)
    }

    /**
     * 根据老师id获取学生信息
     */
    suspend fun getUserByTid(index: Long): List<UserBean> {
        return userDao.getUserByTid(index)
    }

    /**
     * 根据用户id获取学生信息
     */
    suspend fun getUserById(index: Long): UserBean {
        return userDao.getUserById(index)
    }

    /**
     * 新增学生
     */
    suspend fun insertUser(index: UserBean): Long {
        return userDao.insert(index)
    }

    /**
     * 新增学生
     */
    suspend fun clear() {
        userDao.deleteAll()
        teacherDao.deleteAll()
    }
}