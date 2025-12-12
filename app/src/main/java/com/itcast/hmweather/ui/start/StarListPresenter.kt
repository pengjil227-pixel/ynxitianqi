package com.itcast.hmweather.ui.start

import com.itcast.hmweather.base.BasePresenter
import com.itcast.hmweather.util.FavoriteManager

class StarListPresenter(
    var mView: StarListView,
    var mInteractor: StarListInteractor) :
    BasePresenter<StarListView, StarListInteractor>(mView, mInteractor)  {


    /**
     * 获取收藏列表
     */
    fun getStarList() {
        // 获取与视图关联的上下文，用于后续操作
        var context = mView.getContext()
        
        // 检查上下文是否为空，如果为空则调用视图的失败回调并退出方法
        if (context == null) {
            mView.getStarListFail()
            return
        }
        
        // 从FavoriteManager获取当前用户的收藏列表，并转换为不可变列表
        val favorites = FavoriteManager.getCurrentUserFavorites().toList()
        
        // 将获取到的收藏列表通过视图的成功回调方法传递回去
        mView.getStarListSuccess(favorites)
    }

    /**
     * 删除收藏
     */
    fun deleteStar(cityName: String) {
        val context = mView.getContext()
        if (context == null) {
            mView.deleteStarFail()
            return
        }
        FavoriteManager.removeFavoriteCity(cityName)
        mView.deleteStarSuccess()
    }

}