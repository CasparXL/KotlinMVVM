package com.caspar.xl.network.interceptor

import com.caspar.base.helper.LogUtil.i
import com.caspar.base.helper.LogUtil.json
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class HttpLoggingInterceptor : Interceptor {
    enum class Level {
        /**
         * No logs.
         */
        NONE,

        /**
         * Logs request and response lines.
         *
         *
         * Example:
         * <pre>`POST /greeting http/1.1 (3-byte body)
         *
         * 200 OK (22ms, 6-byte body)
        `</pre> *
         */
        BASIC,

        /**
         * Logs request and response lines and their respective headers.
         *
         *
         * Example:
         * <pre>`POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * END POST
         *
         * 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * END HTTP
        `</pre> *
         */
        HEADERS,

        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         *
         * Example:
         * <pre>`POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * END POST
         *
         * 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * END HTTP
        `</pre> *
         */
        BODY
    }

    @Volatile
    var level =
        Level.NONE
        private set

    /**
     * Change the level at which this interceptor logs.
     */
    fun setLevel(level: Level?): HttpLoggingInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = level
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody =
            level == Level.BODY
        val logHeaders =
            logBody || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        var requestStartMessage = (" "
                + request.method()
                + ' ' + request.url()
                + if (connection != null) " " + connection.protocol() else "")
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody?.contentLength() + "-byte body)"
        }
        i(requestStartMessage)
        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                requestBody?.apply {
                    i("Content-Type: " + this.contentType())
                    if (this.contentLength() != -1L)
                        i("Content-Length: " + this.contentLength())
                }
            }
            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(
                        name,
                        ignoreCase = true
                    ) && !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    i(name + ": " + headers.value(i))
                }
                i++
            }
            if (!logBody || !hasRequestBody) {
                i("END " + request.method())
            } else if (bodyEncoded(request.headers())) {
                i("END " + request.method() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)
                var charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                i("")
                if (isPlaintext(buffer)) {
                    i(buffer.readString(charset))
                    i(
                        "END " + request.method()
                                + " (" + requestBody.contentLength() + "-byte body)"
                    )
                } else {
                    i(
                        "END " + request.method() + " (binary "
                                + requestBody.contentLength() + "-byte body omitted)"
                    )
                }
            }
        }
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            i("HTTP FAILED: $e")
            throw e
        }
        val tookMs =
            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength =
            responseBody?.contentLength() ?: 0
        val bodySize =
            if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        i(
            " "
                    + response.code()
                    + (if (response.message()
                    .isEmpty()
            ) "" else ' '.toString() + response.message())
                    + ' ' + response.request().url()
                    + " (" + tookMs + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')'
        )
        if (logHeaders) {
            val headers = response.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                i(headers.name(i) + ": " + headers.value(i))
                i++
            }
            if (!logBody || !HttpHeaders.hasBody(response)) {
                i(" END HTTP")
            } else if (bodyEncoded(response.headers())) {
                i(" END HTTP (encoded body omitted)")
            } else {
                val source = responseBody!!.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer
                var charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }
                if (!isPlaintext(buffer)) {
                    i("")
                    i("END HTTP (binary " + buffer.size() + "-byte body omitted)")
                    return response
                }
                if (contentLength != 0L) {
                    i("Response Content:\u21E3")
                    json(buffer.clone().readString(charset))
                }
                i("END HTTP (" + buffer.size() + "-byte body)")
            }
        }
        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        private fun isPlaintext(buffer: Buffer): Boolean {
            return try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(
                            codePoint
                        )
                    ) {
                        return false
                    }
                }
                true
            } catch (e: EOFException) {
                false // Truncated UTF-8 sequence.
            }
        }
    }
}