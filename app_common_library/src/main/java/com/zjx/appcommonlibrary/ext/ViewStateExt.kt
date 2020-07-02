package com.zjx.appcommonlibrary.base.ext

import androidx.lifecycle.MutableLiveData
import com.zjx.appcommonlibrary.base.network.AppException
import com.zjx.appcommonlibrary.base.network.BaseResponse
import com.zjx.appcommonlibrary.base.network.ExceptionHandle
import com.zjx.appcommonlibrary.base.state.ViewState


/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<ViewState<T>>.paresResult(result: BaseResponse<T>) {
    value = if (result.isSucces()) {
        ViewState.onAppSuccess(result.getBeanData())
    } else {
        ViewState.onAppError(AppException(result.getErrorCode(), result.getErrorMessage()))
    }
}

/**
 * 异常转换异常处理
 */
fun <T> MutableLiveData<ViewState<T>>.paresException(e: Throwable) {
    this.value = ViewState.onAppError(ExceptionHandle.handleException(e))
}
