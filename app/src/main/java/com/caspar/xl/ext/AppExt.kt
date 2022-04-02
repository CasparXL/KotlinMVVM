package com.caspar.xl.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty1

internal data class StateTuple1<A>(val a: A)
internal data class StateTuple2<A, B>(val a: A, val b: B)
internal data class StateTuple3<A, B, C>(val a: A, val b: B, val c: C)


/***
 *
 * private val _xxx: MutableStateFlow<ViewState> = MutableStateFlow(ViewState())
 *         val xxx = _xxx.asStateFlow()
 *         //更改状态
 *         _xxx.setState{ copy(xxx) }
 *         //监听状态
 *         mViewModel.boxInfoState.let { states ->
 *              states.observeState(this,ViewState::xxx){
 *                  it.xxx
 *              }
 *         }
 *
 * */
fun <T, A> StateFlow<T>.observeState(
    lifecycleOwner: LifecycleOwner,
    prop1: KProperty1<T, A>,
    action: suspend (A) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeState.map {
                StateTuple1(prop1.get(it))
            }.distinctUntilChanged().collect { (a) ->
                action.invoke(a)
            }
        }
    }
}

fun <T> MutableStateFlow<T>.setState(action: T.() -> T) {
    this.tryEmit(this.value.let(action))
}


/***
 *
 * private val _viewEvent: SharedFlowEvents<ViewEvent> = SharedFlowEvents()
 *         val viewEvent = _viewEvent.asSharedFlow()
 *          //更改状态
 *         _viewEvent.setEvent(ViewEvent.XXX,ViewEvent.XXX)
 *         //监听状态
 *         viewEvent.observeEvent(this){
 *                  when(it){
 *                     xxx->{}
 *                  }
 *         }
 *
 * */
suspend fun <T> SharedFlowEvents<T>.setEvent(vararg values: T) {
    val eventList = values.toList()
    this.emit(eventList)
}

fun <T> SharedFlow<List<T>>.observeEvent(
    lifecycleOwner: LifecycleOwner,
    action: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        this@observeEvent.collect {
            it.forEach { event ->
                action.invoke(event)
            }
        }
    }
}
typealias SharedFlowEvents<T> = MutableSharedFlow<List<T>>

fun <T> SharedFlowEvents(): SharedFlowEvents<T> {
    return MutableSharedFlow()
}