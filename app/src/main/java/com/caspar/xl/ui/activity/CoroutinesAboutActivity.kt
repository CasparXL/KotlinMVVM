package com.caspar.xl.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.caspar.base.base.BaseActivity
import com.caspar.base.base.BaseDialog
import com.caspar.base.ext.setOnClickListener
import com.caspar.base.utils.log.dLog
import com.caspar.base.utils.log.eLog
import com.caspar.xl.R
import com.caspar.xl.databinding.ActivityCoroutinesAboutBinding
import com.caspar.xl.di.WaitDialogInject
import com.caspar.xl.ext.binding
import com.caspar.xl.ui.dialog.WaitDialog
import com.caspar.xl.ui.viewmodel.CoroutinesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CoroutinesAboutActivity : BaseActivity(),
    View.OnClickListener {
    private val mBindingView: ActivityCoroutinesAboutBinding by binding()
    private val mViewModel: CoroutinesViewModel by viewModels()

    @WaitDialogInject
    @Inject
    lateinit var dialog: WaitDialog.Builder

    //用于模拟取消任务场景做临时变量
    private var job: Job? = null

    override fun initView(savedInstanceState: Bundle?) {
        dialog.setMessage("稍等")
        mBindingView.title.tvCenter.text = "协程场景模拟"
        setOnClickListener(
            this,
            R.id.tv_left,
            R.id.btn_http,
            R.id.btn_timeout,
            R.id.btn_not_timeout,
            R.id.btn_start_plan,
            R.id.btn_cancel_plan
        )
    }

    override fun onClick(v: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (v.id) {
                R.id.tv_left -> finish()
                R.id.btn_http -> {
                    //模拟网络请求
                    dialog.setMessage("网络请求中").show()
                    val httpBean = withContext(Dispatchers.IO) {
                        mViewModel.http()
                    }
                    dialog.dismiss()
                    if (httpBean == null) {
                        toast("网络请求失败，错误详情请查看Studio的Logcat")
                    } else {
                        toast("网络请求成功，请求到的数据有${httpBean.total ?: 0}条")
                    }
                }

                R.id.btn_timeout -> {
                    dialog.setMessage("模拟请求，两秒后将超时，真正操作需要三秒").show()
                    val timeout = mViewModel.timeout(2000, 3000) {
                        "这是耗时操作的回调，如果超时，将被中断，不会打印该日志 --->该操作不会打印日志".dLog()
                    }
                    dialog.dismiss()
                    if (timeout == null) {
                        toast("该方法块超时了，并未执行timeout方法内的代码块")
                    } else {
                        toast("该方法块并未超时，并执行了timeout方法内的代码块")
                    }
                }

                R.id.btn_not_timeout -> {
                    dialog.setMessage("模拟请求，六秒后将超时，真正执行花了三秒").show()
                    val timeout = mViewModel.timeout(6000, 3000) {
                        "这是耗时操作的回调，如果超时，将被中断，不会打印该日志 --->该操作会打印日志".eLog()
                    }
                    dialog.dismiss()
                    if (timeout == null) {
                        toast("该方法块超时了，并未执行timeout方法内的代码块")
                    } else {
                        toast("该方法块并未超时，并执行了timeout方法内的代码块")
                    }
                }

                R.id.btn_start_plan -> {
                    job?.cancel() //结束掉上次的任务
                    val planCount = 10
                    job = launch {
                        mViewModel.cancelPlan(planCount)
                    }
                }

                R.id.btn_cancel_plan -> {
                    job?.cancel() //结束本次任务，如果没有执行任务，则点击无效
                    "停止任务".dLog()
                }
            }
        }
    }

}