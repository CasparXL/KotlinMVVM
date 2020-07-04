package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.xl.db.RoomManager

/**
 *  @Create 2020/7/4.
 *  @Use
 */
class RoomViewModel(application: Application) :AndroidViewModel(application) {
    val user by lazy {
        RoomManager.instance.getUserDao().getAllUser()
    }
    val teacher by lazy {
        RoomManager.instance.getTeacherDao().getAllTeacher()
    }
}