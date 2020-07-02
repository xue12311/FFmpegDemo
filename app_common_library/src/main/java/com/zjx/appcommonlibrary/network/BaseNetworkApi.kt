package com.zjx.appcommonlibrary.base.network

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.zjx.appcommonlibrary.network.TimeoutConfig
import com.zjx.appcommonlibrary.utils.utilcode.util.Utils
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　: 网络请求构建器基类
 */
abstract class BaseNetworkApi {

    fun <T> getApi(serverceClass: Class<T>, baseUrl: String): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()
            .create(serverceClass)
    }

    abstract fun setHttpClientBuilder(builder: OkHttpClient.Builder)

    //Cookies自动持久化
    private val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(Utils.getApp()))
    }


    /**
     * 配置http
     */
    private val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            //读取超时时间
            builder.readTimeout(TimeoutConfig.DEFAULT_CONFIG.readTimeOut, TimeUnit.MILLISECONDS)
                //写入超时时间
                .writeTimeout(TimeoutConfig.DEFAULT_CONFIG.writeTimeOut, TimeUnit.MILLISECONDS)
                //连接超时时间
                .connectTimeout(TimeoutConfig.DEFAULT_CONFIG.connectTimeout, TimeUnit.MILLISECONDS)
                //添加Cookies自动持久化
                .cookieJar(cookieJar)
            setHttpClientBuilder(builder)
            return builder.build()
        }


}



