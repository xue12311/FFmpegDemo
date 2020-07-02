package com.zjx.appcommonlibrary.state

/**
 * 封装一个带一个参数的单例
 */
open class SingletonHolderOne<out T, in A>(private val creator: (A) -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T = instance ?: synchronized(this) {
        instance ?: creator(arg).apply {
            instance = this
        }
    }

    fun clearInstance() {
        instance = null
    }
}

/**
 * 封装一个带两个参数的单例
 */
open class SingletonHolderTwo<out T, in A, in B>(private val creator: (A, B) -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(arg1: A, arg2: B): T = instance ?: synchronized(this) {
        instance ?: creator(arg1, arg2).apply {
            instance = this
        }
    }

    fun clearInstance() {
        instance = null
    }
}
//  实例
//class SingletonHolder(context: Context) {
//    var packageName = context.applicationContext.packageName
//    val applicationContext by lazy {
//        context.applicationContext
//    }
//    init {
//        context.getString(R.string.app_name)
//    }
//    companion object : SingletonHolderOne<SingletonHolder, Context>(::SingletonHolder)
//}