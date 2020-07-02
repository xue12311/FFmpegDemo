package com.zjx.appcommonlibrary

import android.content.Context
import androidx.multidex.MultiDex
import com.zjx.appcommonlibrary.base.BaseApp
import com.zjx.appcommonlibrary.utils.utilcode.util.*
import kotlin.properties.Delegates

//自定义Application
open class App : BaseApp() {
    companion object {
        private var instance: App by Delegates.notNull()
        fun instance() = instance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this;
        Utils.init(instance)
        SPStaticUtils.setDefaultSPUtils(SPUtils.getInstance(AppUtils.getAppPackageName()))
        LogUtils.getConfig()
            //设置 log 总开关
            .setLogSwitch(BuildConfig.DEBUG)
            //设置 log 控制台开关
            .setConsoleSwitch(BuildConfig.DEBUG)
            //设置 log 全局 tag
            .setGlobalTag("zjx")
    }
}