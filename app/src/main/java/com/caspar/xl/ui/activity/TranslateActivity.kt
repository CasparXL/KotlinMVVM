package com.caspar.xl.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.caspar.base.base.BaseActivity
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.databinding.ActivityTranslateBinding
import com.caspar.xl.viewmodel.TranslateViewModel

class TranslateActivity : BaseActivity<ActivityTranslateBinding>(), View.OnClickListener {
    private val mViewModel: TranslateViewModel by viewModels()

    override fun initIntent() {
        mBindingView.title.tvCenter.text = "翻译"
        setOnClickListener(this, R.id.tv_left)
    }

    @SuppressLint("SetTextI18n")
    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.mData.observe(this){
            when(it){
                is NetworkResult.Success -> {
                    it.data?.apply {//UI层可以使用apply，also，等扩展函数让内部安全的执行[这里是为了确保数据源不为空]
                        mBindingView.tvText.text = "原文:\n${mBindingView.etEnter.text}\n译文:\n ${this.translateResult?.get(0)?.get(0)?.tgt}"
                    }
                }
                is NetworkResult.Error ->{
                    mBindingView.tvText.text = it.message?:"请检查网络，并重试"
                }
                else -> {
                    LogUtil.e("预留加载状态在此处可以弹一个加载框")
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