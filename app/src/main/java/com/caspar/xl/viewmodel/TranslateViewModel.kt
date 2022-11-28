package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.eventandstate.ViewEvent
import com.caspar.xl.ext.SharedFlowEvents
import com.caspar.xl.ext.setEvent
import com.caspar.xl.helper.exportError
import com.caspar.xl.helper.otherResult
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

    //设置性质网络的请求，使用MutableSharedFlow，
    //读取并固定展示性质的网络请求，可以使用MutableStateFlow，可以在不必要的时候节省资源,类似于LiveData的生命周期感知
    private val _translateResult: MutableSharedFlow<TranslateBean> = MutableSharedFlow()
    val translateResult = _translateResult.asSharedFlow()

    /**
     * 网络请求
     */
    fun translate(text: String) {
        viewModelScope.launch {
            val result = otherResult { Api.api.translate(text = text) }
            result.onSuccess { _translateResult.emit(it) }
                .onFailure { _viewEvent.setEvent(ViewEvent.ShowToast(exportError(it).second)) }
            _viewEvent.setEvent(ViewEvent.DismissDialog)
        }
    }
}
