package cn.cai.star.liveapp.http.logIntercepter


import okhttp3.Request
import java.io.IOException
import kotlin.jvm.Throws

interface BufferListener {
    @Throws(IOException::class)
    fun getJsonResponse(request: Request?): String?
}