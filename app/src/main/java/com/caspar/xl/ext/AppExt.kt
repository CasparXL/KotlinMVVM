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
 * SharedFlow和StateFlow的浅显区别:
 * SharedFlow:
 *     ① SharedFlow支持多个订阅者，多个订阅者共享实现事件的转播
 *     ② SharedFlow数据会以流的形式发送，不会丢失，新时间不会覆盖旧事件
 *     ③ SharedFlow数据不是粘性的，每个订阅者只会消费一次
 *     ④ SharedFlow不会接受到collect之前发送的事件，因此不能使用repeatOnLifeCycle方法
 *     ⑤ 初始化可变的SharedFlow时，可以不赋默认值，例如-> val sharedFlow :MutableSharedFlow<Bean>() = MutableSharedFlow()
 *     ⑥ MutableSharedFlow可以emit(value)出去，SharedFlow则不可以,他们两个不能通过get方式获取value，只能用observer扩展函数或collect()获取到监听值
 *      总结：
 *      若需要监听的数据在可控范围内，使用SharedFlow,当页面生命周期发生变化时，回调不会重复回来，只有调用emit之后才会触发监听，适用于网络请求等情况
 * StateFlow:
 *     ① StateFlow是一个状态容器式可观察数据流，可以向其收集器发出当前状态更新和新状态更新
 *     ② StateFlow还可通过其 value 属性读取当前状态值。
 *     ③ MutableStateFlow 类的 value 属性分配一个新值
 *     ④ StateFlow 是热数据流：从此类数据流收集数据不会触发任何提供方代码。StateFlow 始终处于活跃状态并存于内存中，而且只有在垃圾回收根中未涉及对它的其他引用时，它才符合垃圾回收条件。
 *     ⑤ 如果需要更新界面，切勿使用 launch 或 launchIn 扩展函数从界面直接收集数据流。即使 View 不可见，这些函数也会处理事件。此行为可能会导致应用崩溃。 为避免这种情况，请使用 repeatOnLifecycle API
 *      总结:
 *      页面状态监听使用StateFlow,适用于页面销毁重建后界面重绘，当界面重新创建时，界面会从StateFlow获取最后一次的值，进而使界面重新绘制成功。
 *
 *      StateFlow和SharedFlow分别适合于UI界面和网络请求的情况，请酌情根据业务需求来使用，切勿滥用导致界面出现莫名其妙的bug
 **/

/***
 * 如何触发StateFlow和SharedFlow相关:
 * 发起一个网络请求
 *  flow<BoxInfoBean> {
 *      //当网络请求成功会走完当前void，并返回Success出去
 *      val result = Api.api.xxx()
 *      if (result.status == 200) {
 *          //网络请求成功，通过该方法发送至Activity的监听- mViewModel.xxxState.let { states -> states.observeState(this).{}}
 *          _xxxState.setState{ copy() }
 *      } else {
 *          //通过该方法发送到Activity的监听- mViewModel.viewEvent.observeEvent(this){ when(it){ xxx->{} } }
 *          _viewEvent.setEvent(ViewEvent.ShowToast("获取装箱详情失败:${result.message}"))
 *      }
 *      //取消Dialog事件
 *      _viewEvent.setEvent(ViewEvent.DismissDialog)
 *  }.retry(3)   //当flow{}发生了异常，会重试3次请求
 *  .onStart {
 *      //当请求发起时，会调用这里
 *      _viewEvent.setEvent(ViewEvent.ShowDialog)
 *  }.catch { ex ->
 *      //发生异常时，会触发这里
 *      //当网络请求尚未完成，且抛出了error，则返回Error出去
 *      val networkResult = call(ex)
 *      LogUtil.d(networkResult.toString())
 *      //请求结束，会先取消弹框，然后showToast弹出提示语
 *      _viewEvent.setEvent(ViewEvent.DismissDialog,ViewEvent.ShowToast("获取装箱详情失败:${networkResult.second}"))
 *  }.collect()
 *
 *
 */

/***
 *
 * data class RoomViewState(
 *    var str: String = "",
 *    var teacherId: Long = -1L,
 *    var userId: Long = -1L,
 * )
 * private val _xxxState: MutableStateFlow<RoomViewState> = MutableStateFlow(RoomViewState())
 *         val xxxState = _xxxState.asStateFlow()
 *         //更改状态
 *         _xxxState.setState{ copy(xxx) }
 *         //监听状态
 *         mViewModel.xxxState.let { states ->
 *              states.observeState(this,RoomViewState::str){
 *                  //当调用了_xxx.setState{ copy(str = "xxx") } 时，该回调方法会进入，如果是重复的数值，因为distinctUntilChanged方法则不会重复进入
 *                  it.xxx
 *              }
 *              states.observeState(this,RoomViewState::teacherId){
 *                  //当调用了_xxx.setState{ copy(teacherId = "xxx") } 时，该回调方法会进入，如果是重复的数值，因为distinctUntilChanged方法则不会重复进入
 *                  it.xxx
 *              }
 *              states.observeState(this,RoomViewState::userId){
 *                  //当调用了_xxx.setState{ copy(userId = "xxx") } 时，该回调方法会进入，如果是重复的数值，因为distinctUntilChanged方法则不会重复进入
 *                  it.xxx
 *              }
 *         }
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
 * sealed class ViewEvent {
 *      data class ShowToast(val message: String) : ViewEvent()
 *      object ShowDialog : ViewEvent()
 *      object DismissDialog : ViewEvent()
 * }
 * private val _viewEvent: SharedFlowEvents<ViewEvent> = SharedFlowEvents()
 *         val viewEvent = _viewEvent.asSharedFlow()
 *         //更改状态
 *         _viewEvent.setEvent(ViewEvent.XXX,ViewEvent.XXX)
 *         //监听状态
 *         mViewModel.viewEvent.observeEvent(this){
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