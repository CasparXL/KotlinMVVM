package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityTranslateBinding
import com.caspar.xl.di.WaitDialogInject
import com.caspar.xl.eventandstate.ViewState
import com.caspar.xl.ext.binding
import com.caspar.xl.ext.observeEvent
import com.caspar.xl.helper.loadNet
import com.caspar.xl.ui.dialog.WaitDialog
import com.caspar.xl.ui.viewmodel.TranslateViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TranslateActivity : BaseActivity(), View.OnClickListener {
    private val mBindingView: ActivityTranslateBinding by binding()

    private val mViewModel: TranslateViewModel by viewModels()

    @WaitDialogInject
    @Inject
    lateinit var dialog: WaitDialog.Builder

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "翻译"
        setOnClickListener(this, R.id.tv_left)
        initViewObserver()
        mBindingView.etEnter.setOnClickListener {
            mViewModel.getImage()
        }
    }

    //视图事件监听
    private fun initViewObserver() {
        mViewModel.viewEvent.observeEvent(this@TranslateActivity) {
            when (it) {
                is ViewState.Content -> {
                    if (it is ViewState.Content.TransitionViewState) {
                        dialog.dismiss()
                        mBindingView.ivImage.loadNet(
                            it.imageList.random().url ?: "",
                            R.drawable.image_loading_bg
                        )
                    }
                }

                is ViewState.Global -> {
                    when (it) {
                        ViewState.Global.Loading -> {
                            dialog.show()
                        }

                        is ViewState.Global.Error -> {
                            toast(it.message)
                            dialog.dismiss()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun hideSoftByEditViewIds(): IntArray {
        return arrayListOf(R.id.et_enter).toIntArray()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_left -> finish()
        }
    }

}