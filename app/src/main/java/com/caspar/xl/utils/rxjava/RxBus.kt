package com.caspar.xl.utils.rxjava

import com.caspar.base.helper.LogUtil.e
import com.caspar.xl.network.util.GsonUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

/**
 * desc   : 利用 PublishSubject的特性：与普通的Subject不同，在订阅时并不立即触发订阅事件，
 * 而是允许我们在任意时刻手动调用onNext(),onError(),onCompleted来触发事件。
 */
object RxBus {
    private lateinit var mSubject: Subject<Any>

    //全局初始化一个RxBus用于发送事件总线
    fun init() {
        mSubject = PublishSubject.create<Any>().toSerialized()
    }

    private var mSubscriptionMap: HashMap<String, CompositeDisposable?>? = null

    fun post(o: Any) {
        mSubject.onNext(o)
    }

    fun <T> post(o: List<T>) {
        mSubject.onNext(o)
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
    </T> */
    private fun <T> getObservable(type: Class<T>?): Flowable<T> {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
            .ofType(type)
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
    </T> */
    fun <T> getObservableList(type: Class<List<T>?>?): Flowable<List<T>?> {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER)
            .ofType(type)
    }

    /**
     * 一个默认的订阅方法
     *
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
    </T> */
    private fun <T> doSubscribe(
        type: Class<T>?,
        next: Consumer<T>?,
        error: Consumer<Throwable>?
    ): Disposable {
        return getObservable(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(next, error)
    }

    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    fun hasObservers(): Boolean {
        return mSubject.hasObservers()
    }

    /**
     * 保存订阅后的disposable
     *
     * @param o
     * @param disposable
     */
    fun addSubscription(
        o: Any,
        disposable: Disposable?
    ) {
        if (mSubscriptionMap == null) {
            mSubscriptionMap = HashMap()
        }
        val key = o.javaClass.name
        if (mSubscriptionMap!![key] != null) {
            mSubscriptionMap!![key]!!.add(disposable!!)
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            val disposables = CompositeDisposable()
            disposables.add(disposable!!)
            mSubscriptionMap!![key] = disposables
        }
    }

    /**
     * 取消订阅
     *
     * @param o
     */
    fun rxBusUnSubscribe(o: Any) {
        if (mSubscriptionMap == null) {
            return
        }
        val key = o.javaClass.name
        if (!mSubscriptionMap!!.containsKey(key)) {
            return
        }
        if (mSubscriptionMap!![key] != null) {
            mSubscriptionMap!![key]!!.dispose()
        }
        mSubscriptionMap!!.remove(key)
        e("删除界面注册:" + key + "剩余注册界面key数量:" + mSubscriptionMap!!.size)
    }

    fun <T> rxBusRegisterRxBus(o: Any, eventType: Class<T>?, action: Consumer<T>?) {
        val disposable = doSubscribe(
            eventType,
            action,
            Consumer { throwable: Throwable ->
                e(o.javaClass.name + "的RxBus发送订阅报错:" )
                e(throwable)
            }
        )
        addSubscription(o, disposable)
    }

}