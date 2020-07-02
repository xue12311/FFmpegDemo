package com.zjx.appcommonlibrary.base.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.zjx.appcommonlibrary.R
import com.zjx.appcommonlibrary.base.BaseViewModel
import com.zjx.appcommonlibrary.base.ext.getVmClazz
import com.zjx.appcommonlibrary.utils.utilcode.util.StringUtils
import com.zjx.appcommonlibrary.utils.utilcode.util.ToastUtils

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseDbFragment<AVM : BaseViewModel, DB : ViewDataBinding> : Fragment() {

    lateinit var mDatabind: DB

    lateinit var mViewModel: AVM

    /**
     * 当前Fragment绑定的视图布局
     */
    abstract fun layoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDatabind = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return mDatabind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
        mViewModel = getActivityViewModel()
        registorDefUIChange()
        createObserver()
    }

    /**
     * 创建viewModel
     */
//    private fun createViewModel(): VM {
//        return ViewModelProvider(this).get(getVmClazz(this) as Class<VM>)
//    }
    /**
     * 获得activity中的 ViewModel
     */
    private fun <AVM : BaseViewModel> getActivityViewModel(): AVM =
        ViewModelProvider(activity!!).get(getVmClazz(this) as Class<AVM>)


    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 创建观察者
     */
    abstract fun createObserver()

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
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        mViewModel.defUI.showDialog.observe(viewLifecycleOwner, Observer {
            showLoading()
        })
        mViewModel.defUI.dismissDialog.observe(viewLifecycleOwner, Observer {
            dismissLoading()
        })
        mViewModel.defUI.toastMessage.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        mViewModel.defUI.showMessage.observe(viewLifecycleOwner, Observer {
            showMessage(it)
        })
    }

    private var dialog: MaterialDialog? = null

    /**
     * 显示加载框
     */
    fun showLoading() = showLoading(null)

    fun showLoading(message: String? = StringUtils.getString(R.string.dialog_loading_message)) {
        if (dialog == null) {
            dialog = activity?.let {
                MaterialDialog(it)
                    .cancelable(true)
                    .cancelOnTouchOutside(false)
                    .cornerRadius(8f)
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
            activity?.let {
                MaterialDialog(it)
                    .cancelable(false)
                    .lifecycleOwner(this)
                    .show {
                        title(text = StringUtils.getString(R.string.dialog_title_text))
                        message(text = message)
                        positiveButton(text = StringUtils.getString(R.string.dialog_button_text_success))
                    }
            }
        }
    }

    /**
     * 显示温馨提示框
     * @param message String?
     */
    fun showNoTitleMessage(message: String?) {
        if (!StringUtils.isEmpty(message)) {
            activity?.let {
                MaterialDialog(it)
                    .cancelable(false)
                    .lifecycleOwner(this)
                    .show {
                        message(text = message)
                        positiveButton(text = StringUtils.getString(R.string.dialog_button_text_success))
                    }
            }
        }
    }
}