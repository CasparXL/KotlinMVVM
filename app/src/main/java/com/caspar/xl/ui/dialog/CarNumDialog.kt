package com.caspar.xl.ui.dialog

import android.annotation.SuppressLint
import android.content.*
import android.view.*
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.caspar.base.action.AnimAction
import com.caspar.base.base.BaseDialog
import com.caspar.xl.R
import com.caspar.xl.databinding.DialogCarNumBinding
import com.caspar.xl.utils.keybord.KeyboardUtil


/**
 * 选择车牌号键盘
 */
class CarNumDialog {
    @SuppressLint("ClickableViewAccessibility")
    class Builder constructor(context: Context) : BaseDialog.Builder<Builder>(context),
        BaseDialog.OnKeyListener {
        private lateinit var mBinding: DialogCarNumBinding
        private var listener: OnCheckCarListener? = null
        private val keyboardUtil by lazy {
            KeyboardUtil(
                getActivity()!!,
                mBinding.kvView,
                listOf(
                    mBinding.etProvince,
                    mBinding.et1,
                    mBinding.et2,
                    mBinding.et3,
                    mBinding.et4,
                    mBinding.et5,
                    mBinding.et6,
                    mBinding.et7
                )
            )
        }

        init {
            setContentView(R.layout.dialog_car_num)
            mBinding = DialogCarNumBinding.bind(getContentView()!!)
            setAnimStyle(AnimAction.ANIM_IOS)
            setBackgroundDimEnabled(true)
            setCancelable(false)
            setOnKeyListener(this)
            setOnClickListener(R.id.tv_ui_confirm, R.id.tv_ui_cancel)
            keyboardUtil.setEditText(0)
        }

        /**
         * @param string 默认展示几位字符串到车牌框上
         */
        fun clearText(string: String = ""): Builder {
            keyboardUtil.clearText(string)
            return this
        }

        fun setCarNumListener(listener: OnCheckCarListener): Builder {
            this.listener = listener
            return this
        }

        fun setCancel(@StringRes id: Int): Builder {
            return setCancel(getString(id))
        }

        fun setCancel(text: CharSequence?): Builder {
            mBinding.tvUiCancel.text = text
            mBinding.tvUiCancel.isVisible = !text.isNullOrEmpty()
            return this
        }

        fun setConfirm(@StringRes id: Int): Builder {
            return setConfirm(getString(id))
        }

        fun setConfirm(text: CharSequence?): Builder {
            mBinding.tvUiConfirm.text = text
            return this
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.tv_ui_confirm -> {
                    listener?.onResultCar(keyboardUtil.getText())
                    getDialog()?.dismiss()
                }

                R.id.tv_ui_cancel -> {
                    getDialog()?.dismiss()
                }
            }
        }

        override fun onKey(dialog: BaseDialog?, event: KeyEvent?): Boolean {
            if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
                keyboardUtil.hideKeyboard()
            }
            return false
        }
    }

    fun interface OnCheckCarListener {
        fun onResultCar(carNo: String)
    }
}