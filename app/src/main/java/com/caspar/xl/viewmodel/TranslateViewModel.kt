package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.ext.SharedFlowEvents
import com.caspar.xl.ext.setEvent
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
    private val _viewEvent: SharedFlowEvents<ViewEvent> = SharedFlowEvents()
    val viewEvent = _viewEvent.asSharedFlow()
    //网络请求，使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    private val _translateResult: MutableSharedFlow<TranslateBean> = MutableSharedFlow()
    val translateResult = _translateResult.asSharedFlow()
    /**
     * 网络请求
     */
    fun translate(text: String) {
        viewModelScope.launch {
            flow<TranslateBean> {
                //当网络请求成功会走完当前void，并返回Success出去
                val result = Api.api.translate(text = text)
                if (result.errorCode == 0) {
                    _translateResult.emit(result)
                } else {
                    _viewEvent.setEvent(ViewEvent.ShowToast("请求失败"))
                }
            }.onStart {
                _viewEvent.setEvent(ViewEvent.ShowDialog)
            }.catch { ex ->
                //当网络请求尚未完成，且抛出了error，则返回Error出去
                val networkResult = call(ex)
                _viewEvent.setEvent(ViewEvent.ShowToast("请求失败:${networkResult.second}"))
            }.onCompletion {
                _viewEvent.setEvent(ViewEvent.DismissDialog)
            }.collect()
        }
    }
}
