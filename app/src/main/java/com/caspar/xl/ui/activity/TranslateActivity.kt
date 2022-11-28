package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityTranslateBinding
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.ext.observeEvent
import com.caspar.xl.viewmodel.TranslateViewModel
import kotlinx.coroutines.launch

class TranslateActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mBindingView: ActivityTranslateBinding
    private val mViewModel: TranslateViewModel by viewModels()
    override fun getViewBinding(): ViewBinding {
        return ActivityTranslateBinding.inflate(layoutInflater).apply {
            mBindingView = this
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mBindingView.title.tvCenter.text = "翻译"
        setOnClickListener(this, R.id.tv_left)
        initViewObserver()
        initNetworkObserver()
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

    //网络请求监听
    private fun initNetworkObserver() {
        lifecycleScope.launch {
            mViewModel.translateResult.collect {
                mBindingView.tvText.text = "原文:\n${mBindingView.etEnter.text}\n译文:\n${
                    it.translateResult?.get(0)?.get(0)?.tgt
                }"
            }
        }
    }

    //视图事件监听
    private fun initViewObserver() {
        mViewModel.viewEvent.observeEvent(this@TranslateActivity) {
            when (it) {
                ViewEvent.DismissDialog -> LogUtil.d("请求结束")
                ViewEvent.ShowDialog -> LogUtil.d("开始请求")
                is ViewEvent.ShowToast -> {
                    mBindingView.tvText.text = it.message
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