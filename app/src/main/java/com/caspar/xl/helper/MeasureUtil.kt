package com.caspar.xl.helper

import android.content.Context

/**
 * 说明：度量工具类
 * 作者：刘婉
 * 添加时间：2017/3/22 13:08
 * 修改人：刘婉
 * 修改时间：2017/3/22 13:08
 */
object MeasureUtil {
    /**
     * 说明：根据手机的分辨率将dp转成为px
     * 作者：刘婉
     * 添加时间：2017/3/22 13:08
     * 修改人：刘婉
     * 修改时间：2017/3/22 13:08
     */
    fun dip2px(context: Context?, dpValue: Float): Int {
        if (context != null) {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
        return 0
    }

    /**
     * 说明：根据手机的分辨率将sp转成为px
     * 作者：刘婉
     * 添加时间：2017/3/22 18:59
     * 修改人：刘婉
     * 修改时间：2017/3/22 18:59
     */
    fun sp2px(context: Context?, spValue: Float): Int {
        if (context != null) {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
        return 0
    }
}