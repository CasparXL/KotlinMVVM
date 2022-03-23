package com.caspar.xl.helper


import com.caspar.commom.helper.LogUtil
import com.caspar.xl.bean.NetworkResult
import com.caspar.xl.network.util.NetException
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineExceptionHandler
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * 数据仓库中的异步请求Error回调
 */
fun <T> call(throwable: Throwable):NetworkResult.Error<T>{
    LogUtil.e(throwable)
    return when (throwable) {
        is HttpException -> {
            var errorBody = throwable.response()?.errorBody()?.string()
            errorBody = when (throwable.code()) {
                404 -> "The right resources were not found"
                500 -> "Server internal error"
                else -> NetException.BAD_NETWORK + errorBody
            }
            NetworkResult.Error(code = throwable.code(), message =  errorBody)
        }
        is ConnectException, is UnknownHostException -> NetworkResult.Error(code = -1, message =  NetException.CONNECT_ERROR)
        is InterruptedIOException -> NetworkResult.Error(code = -1, message =  NetException.CONNECT_TIMEOUT)
        is JsonParseException, is JSONException, is ParseException -> NetworkResult.Error(code = -1, message =  NetException.PARSE_ERROR)
        else -> NetworkResult.Error(code = -1, message =  NetException.UNKNOWN_ERROR)
    }
}
