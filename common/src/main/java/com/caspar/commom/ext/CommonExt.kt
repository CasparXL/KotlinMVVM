package com.caspar.commom.ext

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 */
//Float类型转换dp
val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

//Int类型转换dp[本质上就是Float转换dp,只不过我们的编码习惯更习惯于int.dp 例: 18.dp]
val Int.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

//Float类型转换sp
val Float.sp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

//Int类型转换sp[本质上就是Float转换sp,只不过我们的编码习惯更习惯于int.sp 例: 18.sp]
val Int.sp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

//转换dip
fun Context.dip(value: Int): Int {
    val scale = resources.displayMetrics.density;
    return (value * scale + 0.5f).toInt()
}

//转换sp
fun Context.sp(value: Int): Int {
    val scale = resources.displayMetrics.scaledDensity;
    return (value * scale + 0.5f).toInt()
}

/**
 * byteArray转换十六进制字符串
 */
fun ByteArray.toHexStr() =
    with(StringBuilder()) {
        this@toHexStr.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) append("0").append(hexStr)
            else append(hexStr)
        }
        toString()
    }