package com.itcast.hmweather

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.itcast.hmweather.ui.home.HomeFragment
import com.itcast.hmweather.ui.me.MeFragment
import com.itcast.hmweather.ui.start.StarListFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    // 1. 定义缓存数组
    private val fragmentCache = SparseArray<Fragment>()

    override fun createFragment(position: Int): Fragment {
       // 1. 先尝试从缓存中获取
        var cachedFragment = fragmentCache.get(position)
       // 2. 判断并返回结果
        return if (cachedFragment != null) {
            cachedFragment
        } else {
          val newFragment = when (position) {
                0 -> HomeFragment()
                1 -> StarListFragment()
                2 -> MeFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
          // 把新创建的Fragment添加到缓存中
          fragmentCache.put(position, newFragment)
          // 返回结果
          newFragment
        }
    }

    /**
     * 返回页面的总数
     */
    override fun getItemCount(): Int {
        return 3
    }

}