package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.db.RoomManager
import com.caspar.xl.eventandstate.RoomViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 *  @Create 2020/7/4.
 *  @Use
 */
class RoomViewModel(application: Application) : AndroidViewModel(application) {
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
        return RoomManager.getInstance().getUserDao().getAllUser()
    }

    /**
     * 获取老师数量
     */
    suspend fun getAllTeacher(): List<TeacherBean> {
        return RoomManager.getInstance().getTeacherDao().getAllTeacher()
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherSize(): Int {
        return RoomManager.getInstance().getTeacherDao().getAllTeacher().size
    }

    /**
     * 获取老师对应的Id
     */
    suspend fun getTeacherForId(index: Int): Long {
        return RoomManager.getInstance().getTeacherDao().getAllTeacher()[index].id
    }

    /**
     * 获取老师数量
     */
    suspend fun getTeacherById(index: Long): TeacherBean {
        return RoomManager.getInstance().getTeacherDao().getTeacherById(index)
    }

    /**
     * 新增老师
     */
    suspend fun insertTeacher(index: TeacherBean): Long {
        return RoomManager.getInstance().getTeacherDao().insert(index)
    }

    /**
     * 根据老师id获取学生信息
     */
    suspend fun getUserByTid(index: Long): List<UserBean> {
        return RoomManager.getInstance().getUserDao().getUserByTid(index)
    }

    /**
     * 根据用户id获取学生信息
     */
    suspend fun getUserById(index: Long): UserBean {
        return RoomManager.getInstance().getUserDao().getUserById(index)
    }

    /**
     * 新增学生
     */
    suspend fun insertUser(index: UserBean): Long {
        return RoomManager.getInstance().getUserDao().insert(index)
    }
}