package com.zjx.appcommonlibrary.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjx.appcommonlibrary.R
import com.zjx.appcommonlibrary.base.network.AppException
import com.zjx.appcommonlibrary.base.network.BaseResponse
import com.zjx.appcommonlibrary.base.network.ExceptionHandle
import com.zjx.appcommonlibrary.base.state.SingleLiveEvent
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import kotlinx.coroutines.*

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModel的基类
 */
open class BaseViewModel : ViewModel() {

    val defUI: UIChange by lazy { UIChange() }

    /**
     * 所有网络请求都在 viewModelScope 域中启动，当页面销毁时会自动
     * 调用ViewModel的  #onCleared 方法取消所有协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        //viewModelScope 默认是 Dispatchers.Main
        viewModelScope.launch {
            block()
        }
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    block()
                }
            }
        }
    }

    /**
     * 过滤请求结果，其他全抛异常 回调在Viewmodel
     * @param block 请求体
     * @param success 成功回调
     * @param errorCall 失败回调
     * @param isShowDialog 是否显示加载框
     * @param loadingMessage 加载框提示内容
     */
    fun <T> launchResultVM(
        block: suspend CoroutineScope.() -> BaseResponse<T>,
        success: (T) -> Unit,
        errorCall: (AppException) -> Unit = {},
        isShowDialog: Boolean = false,
        loadingMessage: String = StringUtils.getString(R.string.dialog_loading_message)
    ) {
        if (isShowDialog) {
            defUI.showDialog.postValue(loadingMessage)
        }
        launchUI {
            handleException(
                { withContext(Dispatchers.IO) { block() } },
                { res -> executeResponse(res) { success(it) } },
                { errorCall(it) },
                { defUI.dismissDialog.call() }
            )
        }
    }

    /**
     * 过滤请求结果，其他全抛异常 回调在Viewmodel
     * @param block 请求体
     * @param success 成功回调
     * @param errorCall 失败回调
     * @param isShowDialog 是否显示加载框
     * @param loadingMessage 加载框提示内容
     */
    fun <T> launchResultVM(
        block: suspend CoroutineScope.() -> BaseResponse<T>,
        success: (T) -> Unit,
        errorCall: (AppException) -> Unit = {}
    ) {
        launchUI {
            handleException(
                { withContext(Dispatchers.IO) { block() } },
                { res -> executeResponse(res) { success(it) } },
                { errorCall(it) },
                { defUI.dismissDialog.call() }
            )
        }
    }

    /**
     * 异常统一处理
     * @param block 请求体
     * @param success 成功回调
     * @param error 失败回调
     * @param complete 最终结果回调 （一般不用）
     */
    private suspend fun <T> handleException(
        block: suspend CoroutineScope.() -> BaseResponse<T>,
        success: suspend CoroutineScope.(BaseResponse<T>) -> Unit,
        error: suspend CoroutineScope.(AppException) -> Unit,
        complete: (suspend CoroutineScope.() -> Unit)?
    ) {
        coroutineScope {
            try {
                success(block())
            } catch (e: Throwable) {
                error(ExceptionHandle.handleException(e))
            } finally {
                if (complete != null) {
                    complete()
                }
            }
        }
    }

    /**
     * 请求结果过滤，判断请求服务器请求结果是否成功，不成功则会抛出异常
     *
     */
    private suspend fun <T> executeResponse(
        response: BaseResponse<T>,
        success: suspend CoroutineScope.(T) -> Unit
    ) {
        coroutineScope {
            if (response.isSucces()) {
                if (response.getBeanData() != null) {
                    success(response.getBeanData()!!)
                } else {
                    throw AppException(response.getErrorCode(), response.getErrorMessage())
                }
            } else {
                throw AppException(response.getErrorCode(), response.getErrorMessage())
            }
        }
    }

    /**
     * UI事件
     */
    inner class UIChange {
        //显示加载框
        val showDialog by lazy { SingleLiveEvent<String>() }

        //隐藏
        val dismissDialog by lazy { SingleLiveEvent<Void>() }

        //显示toast
        val toastMessage by lazy { SingleLiveEvent<String>() }

        //显示消息弹窗
        val showMessage by lazy { SingleLiveEvent<String>() }
    }
}