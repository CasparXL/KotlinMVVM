package com.caspar.xl.network.util


import com.caspar.base.utils.log.eLog
import com.caspar.xl.bean.BaseBean
import com.google.gson.JsonParseException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * 默认协程全部拦截,使用方式launch(DefaultErrorHandelCoroutine),
 * 当launch代码块内部出现异常,会中断后续代码块,届时，请在launch的invokeOnCompletion代码块中读取该异常并做相应处理
 */
val DefaultErrorHandelCoroutine = CoroutineExceptionHandler { _, _ -> }

/**
 * 解析数据异常
 */
private fun exportError(throwable: Throwable): Pair<Int, String> {
    throwable.eLog()
    return when (throwable) {
        is HttpException -> {
            var errorBody = throwable.response()?.errorBody()?.string()
            errorBody = when (throwable.code()) {
                404 -> "The right resources were not found"
                500 -> "Server internal error"
                else -> NetException.BAD_NETWORK + errorBody
            }
            return throwable.code() to errorBody
        }
        is ConnectException, is UnknownHostException -> -1 to NetException.CONNECT_ERROR
        is InterruptedIOException -> -1 to NetException.CONNECT_TIMEOUT
        is JsonParseException, is JSONException, is ParseException -> -1 to NetException.PARSE_ERROR
        else -> -1 to NetException.UNKNOWN_ERROR
    }
}

/**
 * 有基类的情况下使用
 */
suspend fun <T> baseResult(final: () -> Unit = {}, block: suspend () -> BaseBean<T>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            block().getResult()
        } catch (e: Exception) {
            e.printStackTrace()
            val exportError = exportError(e)
            Result.failure(Exception(exportError.second))
        } finally {
            final()
        }
    }
}

/**
 * 无基类的情况下使用
 */
suspend fun <T> otherResult(final: () -> Unit = {}, block: suspend () -> T): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            Result.success(block.invoke())
        } catch (e: Exception) {
            e.printStackTrace()
            val exportError = exportError(e)
            Result.failure(Exception(exportError.second))
        } finally {
            final()
        }
    }
}



