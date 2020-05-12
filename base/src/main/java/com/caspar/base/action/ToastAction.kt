package com.caspar.base.action

import androidx.annotation.StringRes
import com.hjq.toast.ToastUtils

/**
 * 在需要的地方实现该接口即可，简单轻便
 */
interface ToastAction {
    fun toast(text: CharSequence?) {
        ToastUtils.show(text)
    }

    fun toast(@StringRes id: Int) {
        ToastUtils.show(id)
    }

    fun toast(`object`: Any?) {
        ToastUtils.show(`object`)
    }
}