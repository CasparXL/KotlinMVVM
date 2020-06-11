package com.caspar.xl.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caspar.base.base.BaseViewModel
import com.caspar.xl.bean.Resource
import com.caspar.xl.bean.response.City
import com.caspar.xl.repository.TestRepository
import kotlinx.coroutines.launch

/**
 *  "CasparXL" 创建 2020/5/12.
 *   界面名称以及功能:
 */
class TestViewModel(application: Application) : BaseViewModel(application) {

    //mData，使用Triple<A,B,C>储存网络请求反应，A为网络请求是否成功，B为成功的消息，C为错误的信息
    val mData: MutableLiveData<Triple<Boolean, City?, Resource<String>>> by lazy {
        MutableLiveData<Triple<Boolean, City?, Resource<String>>>()
    }

    fun getCity() {
        viewModelScope.launch {
            val value = TestRepository.getCity()
            if (value.code == 200) { //网络请求成功，则返回数据
                mData.value = Triple(true, value.body, Resource())
            } else {//网络请求失败，则返回一个errorBean，errorBean一个Activity拦截一次就够了，统一做处理
                mData.value = Triple(false, null, Resource(status = value.code,msg = value.msg,obj = ""))
            }
        }
    }
}
