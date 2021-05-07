package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caspar.base.base.BaseViewModel
import com.caspar.xl.bean.Resource
import com.caspar.xl.bean.response.TranslateBean
import com.caspar.xl.repository.MenuRepository
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TranslateViewModel(application: Application) : BaseViewModel(application) {
    //mData，使用Triple<A,B,C>储存网络请求反应，A为网络请求是否成功，B为成功的消息，C为错误的信息
    val mData: MutableLiveData<Triple<Boolean, TranslateBean?, Resource<String>>> by lazy {
        MutableLiveData<Triple<Boolean, TranslateBean?, Resource<String>>>()
    }

    fun translate(text: String) {
        viewModelScope.launch {
            val value = MenuRepository.Translate(text)
            if (value.code == 200) { //网络请求成功，则返回数据
                mData.value = Triple(true, value.body, Resource())
            } else { //网络请求失败，则返回一个errorBean，errorBean一个Activity拦截一次就够了，统一做处理
                mData.value = Triple(false, null, Resource(status = value.code, msg = value.msg, obj = ""))
            }
        }
    }
}
