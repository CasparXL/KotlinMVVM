package com.caspar.xl.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView嵌套RecyclerView时子View点击无效，使用该控件可以向上传递touch事件，使点击事件有效(一般用于子图标)
 */
class NoTouchRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}