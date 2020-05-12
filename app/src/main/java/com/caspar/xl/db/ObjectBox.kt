package com.caspar.xl.db

import android.content.Context
import com.caspar.xl.bean.db.MyObjectBox
import io.objectbox.BoxStore

class ObjectBox {
    companion object {
        lateinit var boxStore: BoxStore
        @JvmStatic
        fun init(context: Context) {
            boxStore = MyObjectBox.builder()
                .androidContext(context.applicationContext)
                .debugRelations()
                .build()
        }
    }
}
