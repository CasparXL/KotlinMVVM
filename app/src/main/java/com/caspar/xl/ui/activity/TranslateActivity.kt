package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.caspar.base.base.BaseActivity
import com.caspar.commom.ext.setOnClickListener
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityTranslateBinding
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.ext.observeEvent
import com.caspar.xl.viewmodel.TranslateViewModel
import kotlinx.coroutines.launch

class TranslateActivity : BaseActivity<ActivityTranslateBinding>(), View.OnClickListener {
    private val mViewModel: TranslateViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "翻译"
        setOnClickListener(this, R.id.tv_left)
        mViewModel.viewEvent.observeEvent(this@TranslateActivity) {
            when(it){
                ViewEvent.DismissDialog -> LogUtil.d("请求结束")
                ViewEvent.ShowDialog -> LogUtil.d("开始请求")
                is ViewEvent.ShowToast -> {
                    mBindingView.tvText.text = it.message
                }
            }
        }
        lifecycleScope.launch {
            mViewModel.translateResult.collect {
                mBindingView.tvText.text ="原文:\n${mBindingView.etEnter.text}\n译文:\n${ it.translateResult?.get(0)?.get(0)?.tgt}"
            }
        }
        mBindingView.etEnter.addTextChangedListener { text ->
            run {
                if (text.isNullOrEmpty()) {
                    mBindingView.tvText.text = ""
                } else {
                    mViewModel.translate(text.toString())
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