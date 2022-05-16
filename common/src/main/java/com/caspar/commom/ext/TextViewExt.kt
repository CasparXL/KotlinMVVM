package com.caspar.commom.ext

import android.text.InputFilter
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
 * 限制输入框的精度
 */
fun EditText.filterNumber(int: Int){
    val input =
        InputFilter { source, start, end, dest, dstart, dend -> // 删除等特殊字符，直接返回
            if (source.toString().isEmpty()) {
                return@InputFilter null
            }
            val dValue = dest.toString()
            val filter = dValue.filter { it == '.' }
            takeIf {
                (filter.length == 1) and (source.toString() == ".")
            }?.let {
                return@InputFilter ""
            }
            val splitArray = dValue.split(".").toTypedArray()
            takeIf {
                splitArray.size > 1
            }?.let {
                val dotValue = splitArray[1]
                val diff: Int = dotValue.length + 1 - int
                if (diff > 0) {
                    return@InputFilter source.subSequence(start, end - diff)
                }
            }
            null
        }
    this.filters = arrayOf(input)
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