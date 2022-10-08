package com.caspar.xl.utils.rxjava

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.caspar.base.utils.log.LogUtil.e
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import java.util.*

/**
 * desc   : 利用 PublishSubject的特性：与普通的Subject不同，在订阅时并不立即触发订阅事件，
 * 而是允许我们在任意时刻手动调用onNext(),onError(),onCompleted来触发事件。
 */
object RxBus {
    lateinit var mSubject: Subject<Any>

    //全局初始化一个RxBus用于发送事件总线
    fun init() {
        mSubject = PublishSubject.create<Any>().toSerialized()
    }

    private var mSubscriptionMap: HashMap<String, CompositeDisposable> = hashMapOf()

    fun post(o: Any) {
        mSubject.onNext(o)
    }

    /**
     * 保存订阅后的disposable
     *
     * @param o
     * @param disposable
     */
    fun addSubscription(o: String, disposable: Disposable) {
        if (mSubscriptionMap.containsKey(o)) {
            e("订阅key->$o")
            mSubscriptionMap[o]?.add(disposable)
        } else {
            e("订阅key->$o")
            val disposables = CompositeDisposable()
            disposables.add(disposable)
            mSubscriptionMap[o] = disposables
        }
    }

    /**
     * 取消订阅
     */
    fun unRegister(key: String) {
        mSubscriptionMap.apply {
            val observer: CompositeDisposable? = this[key]
            e("删除 $key 的注册,删除的订阅数量->${observer?.size() ?: 0}")
            observer?.dispose()
            this.remove(key)
            e("剩余key订阅的数量:${this.size}")
        }
    }

    inline fun <reified T> rxBusRegister(o: Any, action: Consumer<T>) {
        val disposable = mSubject
            .toFlowable(BackpressureStrategy.BUFFER)
            .ofType(T::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action, { throwable: Throwable ->
                e(throwable)
            })
        addSubscription(o.toString(), disposable)
    }

    /**
     * 例子
     * RxBus.registerForActivityOrFragment<String>(this) {
     *       LogUtil.e("$it")
     * }
     */
    inline fun <reified T> registerForActivityOrFragment(o: LifecycleOwner, action: Consumer<T>) {
        val key = o.toString()
        val disposable = mSubject
            .toFlowable(BackpressureStrategy.BUFFER)
            .ofType(T::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action, { throwable: Throwable ->
                e(throwable)
            })
        addSubscription(key, disposable)
        o.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    unRegister(key) //根据生命周期移除
                    o.lifecycle.removeObserver(this)
                }
            }
        })
    }

    /**
     *
     * 例子
     * RxBus.registerForViewModel<String>(viewModelScope) {
     *       LogUtil.e("$it")
     * }
     */
    inline fun <reified T> registerForViewModel(o: CoroutineScope, action: Consumer<T>) {
        val key = o.toString()
        val disposable = mSubject
            .toFlowable(BackpressureStrategy.BUFFER)
            .ofType(T::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action, { throwable: Throwable ->
                e(throwable)
            })
        addSubscription(key, disposable)
        o.coroutineContext.job.invokeOnCompletion {
            it?.apply {
                e(this)
            }
            unRegister(key) //根据生命周期移除
        }
    }
}