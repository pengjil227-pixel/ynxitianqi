package com.itcast.hmweather.ui.start

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.BaseQuickAdapter.OnItemChildClickListener
import com.itcast.hmweather.util.GridSpacingItemDecoration
import com.itcast.hmweather.R
import com.itcast.hmweather.base.BaseBindingFragment
import com.itcast.hmweather.databinding.FragmentStarListBinding
import com.itcast.hmweather.bean.CityData
import com.itcast.hmweather.ui.detail.WeatherDetailActivity
import com.itcast.hmweather.util.WeatherUtils


class StarListFragment : BaseBindingFragment<FragmentStarListBinding>(), StarListView,
    OnItemChildClickListener<CityData>  {

    // Presenter 层的引用，用于与数据层交互
    private var mPresenter: StarListPresenter? = null

    /**
     * 页面恢复时调用的方法，用于刷新收藏城市列表。
     */
    override fun onResume() {
        super.onResume()

        showLoading()
        mPresenter?.getStarList()
    }

    /**
     * 初始化 Fragment 的视图绑定对象。
     * @param inflater 布局加载器
     * @param parent 父容器
     * @return 返回 FragmentStarListBinding 实例
     */
    override fun initBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragmentStarListBinding {
        return FragmentStarListBinding.inflate(inflater, parent, false)
    }

    /**
     * 初始化页面组件和布局设置。
     */
    override fun initView() {
        // 获取上下文并初始化 Presenter
        var context: Context? = getContext()
        context?.let {
            mPresenter = StarListPresenter(this, StarListInteractor())
        }

        // 设置 RecyclerView 的布局管理器为 GridLayoutManager（2列）
        binding.rvStarsList.layoutManager = GridLayoutManager(mActivity, 2)

        // 添加间距装饰器，设置每个 item 之间的间距
        mActivity?.let {
            binding.rvStarsList.addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    WeatherUtils.dip2px(it, 10f), false
                )
            )
        }

        // 创建适配器实例
        var adapter = StarListAdapter()

        // 为适配器添加子项点击监听器，关联到当前类
        adapter.addOnItemChildClickListener(R.id.iv_city_star, this)

        // 设置条目点击事件，跳转至天气详情页
        adapter.setOnItemClickListener { adapter, view, position ->
            var bean = adapter.getItem(position) as CityData
            var intent = Intent(mActivity, WeatherDetailActivity::class.java)
            intent.putExtra("city", bean.name)
            intent.putExtra("latitude", bean.latitude.toString())
            intent.putExtra("longitude", bean.longitude.toString())
            startActivity(intent)
        }

        // 将适配器绑定到 RecyclerView
        binding.rvStarsList.adapter = adapter
    }

    /**
     * 获取收藏城市列表成功时调用的方法。
     * @param starList 收藏的城市数据列表
     */
    override fun getStarListSuccess(starList: List<CityData>) {
        hideLoading()
        val adapter = binding.rvStarsList.adapter as StarListAdapter
        adapter.submitList(starList)
    }

    /**
     * 获取收藏城市列表失败时调用的方法。
     */
    override fun getStarListFail() {
        hideLoading()
    }

    /**
     * 删除收藏城市成功时调用的方法。
     */
    override fun deleteStarSuccess() {
        Handler(Looper.getMainLooper()).post {
            showLoading()
            mPresenter?.getStarList()
        }
    }

    /**
     * 删除收藏城市失败时调用的方法。
     */
    override fun deleteStarFail() {
        hideLoading()
    }

    /**
     * 子项点击事件处理方法。
     * @param adapter 当前使用的适配器
     * @param view 被点击的视图
     * @param position 点击的位置
     */
    override fun onItemClick(
        adapter: BaseQuickAdapter<CityData, *>,
        view: View,
        position: Int
    ) {
        var bean = adapter.getItem(position)
        when (view.id) {
            R.id.iv_city_star -> {
                // 点击收藏图标时删除对应城市的收藏记录
                mPresenter?.deleteStar(bean?.name.toString())
            }
        }
    }

}