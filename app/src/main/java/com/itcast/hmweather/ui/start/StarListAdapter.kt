package com.itcast.hmweather.ui.start

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.itcast.hmweather.R
import com.itcast.hmweather.bean.CityData

class StarListAdapter : BaseQuickAdapter<CityData, QuickViewHolder>()  {

    // 1. 创建新视图的重写方法，指定列表项的布局文件
    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 使用指定的item_star_city布局创建视图持有者
        return QuickViewHolder(R.layout.item_star_city, parent)
    }

    // 2. 绑定数据到视图的重写方法，用于更新列表项中的UI组件
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: CityData?
    ) {
        holder.run {
            // 获取城市名称并设置给对应的TextView
            var showName = item?.name
            setText(R.id.tv_city_name, showName)

            // 设置天气信息到对应TextView
            setText(R.id.tv_city_weather, item?.weather)

            // 设置温度信息到对应TextView
            setText(R.id.tv_city_temperature, item?.temperature)

            // 设置收藏图标为已收藏状态
            setBackgroundResource(R.id.iv_city_star, R.mipmap.icon_stared)
        }
    }


}