package com.caspar.xl.db

import android.content.Context
import com.caspar.xl.bean.db.MyObjectBox
import com.caspar.xl.bean.db.UserBean
import io.objectbox.Box
import io.objectbox.BoxStore

/**
 * 数据库工具类
 */
object ObjectBox {
    //ObjectBox核心变量
    lateinit var boxStore: BoxStore

    //初始化，若不初始化，工具类其他方法都会导致app崩溃
    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .debugRelations()
            .build()
    }

    /*** 用户信息**/
    fun getUser(): Box<UserBean> = boxStore.boxFor(UserBean::class.java)
}
