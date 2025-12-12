package com.itcast.hmweather.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.itcast.hmweather.LoadingDialog

/**
 * activity的基类
 * 主要用来存放handler，loading等
 */
/**
 * [BaseActivity] 是一个开放类，继承自 [AppCompatActivity] 并实现了 [BaseView] 接口。
 * 它提供了基本的活动功能和通用的界面加载状态管理。
 */
open class BaseActivity : AppCompatActivity(), BaseView {
    // 当前活动的引用，用于在整个类中访问活动上下文。
    private var mActivity: AppCompatActivity? = null
    // 主线程的 Handler，用于在主线程中执行操作。
    private var mainHandler: Handler? = Handler(Looper.getMainLooper())
    // 加载对话框的实例，用于展示和隐藏加载中的状态。
    private var loadingDialog: LoadingDialog? = null

    /**
     * 在活动创建时调用。初始化活动并设置 [mActivity] 变量。
     *
     * @param savedInstanceState 保存的实例状态，如果活动被重新创建（如屏幕旋转），则提供之前保存的状态。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
    }

    /**
     * 在活动的上下文被附加到新的基上下文时调用。
     *
     * @param newBase 新的基上下文。
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    /**
     * 提供当前活动的上下文。
     *
     * @return 当前活动的上下文。
     */
    override fun getContext(): Context? {
        return mActivity
    }

    /**
     * 在活动销毁时调用。释放 [mActivity] 和 [mainHandler] 的引用，避免内存泄漏。
     */
    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
        if (mainHandler != null) {
            mainHandler?.removeCallbacksAndMessages(null)
            mainHandler = null
        }
    }

    /**
     * 显示加载中的对话框。确保对话框仅在活动未完成且未被销毁时显示。
     */
    override fun showLoading() {
        mActivity?.let {
            if (mActivity?.isFinishing == false && mActivity?.isDestroyed == false) {
                if (loadingDialog == null) {
                    loadingDialog = LoadingDialog.Builder(mActivity!!)
                        .create(false)
                }
                loadingDialog?.show()
            }
        }
    }

    /**
     * 隐藏加载中的对话框。如果对话框是显示状态，则将其隐藏。
     */
    override fun hideLoading() {
        if (loadingDialog != null && loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

}

