package com.caspar.base.helper

import android.os.SystemClock

/**
 * desc   : 防双击判断工具类
 */
object DoubleClickHelper {
    /** 数组的长度为2代表只记录双击操作  */
    private val TIME_ARRAY = LongArray(2) // 默认间隔时长

    /**
     * 是否在短时间内进行了双击操作
     */
    val isOnDoubleClick: Boolean
        get() = // 默认间隔时长
            isOnDoubleClick(1000)

    /**
     * 是否在短时间内进行了双击操作,false是第一次点击,true是第二次点击,当为true的时候不执行操作
     */
    fun isOnDoubleClick(time: Int): Boolean {
        System.arraycopy(TIME_ARRAY, 1, TIME_ARRAY, 0, TIME_ARRAY.size - 1)
        TIME_ARRAY[TIME_ARRAY.size - 1] = SystemClock.uptimeMillis()
        return TIME_ARRAY[0] >= SystemClock.uptimeMillis() - time
    }
}