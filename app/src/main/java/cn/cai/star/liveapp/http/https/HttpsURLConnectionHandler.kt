package cn.cai.star.liveapp.http.https

import android.util.Log
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

object HttpsURLConnectionHandler {

    private const val TAG = "HttpsURLConnectionHandler"

    @JvmStatic
    fun handleSSLHandshake() {
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(SSLSocketClient.sslSocketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _: String?, _: SSLSession? -> true }
        } catch (ignored: Exception) {
            Log.w(TAG, "$ignored")
        }
    }
}