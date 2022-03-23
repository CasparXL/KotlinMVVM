package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.caspar.base.base.BaseActivity
import com.caspar.commom.ext.setOnClickListener
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.databinding.ActivityTranslateBinding
import com.caspar.xl.viewmodel.TranslateViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class TranslateActivity : BaseActivity<ActivityTranslateBinding>(), View.OnClickListener {
    private val mViewModel: TranslateViewModel by viewModels()

    override fun initIntent() {
        mBindingView.title.tvCenter.text = "翻译"
        setOnClickListener(this, R.id.tv_left)
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            //注意，使用repeatOnLifecycle时，里面如果有多个请求，需用多个launch来实现，否则之后请求的则不生效
            //使用repeatOnLifecycle(Lifecycle.State.STARTED)[多个接口请求请考虑使用这个，效率高]或者flow的flowWithLifecycle()[单个请求考虑这个]
            mViewModel.translateResult.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            it.data?.apply {//UI层可以使用apply，also，等扩展函数让内部安全的执行[这里是为了确保数据源不为空]
                                mBindingView.tvText.text =
                                    "原文:\n${mBindingView.etEnter.text}\n译文:\n${
                                        this.translateResult?.get(0)?.get(0)?.tgt
                                    }"
                            }
                        }
                        is NetworkResult.Error -> {
                            mBindingView.tvText.text = it.message ?: "请检查网络，并重试"
                        }
                        else -> {
                            LogUtil.e("预留加载状态在此处可以弹一个加载框")
                        }
                    }
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