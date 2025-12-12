package com.itcast.hmweather.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.annotation.IdRes;
import com.itcast.hmweather.LoadingDialog

/**
 * fragment的基类，主要用来存放handler，loading等
 */
abstract class BaseFragment : Fragment(), BaseView {
    var mActivity: Activity? = null
    protected var mView: View? = null

    private val NO_ID = -1
    private var isForeground: Boolean = false //当前fragment是否前台（该值改变在上页面onpause之前，会有一丢丢问题）
    var mIsInit: Boolean = false //是否已经初始化
    private var currFragmentVisible = false //该值精准（勿动，与mIsForeground赋值时机不同）

    var mainHandler: Handler? = Handler(Looper.getMainLooper())
    private var loadingDialog: LoadingDialog? = null

    fun setIsForeground(isForeground: Boolean) {
        this.isForeground = isForeground
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mView == null) {
            beforeInitView()
            if (!useLazyLoad()) {
                initView()
                mIsInit = true
            }
        }

        val parent = mView?.parent as ViewGroup
        parent.removeView(mView)

        return mView
    }

    override fun getContext(): Context? {
        return mActivity
    }

    override fun onResume() {
        super.onResume()
        currFragmentVisible = true
        if (useLazyLoad() && !mIsInit) {
            initView()
            mIsInit = true
        }
    }

    override fun onDetach() {
        super.onDetach()
        mView = null
        mActivity = null
    }

    override fun onStop() {
        super.onStop()
        currFragmentVisible = false
    }

    val isAutonomousLayout: Boolean
        /**
         * 默认不使用父布局
         */
        get() = true

    val isCancelable: Boolean
        /**
         * loading是否允许返回键取消
         *
         * @return
         */
        get() = false

    /**
     * 是否使用懒加载--默认不使用
     */
    fun useLazyLoad(): Boolean {
        return false
    }

    /**
     * 用于解决懒加载时部分子View出现高度为0的bug 一般不使用
     */
    fun beforeInitView() {
    }

    /**
     * 界面是否已经加载成功
     */
    fun hasInitView(): Boolean {
        return mIsInit
    }

    abstract fun initView()

    override fun showLoading() {
        mActivity?.let {
            if (mActivity?.isFinishing == false && mActivity?.isDestroyed == false) {
                if (loadingDialog == null) {
                    loadingDialog = LoadingDialog.Builder(mActivity!!)
                        .create(isCancelable)
                }
                loadingDialog?.show()
            }
        }
    }

    override fun hideLoading() {
        if (loadingDialog != null && loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mainHandler != null) {
            mainHandler?.removeCallbacksAndMessages(null)
            mainHandler = null
        }
    }


    fun <T : View?> findViewById(@IdRes id: Int): T? {
        if (id == NO_ID) {
            return null
        }
        return mView?.findViewById(id)
    }


}