package com.itcast.hmweather.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int, // 每行显示的item数量
    private val spacing: Int, // item之间的间距
    private val includeEdge: Boolean // 是否在边缘添加边距
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, // 用于设置当前item的边距
        view: View, // 当前item的视图
        parent: RecyclerView, // RecyclerView本身
        state: RecyclerView.State // RecyclerView的状态
    ) {
        val position = parent.getChildAdapterPosition(view) // 获取当前item的位置
        val column = position % spanCount // 计算当前item位于哪一列

        if (includeEdge) {
            // 如果包含边缘边距，则计算左右边距
            outRect.left = spacing - column * spacing / spanCount // 左边距
            outRect.right = (column + 1) * spacing / spanCount // 右边距
            if (position < spanCount) outRect.top = spacing // 如果是第一行，则添加上边距
            outRect.bottom = spacing // 所有item都添加下边距
        } else {
            // 如果不包含边缘边距，则计算左右边距
            outRect.left = column * spacing / spanCount // 左边距
            outRect.right = spacing - (column + 1) * spacing / spanCount // 右边距
            if (position >= spanCount) outRect.top = spacing // 如果不是第一行，则添加上边距
        }
    }
}