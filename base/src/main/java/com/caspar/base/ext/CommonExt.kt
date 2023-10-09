package com.caspar.base.ext

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 */
//Float类型转换dp
val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics)

//Int类型转换dp[本质上就是Float转换dp,只不过我们的编码习惯更习惯于int.dp 例: 18.dp]
val Int.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

//Float类型转换sp
val Float.sp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics)

//Int类型转换sp[本质上就是Float转换sp,只不过我们的编码习惯更习惯于int.sp 例: 18.sp]
val Int.sp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics).toInt()

//获取版本号
fun Context.getVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
}

fun Number?.toNumberString(int: Int = 2): String {
    val string = "%.${int}f"
    return this?.let {
        if (this is Float || this is Int) {
            return@let String.format(string, this.toFloat())
        } else {
            return@let String.format(string, this)
        }
    } ?: run {
        val result = 0.00
        return String.format(string, result)
    }
}

fun Int.toDoubleString(): String {
    return if (this < 10) {
        "0${this}"
    } else {
        this.toString()
    }
}

fun Int.toBinaryString(): String {
    var localn = this
    var binaryNumber: Long = 0
    var remainder: Int
    var i = 1
    while (localn != 0) {
        remainder = localn % 2
        localn /= 2
        binaryNumber += (remainder * i).toLong()
        i *= 10
    }
    val string = StringBuilder()
    val size = binaryNumber.toString().length
    val sub = 8 - size
    repeat(sub){
        string.append("0")
    }
    string.append(binaryNumber.toString())
    return string.toString()
}

/**
 * 设置图片的tint颜色
 * @param color 颜色
 * @param background 是否设置背景，是的话，仅背景色设置，否则只设置图片资源的tint
 */
fun ImageView.setTintColor(@ColorRes color: Int, background: Boolean = false) {
    if (background) {
        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
    } else {
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
    }
}


/**
 * 设置图片的tint颜色
 * @param color 颜色
 */
fun View.setTintColor(color: Int) {
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,color))
}
