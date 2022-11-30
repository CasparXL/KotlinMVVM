package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.bean.response.City
import com.caspar.xl.network.ApiService
import com.caspar.xl.di.BodyOkHttpClient
import com.caspar.xl.di.HeaderOkHttpClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能: 协程相关功能使用
 */
@HiltViewModel
class CoroutinesViewModel @Inject constructor(application: Application, @HeaderOkHttpClient private val api: ApiService) : AndroidViewModel(application) {

    /**
     * 用于封装java类似的回调返回值，使api在协程中可以用变量接收
     * 模拟使用协程返回一个数值出去,注意：Retrofit的方法声明不能带有suspend，否则会走依赖库的协程回调
     * 请确定该回调，调用一次就只会回调一次，否则同一次请求多次resume会导致崩溃
     */
    suspend fun http(): City? {
        return suspendCoroutine {
            api.getSyncCity().enqueue(object : Callback<City> {
                override fun onResponse(call: Call<City>, response: Response<City>) {
                    it.resume(response.body())
                }

                override fun onFailure(call: Call<City>, t: Throwable) {
                    LogUtil.e(t.message)
                    it.resume(null)
                }
            })
        }
    }

    /**
     * 模拟协程超时操作
     * @param time 设置超时时间，单位是毫秒，如果代码块执行时间所花时间超过delay，后续方法块则不执行
     * @param delay 模拟耗时时间,需要花多久时间，这里使用delay做延时
     * @param unit 超时或未超时
     */
    suspend fun timeout(time: Long, delay: Long, unit: () -> Unit) = withTimeoutOrNull(time) {
        delay(delay)
        unit.invoke() //模拟花了 [delay]参数的时间才从回调出去到UI层
    }

    suspend fun cancelPlan(int: Int) = repeat(int) {
        LogUtil.d("任务共计 $int 次,当前执行第 ${it + 1} 次")
        delay(1000)
    }


}
