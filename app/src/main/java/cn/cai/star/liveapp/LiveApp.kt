package cn.cai.star.liveapp

import android.app.Activity
import android.app.Application
import cai.lib.async.Async
import cai.lib.design.Design
import cai.lib.kvstore.KVStore
import cai.lib.toast.ToastUtils
import cn.cai.star.liveapp.app.App
import cn.cai.star.liveapp.http.https.HttpsURLConnectionHandler
import cn.cai.star.liveapp.utils.AppUtils
import cn.cai.star.liveapp.utils.density.DensityHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiveApp : Application(), DensityHelper.IDensityProvider {

    override fun onCreate() {
        super.onCreate()
        App.setAppContext(this)
        App.setLogCallback(this)
        KVStore.init(this)
        Async.init(this)
        ToastUtils.init(this)
        Design.init(this)
        DensityHelper.init(this, true, this)
        HttpsURLConnectionHandler.handleSSLHandshake()

        AppUtils.init(this)
    }

    override fun getCurrentActivity(): Activity {
        return App.getCurrentActivity()!!
    }
}