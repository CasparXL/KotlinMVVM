package com.caspar.base.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class SmartTextView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int =android.R.attr.textViewStyle) :
    AppCompatTextView(context, attrs, defStyleAttr), TextWatcher {
    /**
     * [TextWatcher]
     */
    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
    }

    override fun afterTextChanged(s: Editable?) {
        // 判断当前有没有设置文本达到自动隐藏和显示的效果
        if ("" == text.toString()) {
            if (visibility != View.GONE) {
                visibility = View.GONE
            }
        } else {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }
        }
    }

    init{
        addTextChangedListener(this)
        // 触发一次监听
        afterTextChanged(null)
    }
}