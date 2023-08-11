package com.caspar.base.action

import androidx.annotation.StringRes
import com.hjq.toast.Toaster

/**
 * 在需要的地方实现该接口即可，简单轻便
 */
interface ToastAction {
    fun toast(text: CharSequence?) {
        Toaster.show(text)
    }

    fun toast(@StringRes id: Int) {
        Toaster.show(id)
    }

    fun toast(`object`: Any?) {
        Toaster.show(`object`)
    }
}