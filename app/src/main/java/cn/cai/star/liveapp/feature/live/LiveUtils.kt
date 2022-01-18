package cn.cai.star.liveapp.feature.live

import java.lang.StringBuilder

object LiveUtils {
    private const val PROTOCOL = "rtmp"
    private const val HOST = "49.232.112.170"
    private const val PART = "1935"

    @JvmStatic
    fun appendUrl(key: String): String {
        val builder = StringBuilder()
        builder.append(PROTOCOL).append("://")
            .append(HOST).append(":").append(PART)
            .append("/live/")
            .append(key)
        return builder.toString()
    }
}