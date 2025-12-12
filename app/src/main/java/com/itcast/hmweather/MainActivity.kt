package com.itcast.hmweather

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.itcast.hmweather.storage.TokenPreferences


class MainActivity : AppCompatActivity() {

    private var viewPager: ViewPager2? = null
    private var adapter: ViewPagerAdapter? = null
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)

        // 1. 添加点击事件
        addClickEvent()

        // 2. 加载Fragment
        loadFragment()

        // 3. 恢复状态
        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getInt("currentPosition", 0)
        }

        // 4. 恢复选中项
        viewPager?.currentItem = currentPosition
    }

    /**
     * 添加点击事件
     */
    private fun addClickEvent() {
        // 1. 获取控件
        val bottomNavigationView = findViewById<ViewGroup>(R.id.llBottomNavigation)
        val homeContainer = bottomNavigationView.findViewById<ViewGroup>(R.id.nav_home)
        val lifeContainer = bottomNavigationView.findViewById<ViewGroup>(R.id.nav_life)
        val profileContainer = bottomNavigationView.findViewById<ViewGroup>(R.id.nav_profile)

        // 2. 添加点击事件
        homeContainer.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                   // a. 实现页面切换(通过viewPager2)
                    viewPager?.setCurrentItem(0, false)
                   // b. 更新底部的导航栏选中
                   setCurrentTab(0)
                }
            }
        )

        lifeContainer.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // a. 实现页面切换(通过viewPager2)
                    viewPager?.setCurrentItem(1, false)
                    // b. 更新底部的导航栏选中
                    setCurrentTab(1)
                }
            }
        )

        profileContainer.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // a. 实现页面切换(通过viewPager2)
                    viewPager?.setCurrentItem(2, false)
                    // b. 更新底部的导航栏选中
                    setCurrentTab(2)
                }
            }
        )

    }

    /**
     * 设置当前选中的tab
     */
    fun setCurrentTab(position: Int){
        val bottomNavigationView = findViewById<ViewGroup>(R.id.llBottomNavigation)

        val tvHome = bottomNavigationView.findViewById<TextView>(R.id.tv_home)
        var imageHome = bottomNavigationView.findViewById<ImageView>(R.id.img_home)

        val tvLife = bottomNavigationView.findViewById<TextView>(R.id.tv_life)
        var imageLife = bottomNavigationView.findViewById<ImageView>(R.id.img_life)

        val tvProfile = bottomNavigationView.findViewById<TextView>(R.id.tv_profile)
        var imageProfile = bottomNavigationView.findViewById<ImageView>(R.id.img_profile)

        when(position){
            0 -> {
                tvHome.setTextColor(resources.getColor(R.color.weather_primary, null))
                imageHome.setColorFilter(resources.getColor(R.color.weather_primary, null))

                tvLife.setTextColor(resources.getColor(R.color.default_gray, null))
                imageLife.setColorFilter(resources.getColor(R.color.default_gray, null))

                tvProfile.setTextColor(resources.getColor(R.color.default_gray, null))
                imageProfile.setColorFilter(resources.getColor(R.color.default_gray, null))
            }
            1 -> {
                tvHome.setTextColor(resources.getColor(R.color.default_gray, null))
                imageHome.setColorFilter(resources.getColor(R.color.default_gray, null))

                tvLife.setTextColor(resources.getColor(R.color.weather_primary, null))
                imageLife.setColorFilter(resources.getColor(R.color.weather_primary, null))

                tvProfile.setTextColor(resources.getColor(R.color.default_gray, null))
                imageProfile.setColorFilter(resources.getColor(R.color.default_gray, null))
            }
            2 -> {
                tvHome.setTextColor(resources.getColor(R.color.default_gray, null))
                imageHome.setColorFilter(resources.getColor(R.color.default_gray, null))

                tvLife.setTextColor(resources.getColor(R.color.default_gray, null))
                imageLife.setColorFilter(resources.getColor(R.color.default_gray, null))

                tvProfile.setTextColor(resources.getColor(R.color.weather_primary, null))
                imageProfile.setColorFilter(resources.getColor(R.color.weather_primary, null))
            }
        }
    }

    /**
     * 加载Fragment
     */
    private fun loadFragment() {
        // 1. 获取ViewPager2
        if(viewPager == null){
          viewPager = findViewById(R.id.vpMain)

          // 禁止页面滑动
          viewPager?.isUserInputEnabled = false

          // 注册页面切换监听
          viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
              override fun onPageSelected(position: Int) {
                  super.onPageSelected(position)
                  setCurrentTab(position)
                  currentPosition = position
              }
          })
        }

        // 2. 创建适配器
        if(adapter == null){
            adapter = ViewPagerAdapter(this)
        }

        // 3. 设置适配器
        viewPager?.adapter = adapter
        viewPager?.offscreenPageLimit = 2 // 缓存3个页面(0, 1, 2)
    }
}