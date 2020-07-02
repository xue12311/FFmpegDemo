package com.zjx.appcommonlibrary.base

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.zjx.appcommonlibrary.R
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.ToastUtils

abstract class BaseActivity : AppCompatActivity() {
    private var dialog: MaterialDialog? = null

    abstract fun layoutId(): Int

    abstract fun initView()
    open fun initData() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        initView()
        initData()
    }

    /**
     * 显示加载框
     */
    fun showLoading() = showLoading(null)

    fun showLoading(message: String? = StringUtils.getString(R.string.dialog_loading_message)) {
        if (dialog == null) {
            dialog = this.let {
                MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .customView(R.layout.custom_progress_dialog_view)
                    .lifecycleOwner(this)
            }
            if (!StringUtils.isEmpty(message)) {
                dialog?.getCustomView()?.run {
                    this.findViewById<TextView>(R.id.loading_tips).text = message
                }
            }
        }
        dialog?.show()
    }

    /**
     * 关闭加载框
     */
    fun dismissLoading() {
        dialog?.dismiss()
    }

    /**
     * 显示吐司
     */
    fun showToast(message: String?) {
        if (!StringUtils.isEmpty(message)) {
            ToastUtils.showShort(message)
        }
    }

    /**
     * 显示温馨提示框
     * @param message String?
     */
    fun showMessage(message: String?) {
        if (!StringUtils.isEmpty(message)) {
            MaterialDialog(this)
                .cancelable(false)
                .lifecycleOwner(this)
                .show {
                    title(text = StringUtils.getString(R.string.dialog_title_text))
                    message(text = message)
                    positiveButton(text = StringUtils.getString(R.string.dialog_button_text_success))
                }
        }
    }

    /**
     * 显示温馨提示框
     * @param message String?
     */
    fun showNoTitleMessage(message: String?) {
        if (!StringUtils.isEmpty(message)) {
            MaterialDialog(this)
                .cancelable(false)
                .lifecycleOwner(this)
                .show {
                    message(text = message)
                    positiveButton(text = StringUtils.getString(R.string.dialog_button_text_success))
                }
        }
    }
}