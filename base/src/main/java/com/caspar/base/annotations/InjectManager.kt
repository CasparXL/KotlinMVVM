package com.caspar.base.annotations

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import com.caspar.base.R

/**
 * @ describe:注入管理
 * @ author: Martin
 * @ createTime: 2019/3/27 20:55
 * @ version: 1.0
 */
object InjectManager {
    /**
     * activity注入
     *
     * @param activity activity
     */
    fun inject(activity: Activity): Int {
        // 布局注入
        return injectActivityLayout(activity)
    }

    /**
     * fragment注入
     *
     * @param fragment fragment
     */
    fun inject(fragment: Fragment): Int {
        // 布局注入
        return injectLayout(fragment)
    }

    private fun injectActivityLayout(activity: Activity): Int {
        // 获取类
        val clazz: Class<out Activity> = activity.javaClass

        // 获取类的注解
        val contentView =
            clazz.getAnnotation(ContentView::class.java)
        return if (contentView != null) {
            // 获取布局的值
            val layoutId: Int = contentView.value
            if (layoutId == ResId.DEFAULT_VALUE) {
                log(
                    clazz,
                    """
                    Error Activity(${getClassName(clazz)}.java:1):

                    """.trimIndent() + activity.getString(
                        R.string.layout_id_error
                    )
                )
                throw RuntimeException(
                    getClassName(clazz) + activity.getString(
                        R.string.layout_id_error
                    )
                )
            }
            layoutId
        } else {
            log(
                clazz,
                """
                Error Activity(${getClassName(clazz)}.java:1):

                """.trimIndent() + activity.getString(
                    R.string.layout_id_error
                )
            )
            throw NullPointerException(
                getClassName(clazz) + activity.getString(
                    R.string.content_view_empty
                )
            )
        }
    }

    private fun injectLayout(fragment: Fragment): Int {
        // 获取类
        val clazz: Class<out Fragment?> = fragment.javaClass

        // 获取类的注解
        val contentView =
            clazz.getAnnotation(ContentView::class.java)
        return if (contentView != null) {
            // 获取布局的值
            val layoutId: Int = contentView.value
            if (layoutId == ResId.DEFAULT_VALUE) {
                log(
                    clazz,
                    """
                    Error Activity(${getClassName(clazz)}.java:1):

                    """.trimIndent() + fragment.getString(
                        R.string.layout_id_error
                    )
                )
                throw RuntimeException(
                    getClassName(clazz) + fragment.getString(
                        R.string.layout_id_error
                    )
                )
            }
            // 第一种方法
            layoutId
        } else {
            log(
                clazz,
                """
                Error Activity(${getClassName(clazz)}.java:1):

                """.trimIndent() + fragment.getString(
                    R.string.layout_id_error
                )
            )
            throw NullPointerException(
                getClassName(clazz) + fragment.getString(
                    R.string.content_view_empty
                )
            )
        }
    }

    private fun log(clazz: Class<*>, s: String) {
        Log.e("浪", "$clazz:$s")
    }

    private fun getClassName(clazz: Class<*>): String {
        var className = clazz.name
        className = if (className.contains("$")) { //用于内部类的名字解析
            className.substring(className.lastIndexOf(".") + 1, className.indexOf("$"))
        } else {
            className.substring(className.lastIndexOf(".") + 1, className.length)
        }
        return className
    }
}