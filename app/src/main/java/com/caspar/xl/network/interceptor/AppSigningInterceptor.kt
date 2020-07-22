package com.caspar.xl.network.interceptor

import androidx.lifecycle.MutableLiveData
import com.caspar.base.helper.LogUtil
import com.caspar.xl.R
import com.caspar.xl.app.BaseApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.*

/**
 * 下载拦截，由于该拦截器跟Log的拦截器中有冲突，所以建议使用两个网络请求的api，这样就不会有冲突了
 *  @param format true 代表 0KB/100KB格式 false 代表百分比格式
 */
class AppSigningInterceptor(private val format: Boolean = true) : Interceptor {
    var totalBytesRead = 0L

    /**
     * 流文件下载，目前测试该类型可获取范围
     */
    enum class StreamType(val type: String) {
        STREAM("application/octet-stream"),
    }

    /**
     * 音频文件下载，目前测试该类型不可获取范围，因此需要使用skip对文件进行字节跳转操作
     */
    enum class MediaType(val type: String) {
        AUDIO("audio/mpeg"),
        VIDEO("video/mp4")
    }

    companion object {
        val mLiveData: MutableLiveData<Triple<String, String, Boolean>> by lazy {
            MutableLiveData<Triple<String, String, Boolean>>()
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url().toString()
        val originalResponse = chain.proceed(request)
        downLoadConfig(originalResponse, url)
        return originalResponse
    }

    /**
     * 拿到Response以后写入文件流中
     */
    private fun downLoadConfig(originalResponse: Response, url: String) {
        val contentType = originalResponse.body()?.contentType().toString()
        val contentLength = originalResponse.body()?.contentLength() ?: 0
        val streamType = !StreamType.values().filter { it.type == contentType }.isNullOrEmpty()
        val mediaType = !MediaType.values().filter { it.type == contentType }.isNullOrEmpty()
        var fileName = ""
        if (url.contains("?")) {
            fileName = url.substring(url.lastIndexOf("/"), url.indexOf("?"))
            if (!fileName.contains(".")) {
                fileName += "." + contentType.substring(
                    contentType.indexOf(
                        "/",
                        contentType.length
                    )
                )
            }
        } else {
            fileName = url.substring(url.lastIndexOf("/"), url.length)
        }
        val file = File("${getOutputDirectory()}/$fileName")
        val mDownLoadLength = when {
            mediaType -> {
                contentLength
            }
            streamType -> {
                contentLength.plus(file.length())
            }
            else -> {
                contentLength
            }
        }
        totalBytesRead = file.length()
        if (totalBytesRead != mDownLoadLength) {
            if (originalResponse.isSuccessful) {
                val fos = FileOutputStream(file, true).buffered()
                val bis = originalResponse.body()?.byteStream()!!.buffered()
                val buffer: ByteArray
                if (totalBytesRead != 0L) {
                    if (!streamType) {//streamType类型返回的是范围字节流，其它的contentType类型返回的是完整字节，因此除了stream之外，所有需要断点续传的都要做skip跳转
                        LogUtil.e("跳转字节预备中，在这里会卡顿线程")
                        bis.skip(totalBytesRead)
                        LogUtil.e("跳转字节" + bis.available())
                    }
                    buffer = ByteArray(mDownLoadLength.toInt() - totalBytesRead.toInt())
                    LogUtil.e("1准备写入${buffer.size}")
                } else {
                    buffer = ByteArray(mDownLoadLength.toInt())
                    LogUtil.e("2准备写入${buffer.size}")
                }
                var len: Int
                var ab: Boolean
                if (totalBytesRead == 0L) {
                    ab = bis.read(buffer).also { len = it } != -1  //true 证明有数据流
                } else {
                    ab = bis.read(buffer).also { len = it } != -1  //true 证明有数据流
                }
                do {
                    LogUtil.e("写入字节$len")
                    fos.write(buffer,0,len)
                    mLiveData.postValue(triple(url, mDownLoadLength, ab))
                    totalBytesRead += len
                    ab = bis.read(buffer).also { len = it } != -1
                } while (ab)
                originalResponse.body()?.close()
                bis.close()
                fos.close()
            }
        } else {
            mLiveData.postValue(
                Triple(
                    url,
                    if (format) "${totalBytesRead / 1024}Kb/${mDownLoadLength / 1024}Kb" else String.format(
                        "%.2f%s",
                        (totalBytesRead / mDownLoadLength).toDouble() * 100,
                        "%"
                    ),
                    true
                )
            )
        }
    }

    private fun triple(
        url: String,
        mDownLoadLength: Long,
        ab: Boolean
    ): Triple<String, String, Boolean> {
        return Triple(
            url,
            if (format) String.format(
                "%.2fKb/%.2fKB",
                (totalBytesRead.toFloat() / 1024),
                (mDownLoadLength.toFloat() / 1024)
            ) else String.format(
                "%.2f%s",
                (totalBytesRead.toFloat() / mDownLoadLength.toFloat()).toDouble() * 100,
                "%"
            ),
            !ab
        )
    }

    //图片将要保存的路径
    private fun getOutputDirectory(): File {
        val mediaDir = BaseApplication.context.externalMediaDirs.firstOrNull()?.let {
            File(
                it,
                BaseApplication.context.resources.getString(R.string.app_name)
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else BaseApplication.context.filesDir
    }

}
