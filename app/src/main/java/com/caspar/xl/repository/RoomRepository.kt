package com.caspar.xl.repository

import com.caspar.xl.db.RoomManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *  @Create 2020/7/4.
 *  @Use
 */
object RoomRepository {
      val userDao = RoomManager.instance.getUserDao()

      val teacherDao = RoomManager.instance.getTeacherDao()

}