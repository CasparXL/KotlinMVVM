package com.caspar.base.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.base.action.ToastAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.json.JSONArray
import org.json.JSONTokener
import kotlin.coroutines.CoroutineContext

/**
 * 没有viewModel的情况
 */
open class BaseViewModel(application: Application) :
    AndroidViewModel(application), CoroutineScope, ToastAction {
    var job = Job()

    //重写协程上下文，使它包含ViewModel的上下文
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()//取消协程上下文
        job.cancel()//取消协程工作工作域
    }

}