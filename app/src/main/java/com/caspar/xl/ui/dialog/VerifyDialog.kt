package com.caspar.xl.ui.dialog

import android.content.*
import android.view.*
import com.caspar.base.action.AnimAction
import com.caspar.base.base.BaseDialog
import com.caspar.xl.R
import com.caspar.xl.databinding.DialogVerifyBinding
import com.caspar.xl.widget.captcha.Captcha

class VerifyDialog {
    @Suppress("UNCHECKED_CAST", "LeakingThis")
    open class Builder<B : Builder<B>>(context: Context) : BaseDialog.Builder<B>(context) {
        private var autoDismiss = true
        var mBindingView : DialogVerifyBinding

        init {
            setContentView(R.layout.dialog_verify)
            mBindingView = DialogVerifyBinding.bind(getContentView()!!)
            setAnimStyle(AnimAction.ANIM_IOS)
            setGravity(Gravity.CENTER)
        }


        fun setAutoDismiss(dismiss: Boolean): B {
            autoDismiss = dismiss
            return this as B
        }

        fun setListener(listener: Captcha.CaptchaListener): B {
            mBindingView.captCha.setCaptchaListener(listener)
            return this as B
        }

        fun autoDismiss() {
            if (autoDismiss) {
                dismiss()
            }
        }

        override fun onClick(view: View) {

        }
    }

}