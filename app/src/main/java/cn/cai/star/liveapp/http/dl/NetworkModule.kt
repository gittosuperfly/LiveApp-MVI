package cn.cai.star.liveapp.http.dl

import cn.cai.star.liveapp.http.https.SSLSocketClient
import cn.cai.star.liveapp.http.logIntercepter.LogInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    companion object {
        private const val DEFAULT_TIME_OUT = 15L
    }

    @Provides
    @Singleton
    fun provideOkHttpClick(): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            .sslSocketFactory(SSLSocketClient.sslSocketFactory, SSLSocketClient.x509TrustManager)
            .hostnameVerifier(SSLSocketClient.hostnameVerifier)
            .addInterceptor(LogInterceptor.default)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(httpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    }
}