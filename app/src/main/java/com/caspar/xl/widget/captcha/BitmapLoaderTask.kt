package com.caspar.xl.widget.captcha

import android.os.AsyncTask
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * 网络加载图片的Task类
 * Created by cdc4512 on 2018/5/7.
 */
internal class BitmapLoaderTask(private val callback: Callback) : AsyncTask<String?, Int?, Bitmap?>() {
    internal interface Callback {
        fun result(bitmap: Bitmap?)
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)
        callback.result(bitmap)
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): Bitmap? {
        var result: Bitmap? = null
        var url: URL? = null
        var conn: HttpURLConnection? = null
        var `is`: InputStream? = null
        try {
            url = URL(params[0])
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val code = conn!!.responseCode
            if (code == 200) {
                `is` = conn.inputStream
                result = BitmapFactory.decodeStream(`is`)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                conn?.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}