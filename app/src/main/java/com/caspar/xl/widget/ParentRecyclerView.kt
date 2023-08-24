package com.caspar.xl.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView嵌套RecyclerView时滑动子View无效或卡顿问题，使用该控件可以向下传递touch事件，使子控件点击事件以及滑动事件有效且流畅，父控件不允许滑动
 */
class ParentRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }
}