package com.caspar.base.helper

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

/**
 * 隐藏软键盘
 */ //打开或关闭软键盘
object KeyBoardUtils {
    /**
     * 打开软键盘
     * 魅族可能会有问题
     *
     * @param mEditText
     * @param mContext
     */
    fun openKeyBord(mEditText: EditText?, mContext: Context) {
        val imm = mContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText
     * @param mContext
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm =
            (mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }

    /**
     * des:隐藏软键盘,这种方式参数为activity
     *
     * @param activity
     */
    fun hideInputForce(activity: Activity?) {
        if (activity == null || activity.currentFocus == null) return
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                activity.currentFocus!!
                    .windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
    }

    /**
     * des:隐藏软键盘,这种方式参数为activity
     *
     * @param activity
     */
    fun hideInputForce(activity: Fragment) {
        activity.activity!!.window
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    /**
     * 打开键盘
     */
    fun showInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.requestFocus()
        imm.showSoftInput(view, 0)
    }
}