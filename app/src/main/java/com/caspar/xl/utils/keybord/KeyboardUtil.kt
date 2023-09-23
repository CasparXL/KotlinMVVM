package com.caspar.xl.utils.keybord

import android.annotation.SuppressLint
import android.app.Activity
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.isVisible
import com.caspar.base.utils.log.eLog
import com.caspar.base.utils.log.wLog
import com.caspar.xl.R

/**
 * 键盘视图
 */
@SuppressLint("ClickableViewAccessibility")
class KeyboardUtil(
    private var activity: Activity,
    private val mKeyboardView: KeyboardView,
    private val editList: List<TextView>
) {

    private val provinceKeyboard: Keyboard by lazy {
        Keyboard(activity, R.xml.province)
    }
    private val abcKeyboard: Keyboard by lazy {
        Keyboard(activity, R.xml.letter)
    }

    //当前是多少
    private var index: Int = 0

    init {
        mKeyboardView.apply {
            keyboard = provinceKeyboard
            isEnabled = true
            // 设置按键没有点击放大镜显示的效果
            isPreviewEnabled = false
            setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
                override fun onPress(primaryCode: Int) {
                    /*if (primaryCode in Char.MIN_VALUE.code..Char.MAX_VALUE.code){
                        editList.getOrNull(index)?.text = Char(primaryCode).toString()
                    }*/
                }

                override fun onRelease(primaryCode: Int) {}
                override fun onText(text: CharSequence?) {}
                override fun swipeLeft() {}
                override fun swipeRight() {}
                override fun swipeDown() {}
                override fun swipeUp() {}
                override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                    val editable = editList.getOrNull(index)
                    when (primaryCode) {
                        -1 -> changeKeyboard(true)
                        -2 -> changeKeyboard(false)
                        -3 -> {
                            if (index > 0) {
                                editList[index - 1].apply {
                                    requestFocus()
                                    text = null
                                    editList[index].setBackgroundResource(
                                        if (index == editList.size - 1) {
                                            R.drawable.shape_et_green
                                        } else {
                                            R.drawable.shape_et
                                        }
                                    )
                                    setBackgroundResource(R.drawable.shape_et_select)
                                }

                                setEditText(index - 1)
                            }
                            editable?.text = null
                        }

                        else -> {
                            // 清空之前数据
                            editList.getOrNull(index)?.text = primaryCode.toChar().toString()
                        }
                    }
                }
            })
        }
        hideSoftInputMethod(editList.first())
        showKeyboard()
        setEditText(0)
        editList.first().setBackgroundResource(R.drawable.shape_et_select)
        editList.forEachIndexed { index, edit ->
            edit.setOnTouchListener(
                MyOnTouchListener(
                    index = index,
                    isNumber = index != 0,
                    etList = editList,
                )
            )
            if (index < 6) {
                edit.addTextChangedListener(
                    MyTextWatcher(
                        curEditText = edit,
                        nextEditText = editList[index + 1],
                        isNumber = true
                    )
                )
            }
        }
    }

    fun clearText(default: String = "") {
        if (default.length > editList.count()) {
            "车牌字符串不能大于控件数量".wLog()
            return
        }
        editList.forEachIndexed { index, editText ->
            val length = default.length
            if (length - 1 > index) {
                editText.text = default.getOrNull(index)?.toString()
            } else if (length - 1 == index) {
                editText.text = default.getOrNull(index)?.toString()
            } else {
                editText.text = null
            }
            editText.setBackgroundResource(if (index == editList.size - 1) R.drawable.shape_et_green else R.drawable.shape_et)
        }
        setEditText(default.length)
        editList[default.length].apply {
            requestFocus()
            setBackgroundResource(R.drawable.shape_et_select)
        }
    }

    fun getText(): String {
        return editList.joinToString("") { it.text.toString() }
    }

    fun setEditText(index: Int) {
        this.index = index
        changeKeyboard(index != 0)
    }

    /**
     * 指定切换软键盘
     * isNumber false 省份软键盘， true 数字字母软键盘
     */
    fun changeKeyboard(isNumber: Boolean) {
        if (isNumber) {
            mKeyboardView.keyboard = abcKeyboard
        } else {
            mKeyboardView.keyboard = provinceKeyboard
        }
    }

    /**
     * 软键盘展示状态
     */
    fun isShow() = mKeyboardView.visibility == View.VISIBLE

    /**
     * 显示软键盘
     */
    fun showKeyboard() {
        val visibility = mKeyboardView.visibility
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.visibility = View.VISIBLE
        }
    }

    /**
     * 隐藏软键盘
     */
    fun hideKeyboard() {
        val visibility = mKeyboardView.visibility
        if (visibility == View.VISIBLE) {
            mKeyboardView.visibility = View.INVISIBLE
        }
    }

    /**
     * 禁掉系统软键盘
     */
    fun hideSoftInputMethod(editText: TextView) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        editText.inputType = InputType.TYPE_NULL
    }

    inner class MyTextWatcher(
        private val curEditText: TextView,
        private val nextEditText: TextView,
        private val isNumber: Boolean,
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (it.isNotEmpty()) {
                    // 下个控件获取焦点
                    nextEditText.requestFocus()
                    //nextEditText.setSelection(nextEditText.text.length)
                    // 更新背景
                    curEditText.setBackgroundResource(R.drawable.shape_et)
                    nextEditText.setBackgroundResource(R.drawable.shape_et_select)
                    // 切换键盘
                    changeKeyboard(isNumber)
                    setEditText(index = index + 1)
                }
            }
        }
    }

    inner class MyOnTouchListener(
        private val index: Int,
        private val isNumber: Boolean,
        private val etList: List<TextView>,
    ) : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            changeEditTextBg(index, etList)
            if (!mKeyboardView.isVisible) {
                showKeyboard()
            }
            // 切换键盘
            hideSoftInputMethod(v as TextView)
            changeKeyboard(isNumber)
            setEditText(index)
            //v.setSelection(v.text.length)
            return false
        }

        // 更新控件背景
        private fun changeEditTextBg(index: Int, etList: List<TextView>) {
            etList[0].setBackgroundResource((if (index == 0) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[1].setBackgroundResource((if (index == 1) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[2].setBackgroundResource((if (index == 2) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[3].setBackgroundResource((if (index == 3) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[4].setBackgroundResource((if (index == 4) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[5].setBackgroundResource((if (index == 5) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[6].setBackgroundResource((if (index == 6) R.drawable.shape_et_select else R.drawable.shape_et))
            etList[7].setBackgroundResource((if (index == 7) R.drawable.shape_et_select else R.drawable.shape_et_green))
        }
    }
}

