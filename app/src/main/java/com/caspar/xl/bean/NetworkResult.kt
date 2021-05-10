package com.caspar.xl.bean


/**
 * 密封类，用于封装网络请求之后的数据源，方便UI层使用
 * 请注意，该密封类仅限于使用带有返回值的网络请求，如果无网络请求，建议使用Call<String>来接收数据,这是目前Retrofit2搭配Kotlin以及协程时的缺陷
 */
sealed class NetworkResult<T>(val data: T? = null, val code: Int? = 0, val message: String? = null) {
    //网络请求成功时的内部类，data可为空，因为部分接口请求成功后，可能只会返回成功的状态码等，但数据源不一定有
    class Success<T>(data: T? = null) : NetworkResult<T>(data = data)
    //网络请求失败后的内部类，会将相关信息返回给UI层，以便于区分网络请求的状态
    class Error<T>(data: T? = null, code: Int?, message: String?) : NetworkResult<T>(data = data, code = code, message = message)
    //预留内部类，当请求接口时，可以设置该属性值，在UI层判断如果是该类，弹一个加载中的框给UI层
    class Loading<T>() : NetworkResult<T>()
}