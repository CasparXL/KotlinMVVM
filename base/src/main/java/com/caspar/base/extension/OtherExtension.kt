package com.caspar.base.extension

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */

/**
 * 设置TextView的边边图标
 * @param position 0 is top,1 is left,2 is bottom,3 is right
 * @param idRes img
 */
fun AppCompatTextView.setDrawable(position: Int, idRes: Int) {
    when (position) {
        0 -> {//top
            setCompoundDrawablesWithIntrinsicBounds(0, idRes, 0, 0)
        }
        1 -> {//left
            setCompoundDrawablesWithIntrinsicBounds(idRes, 0, 0, 0)
        }
        2 -> {//bottom
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, idRes)
        }
        3 -> {//right
            setCompoundDrawablesWithIntrinsicBounds(0, 0, idRes, 0)
        }
    }
}

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
 * Activity扩展函数，用于给视图添加点击事件
 */
fun Activity.setOnClickListener(
    listener: View.OnClickListener,
    @IdRes vararg viewId: Int
) {
    for (id in viewId) {
        findViewById<View>(id)?.setOnClickListener(listener)
    }
}

/**
 * Fragment扩展函数，用于给视图添加点击事件
 */
fun Fragment.setOnClickListener(
    listener: View.OnClickListener,
    @IdRes vararg viewId: Int
) {
    for (id in viewId) {
        activity?.findViewById<View>(id)?.setOnClickListener(listener)
    }
}
