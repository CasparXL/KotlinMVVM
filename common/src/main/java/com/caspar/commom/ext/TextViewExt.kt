package com.caspar.commom.ext

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 各种自定义扩展函数
 */
/**
 * 设置TextView的边边图标
 * @param position 0 is top,1 is left,2 is bottom,3 is right
 * @param idRes img
 */
fun AppCompatTextView.setDrawable(position: Int, idRes: Int) {
    when (position) {
        0 -> { //top
            setCompoundDrawablesWithIntrinsicBounds(0, idRes, 0, 0)
        }
        1 -> { //left
            setCompoundDrawablesWithIntrinsicBounds(idRes, 0, 0, 0)
        }
        2 -> { //bottom
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, idRes)
        }
        3 -> { //right
            setCompoundDrawablesWithIntrinsicBounds(0, 0, idRes, 0)
        }
    }
}

/**
 * 显示密码文本
 */
fun EditText.showPassword() {
    transformationMethod = HideReturnsTransformationMethod.getInstance()
    setSelection(text.length)
}

/**
 * 隐藏密码文本
 */
fun EditText.hidePassword() {
    transformationMethod = PasswordTransformationMethod.getInstance()
    setSelection(text.length)
}