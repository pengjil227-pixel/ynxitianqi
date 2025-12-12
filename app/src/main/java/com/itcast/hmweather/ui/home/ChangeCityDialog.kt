package com.itcast.hmweather.ui.home

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.WindowManager.LayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.itcast.hmweather.R
import com.itcast.hmweather.bean.ChangeCityBean
import com.itcast.hmweather.databinding.LayoutDialogChangeCityBinding
import com.itcast.hmweather.event.ChangeCityClickEvent
import com.itcast.hmweather.util.GridSpacingItemDecoration
import com.itcast.hmweather.util.WeatherUtils
import org.greenrobot.eventbus.EventBus

class ChangeCityDialog(
    private val mContext: Context,
    theme: Int
): BottomSheetDialog(mContext, theme) {

    private lateinit var binding: LayoutDialogChangeCityBinding

    init {
        initContentView()
    }

    /**
     * 初始化布局
     */
    fun initContentView() {

        // 初始化binding
        binding = LayoutDialogChangeCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivCloseDialog.setOnClickListener {
            dismiss()
        }

        if (window != null) {
            // 添加透明状态栏标志，使内容可以延伸到状态栏区域
            window?.addFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 设置对话框窗口的布局参数，宽度为匹配父布局，高度为包裹内容
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 查找BottomSheetDialog的design_bottom_sheet视图，这是Material Design中的一个标准ID
        val view = delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        // 设置视图的背景颜色为透明，使对话框底部区域变为透明
        view?.setBackgroundColor(context.resources.getColor(R.color.transparent, null))


        // 获取binding.root的布局参数
        val layoutParams = binding.root.layoutParams
        // 设置宽度为MATCH_PARENT，表示匹配父容器的宽度
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置高度为WRAP_CONTENT，表示根据内容自动调整高度
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // 将修改后的布局参数重新赋值给binding.root
        binding.root.layoutParams = layoutParams

        // 使用post方法确保视图已经完成布局后再执行以下操作
        binding.root.post {
            // 查找design_bottom_sheet视图，该视图为BottomSheetDialog的标准ID
            val tempView = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            // 如果tempView不为空，则继续配置BottomSheetBehavior
            if (tempView != null) {
                // 获取与tempView关联的BottomSheetBehavior对象
                val behavior = BottomSheetBehavior.from(tempView)
                // 禁用拖动手势，使用户无法通过手势关闭或移动BottomSheet
                behavior.isDraggable = false
            }
        }

        // 获取binding.rvCityList的布局参数
        binding.rvCityList.layoutManager = GridLayoutManager(mContext, 3)
        // 设置网格间距
        binding.rvCityList.addItemDecoration(
            GridSpacingItemDecoration(3,
                WeatherUtils.dip2px(mContext, 16f), // 将16dp转换为像素值作为间距,
                false)
        )

        val cityListAdapter = CityListAdapter()

        // 监听适配器的点击事件
        cityListAdapter.setOnItemClickListener { _, _, position ->
            if(position==0){
                return@setOnItemClickListener
            }
            val cityBean = cityListAdapter.getItem( position) as ChangeCityBean
            // 使用EventBus发送ChangeCityClickEvent事件, 并将cityBean作为参数传递给ChangeCityClickEvent
            EventBus.getDefault().post(ChangeCityClickEvent(cityBean))
            // 关闭当前对话框
            dismiss()
        }

        binding.rvCityList.adapter = cityListAdapter
    }

    /**
     * 刷新城市列表
     */
    fun refreshCityList(cityList: List<ChangeCityBean>) {
        if(cityList.isNotEmpty()){
           val adapter = binding.rvCityList.adapter as CityListAdapter
            adapter.submitList(cityList)
        }
    }
}
