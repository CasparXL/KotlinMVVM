package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.commom.helper.LogUtil
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.helper.call
import com.caspar.xl.network.Api
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TranslateViewModel(application: Application) : AndroidViewModel(application) {
    /***基础请求管理**/
    var viewEvent: MutableSharedFlow<ViewEvent> = MutableSharedFlow()
    //网络请求，使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    var translateResult: MutableSharedFlow<TranslateBean> =
        MutableSharedFlow()

    /**
     * 网络请求
     */
    fun translate(text: String) {
        viewModelScope.launch {
            flow<TranslateBean> {
                //当网络请求成功会走完当前void，并返回Success出去
                val result = Api.api.translate(text = text)
                if (result.errorCode == 0) {
                    translateResult.emit(result)
                } else {
                    viewEvent.emit(ViewEvent.ShowToast("翻译失败"))
                }
                viewEvent.emit(ViewEvent.DismissDialog)
            }.onStart {
                viewEvent.emit(ViewEvent.ShowDialog)
            }.catch { ex ->
                viewEvent.emit(ViewEvent.DismissDialog)
                //当网络请求尚未完成，且抛出了error，则返回Error出去
                val networkResult = call(ex)
                LogUtil.d(networkResult.toString())
                viewEvent.emit(ViewEvent.ShowToast("登录失败:${networkResult.second}"))
            }.collect()
        }
    }
}
