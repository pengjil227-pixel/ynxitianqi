package com.itcast.hmweather.ui.home

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.itcast.hmweather.R
import com.itcast.hmweather.bean.ChangeCityBean

class CityListAdapter: BaseQuickAdapter<ChangeCityBean, QuickViewHolder>() {
    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: ChangeCityBean?
    ) {
       holder.run {
           // 设置城市的名称
           setText(R.id.tv_city_name, item?.cityName)

           // 判断是否是当前定位城市
           if (item?.isCurrentLocation == true) {
               setGone(R.id.iv_location, false)
               // 设置城市的颜色(深色)
               setTextColor(R.id.tv_city_name, context.resources.getColor(R.color.colorAccent, null))

           }else{
               setGone(R.id.iv_location, true)
               // 设置城市的颜色(深色)
               setTextColor(R.id.tv_city_name, context.resources.getColor(R.color.white, null))
           }

       }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
       return QuickViewHolder(R.layout.item_city_list, parent)
    }
}