package com.caspar.base.ext

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

/**
 *  @Create 2020/6/25.
 *  @Use CasparXL
 */

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

/**
 * 扩展函数，用于startActivity(intent)
 * 使用方法如下:
 *    acStart(XxxACTIVITY::class.java)
 * 或者如果需要传输数据进其他界面
 *    acStart(XxxACTIVITY::class.java){
 *         putString("key","value")
 *         putInt("key",123)
 *    }
 */
fun Fragment.acStart(url: Class<*>, block: intentVoid = null) =
    run {
        val intent = Intent(this.context, url)
        block?.let {
            it(intent)
        }
        startActivity(intent)
    }
/**
 * 扩展函数，startActivityForResult(intent,requestCode)
 * 回调接收时会走onActivityForResult方法
 * 使用方法如下:
 *    acStartForResult(XxxACTIVITY::class.java)
 * 或者如果需要传输数据进其他界面
 *    acStartForResult(XxxACTIVITY::class.java){
 *         putString("key","value")
 *         putInt("key",123)
 *    }
 */
fun Fragment.acStartForResult(url: Class<*>, requestCode: Int = 0, block: intentVoid = null) =
    run {
        val intent = Intent(this.context, url)
        block?.let {
            it(intent)
        }
        startActivityForResult(intent, requestCode)
    }