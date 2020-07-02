package com.zjx.appcommonlibrary.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.zjx.appcommonlibrary.R
import com.zjx.appcommonlibrary.base.ext.getVmClazz
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.ToastUtils

abstract class BaseFragment : Fragment() {
    private var dialog: MaterialDialog? = null

    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    abstract fun initView(view: View)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
        initView(view)
    }


    /**
     * 获得activity中的 ViewModel
     */
    fun <AVM : BaseViewModel> getActivityViewModel(): AVM? {
        if (activity != null) {
            return ViewModelProvider(activity!!).get(getVmClazz(this) as Class<AVM>)
        } else {
            return null
        }
    }

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()


    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED) {
            lazyLoadData()
        }
    }


    /**
     * 显示加载框
     */
    fun showLoading() = showLoading(null)

    fun showLoading(message: String? = StringUtils.getString(R.string.dialog_loading_message)) {
        if (activity == null) {
            return
        }
        if (dialog == null) {
            dialog = this.let {
                MaterialDialog(activity!!)
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
        if (activity == null) {
            return
        }
        if (!StringUtils.isEmpty(message)) {
            MaterialDialog(activity!!)
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
        if (activity == null) {
            return
        }
        if (!StringUtils.isEmpty(message)) {
            MaterialDialog(activity!!)
                .cancelable(false)
                .lifecycleOwner(this)
                .show {
                    message(text = message)
                    positiveButton(text = StringUtils.getString(R.string.dialog_button_text_success))
                }
        }
    }

}