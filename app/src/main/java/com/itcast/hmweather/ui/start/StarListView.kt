package com.itcast.hmweather.ui.start

// 导入基础视图类，提供基础UI操作的定义
import com.itcast.hmweather.base.BaseView
// 导入城市数据模型类，用于表示城市信息
import com.itcast.hmweather.bean.CityData

/**
 * StartList 接口定义了启动页面中列表相关的视图行为。
 * 继承自 BaseView，适用于 MVP 架构中的 View 层。
 */
interface StarListView : BaseView {
    /**
     * 获取收藏城市列表成功时调用的方法。
     * @param starList 收藏的城市数据列表
     */
    fun getStarListSuccess(starList: List<CityData>)

    /**
     * 获取收藏城市列表失败时调用的方法。
     */
    fun getStarListFail()

    /**
     * 删除收藏城市成功时调用的方法。
     */
    fun deleteStarSuccess()

    /**
     * 删除收藏城市失败时调用的方法。
     */
    fun deleteStarFail()
}