package com.caspar.base.ext

import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

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
 * 设置TextView的边边图标
 * @param position 0 is top,1 is left,2 is bottom,3 is right
 * @param idRes img
 */
fun AppCompatTextView.setDrawableSize(position: Int, idRes: Int, wSize:Int = 0, hSize:Int = 0) {
    val drawable = if (wSize == 0){
        ContextCompat.getDrawable(context, idRes)
    } else {
        null
    }
    when (position) {
        0 -> { //top
            drawable?.let {
                it.setBounds(0, 0, wSize, hSize)
                setCompoundDrawables(null, it, null, null)
            }?:run{
                setCompoundDrawablesWithIntrinsicBounds(0, idRes, 0, 0)
            }
        }
        1 -> { //left
            drawable?.let {
                it.setBounds(0, 0, wSize, hSize)
                setCompoundDrawables(it, null, null, null)
            }?:run{
                setCompoundDrawablesWithIntrinsicBounds(idRes, 0, 0, 0)
            }
        }
        2 -> { //bottom
            drawable?.let {
                it.setBounds(0, 0, wSize, hSize)
                setCompoundDrawables(null, null, null, it)
            }?:run{
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, idRes)
            }
        }
        3 -> { //right
            drawable?.let {
                it.setBounds(0, 0, wSize, hSize)
                setCompoundDrawables(null, null, it, null)
            }?:run{
                setCompoundDrawablesWithIntrinsicBounds(0, 0, idRes, 0)
            }
        }
    }
}

/**
 * 限制输入框的精度
 */
fun EditText.filterNumber(int: Int = 1){
    /**
     * @param source  将要插入的字符串，来自键盘输入、粘贴
     * @param start  source的起始位置，为0（暂时没有发现其它值的情况）输入-0，删除-0
     * @param end  source的长度: 输入-文字的长度，删除-0
     * @param dest  EditText中已经存在的字符串，原先显示的内容
     * @param dstart  插入点的位置：输入-原光标位置，删除-光标删除结束位置
     * @param dend  输入-原光标位置，删除-光标删除开始位置
     */
    val input =
        InputFilter { source, start, end, dest, dstart, dend -> // 删除等特殊字符，直接返回
            if (source.toString().isEmpty()) {
                return@InputFilter null
            }
            val dValue = dest.toString()
            val toStrings = StringBuilder(dValue)
            toStrings.insert(dstart,source)
            val filter = toStrings.filter { it == '.' }
            takeIf {
                (filter.length > 1) and (source.toString() == ".")
            }?.let {
                return@InputFilter ""
            }
            val index = dValue.indexOf(".")
            val splitArray = dValue.split(".").toTypedArray()
            takeIf {
                splitArray.size > 1 && dstart > index
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