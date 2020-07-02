package com.zjx.appcommonlibrary.base.ext

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.appcommonlibrary.R
import com.zjx.appcommonlibrary.base.*
import com.zjx.appcommonlibrary.base.databinding.BaseDbFragment
import com.zjx.appcommonlibrary.base.network.AppException
import com.zjx.appcommonlibrary.base.network.BaseResponse
import com.zjx.appcommonlibrary.base.state.ViewState
import com.zjx.appcommonlibrary.base.databinding.BaseDbVmActivity
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmActivity
import com.zjx.appcommonlibrary.base.viewmodel.BaseVmFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.ParameterizedType

/**
 * 获取vm clazz
 */
@Suppress("UNCHECKED_CAST")
fun <VM> getVmClazz(obj: Any): VM {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param viewState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseDbVmActivity<*, *>.parseState(
    viewState: ViewState<T>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: (() -> Unit)? = null
) {
    when (viewState) {
        is ViewState.Loading -> {
            showLoading(viewState.loadingMessage)
            onLoading?.run { this }
        }
        is ViewState.Success -> {
            dismissLoading()
            onSuccess(viewState.data)
        }
        is ViewState.Error -> {
            dismissLoading()
            onError?.run { this(viewState.error) }
        }
    }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param viewState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseVmActivity<*>.parseState(
    viewState: ViewState<T>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: (() -> Unit)? = null
) {
    when (viewState) {
        is ViewState.Loading -> {
            showLoading(viewState.loadingMessage)
            onLoading?.run { this }
        }
        is ViewState.Success -> {
            dismissLoading()
            onSuccess(viewState.data)
        }
        is ViewState.Error -> {
            dismissLoading()
            onError?.run { this(viewState.error) }
        }
    }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param viewState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 * @param loadingMessage 加载框的提示内容，默认 请求网络中...
 *
 */
fun <T> BaseVmFragment<*>.parseState(
    viewState: ViewState<T>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: (() -> Unit)? = null
) {
    when (viewState) {
        is ViewState.Loading -> {
            showLoading(viewState.loadingMessage)
            onLoading?.run { this }
        }
        is ViewState.Success -> {
            dismissLoading()
            onSuccess(viewState.data)
        }
        is ViewState.Error -> {
            dismissLoading()
            onError?.run { this(viewState.error) }
        }
    }
}

/**
 * 显示页面状态，这里有个技巧，成功回调在第一个，其后两个带默认值的回调可省
 * @param viewState 接口返回值
 * @param onLoading 加载中
 * @param onSuccess 成功回调
 * @param onError 失败回调
 *
 */
fun <T> BaseDbFragment<*, *>.parseState(
    viewState: ViewState<T>,
    onSuccess: (T?) -> Unit,
    onError: ((AppException) -> Unit)? = null,
    onLoading: (() -> Unit)? = null
) {
    when (viewState) {
        is ViewState.Loading -> {
            showLoading(viewState.loadingMessage)
            onLoading?.run { this }
        }
        is ViewState.Success -> {
            dismissLoading()
            onSuccess(viewState.data)
        }
        is ViewState.Error -> {
            dismissLoading()
            onError?.run { this(viewState.error) }
        }
    }
}

/**
 *
 * net request
 * @param request request method
 * @param viewState request result
 * @param showLoading 配置是否显示等待框
 */
fun <T> BaseViewModel.launchRequest(
    request: suspend () -> BaseResponse<T>,
    viewState: MutableLiveData<ViewState<T>>,
    showLoading: Boolean = false,
    loadingMessage: String
) {
    viewModelScope.launch {
        runCatching {
            if (showLoading) {
                viewState.value = ViewState.onAppLoading(loadingMessage)
            }
            withContext(Dispatchers.IO) { request() }
        }.onSuccess {
            viewState.paresResult(it)
        }.onFailure {
            viewState.paresException(it)
        }
    }
}

fun <T> BaseViewModel.launchRequest(
    request: suspend () -> BaseResponse<T>,
    viewState: MutableLiveData<ViewState<T>>,
    showLoading: Boolean? = false
) {
    if (showLoading != null) {
        launchRequest(
            request,
            viewState,
            showLoading,
            StringUtils.getString(R.string.dialog_loading_message)
        )
    } else {
        launchRequest(
            request,
            viewState,
            false,
            StringUtils.getString(R.string.dialog_loading_message)
        )
    }
}




