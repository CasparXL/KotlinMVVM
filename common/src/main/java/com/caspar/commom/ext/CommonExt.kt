package com.caspar.commom.ext

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import java.text.DecimalFormat

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

fun Float.toNumberString(int: Int = 1):String {
    val string = StringBuilder("")
    repeat(int){
        string.append("#")
    }
    val df = DecimalFormat("#.${string}")
    val p: String = df.format(this)
    return p
}

fun View.setTintColor(color: Int) {
    val colors = intArrayOf(context.resources.getColor(color), context.resources.getColor(color))
    val states = arrayOfNulls<IntArray>(2)
    states[0] = intArrayOf(R.attr.state_pressed)
    states[1] = intArrayOf(R.attr.state_enabled)
    backgroundTintList = ColorStateList(states, colors)
}