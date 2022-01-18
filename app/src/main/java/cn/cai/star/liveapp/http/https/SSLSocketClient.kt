package cn.cai.star.liveapp.http.https

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * 处理HTTPS 自制证书的问题 (使用此类证书的话是不受信任的)
 *
 * 自己构建一个SSL来信任所有的证书
 */
@SuppressLint("TrustAllX509TrustManager")
object SSLSocketClient {
    val sslSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManager, SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    val x509TrustManager
        get() = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }

    //用来忽略host验证
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { _, _ -> true }

    private val trustManager: Array<TrustManager>
        get() = arrayOf(x509TrustManager)

}