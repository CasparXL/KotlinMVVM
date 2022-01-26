package com.caspar.xl.utils.rxjava

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.ConcurrentHashMap

object SharedFlowBus {

    private var events = ConcurrentHashMap<Any, MutableSharedFlow<Any>>()
    private var stickyEvents = ConcurrentHashMap<Any, MutableSharedFlow<Any>>()

    // 发送消息
    //SharedFlowBus.with(objectKey: Class<T>).tryEmit(value: T)
    fun <T> with(objectKey: Class<T>): MutableSharedFlow<T> {
        if (!events.containsKey(objectKey)) {
            events[objectKey] = MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)
        }
        return events[objectKey] as MutableSharedFlow<T>
    }

    // 发送粘性消息
    //SharedFlowBus.withSticky(objectKey: Class<T>).tryEmit(value: T)
    fun <T> withSticky(objectKey: Class<T>): MutableSharedFlow<T> {
        if (!stickyEvents.containsKey(objectKey)) {
            stickyEvents[objectKey] = MutableSharedFlow(1, 1, BufferOverflow.DROP_OLDEST)
        }
        return stickyEvents[objectKey] as MutableSharedFlow<T>
    }

    // 订阅消息
    /*SharedFlowBus.on(objectKey: Class<T>).observe(owner){ it ->
        println(it)
    }*/
    fun <T> on(objectKey: Class<T>): LiveData<T> {
        return with(objectKey).asLiveData()
    }

    // 订阅粘性消息
    /*SharedFlowBus.onSticky(objectKey: Class<T>).observe(owner){ it ->
    println(it)
    }*/
    fun <T> onSticky(objectKey: Class<T>): LiveData<T> {
        return withSticky(objectKey).asLiveData()
    }

}