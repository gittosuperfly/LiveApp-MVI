package cn.cai.star.liveapp.http.dl

import cn.cai.star.liveapp.AppConstant
import cn.cai.star.liveapp.http.LiveApi
import cn.cai.star.liveapp.http.ServerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideServerApi(builder: Retrofit.Builder): ServerApi {
        return builder
            .baseUrl(AppConstant.HOST_SERVER)
            .build()
            .create(ServerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLiveApi(builder: Retrofit.Builder): LiveApi {
        return builder
            .baseUrl(AppConstant.HOST_LIVE)
            .build()
            .create(LiveApi::class.java)
    }
}