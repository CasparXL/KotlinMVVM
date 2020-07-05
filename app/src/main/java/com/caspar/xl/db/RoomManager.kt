package com.caspar.xl.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caspar.xl.app.BaseApplication
import com.caspar.xl.bean.db.TeacherBean
import com.caspar.xl.bean.db.TeacherDao
import com.caspar.xl.bean.db.UserBean
import com.caspar.xl.bean.db.UserDao

/**
 *  @Create 2020/7/4.
 *  @Use
 */
@Database(entities = [TeacherBean::class, UserBean::class], version = 1)
abstract class RoomManager : RoomDatabase() {
    companion object {
        val instance by lazy {
            return@lazy Room.databaseBuilder(
                BaseApplication.context,
                RoomManager::class.java,
                "test_database"
            )
//                .allowMainThreadQueries()  //如果想要支持接口被主线程调用，则使用该方法，默认必须是异步调用，否则使用Room时会导致app崩溃
                .build()
        }
    }

    abstract fun getUserDao(): UserDao

    abstract fun getTeacherDao(): TeacherDao
}