package com.caspar.xl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caspar.base.utils.log.LogUtil
import com.caspar.xl.ui.adapter.MessageListBean
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class RefreshListViewModel : ViewModel() {
    var pageNo: Int = 1
    val pageSize: Int = 10
    //是否反方向数据
    var revered: Boolean = false
    var reveredNumber:Int = -4
    private val _messageEvent: MutableSharedFlow<List<MessageListBean>> = MutableSharedFlow()
    val messageEvent = _messageEvent.asSharedFlow()

    fun messageList() {
        viewModelScope.launch {
            val list = mutableListOf<MessageListBean>()
            if (!revered){
                repeat(10){
                    val number = ((pageNo - 1) * pageSize) + it + 1
                    //新数据会全部加入，旧数据会只刷新部分UI，刷新UI的效率会极高
                    list.add(MessageListBean(id = number, name = "${number}", age = number))
                }
            } else {
                if (pageNo == 1){
                    repeat(10){
                        val number = ((pageNo - 1) * pageSize) + it + 1
                        //新数据会全部加入，旧数据会只刷新部分UI，刷新UI的效率会极高
                        list.add(MessageListBean(id = number, name = "${number}", age = number))
                    }
                } else {
                    //模拟一次旧数据在后方
                    reveredNumber = if (reveredNumber == -4) {
                        +1
                    } else {
                        -4
                    }
                    repeat(10){
                        val number = ((pageNo - 1) * pageSize) + it + reveredNumber
                        //新数据会全部加入，旧数据会只刷新部分UI，刷新UI的效率会极高
                        list.add(MessageListBean(id = number, name = "${number + Random.nextInt(30)}", age = number))
                    }
                }
            }
            _messageEvent.emit(list)
        }
    }
}