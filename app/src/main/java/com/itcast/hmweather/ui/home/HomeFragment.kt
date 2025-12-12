package com.itcast.hmweather.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.itcast.hmweather.WeatherGlobal
import com.itcast.hmweather.base.BaseBindingFragment
import com.itcast.hmweather.databinding.FragmentHomeBinding
import com.itcast.hmweather.util.WeatherUtils
import com.qweather.sdk.response.weather.WeatherHourlyResponse
import com.qweather.sdk.response.weather.WeatherNowResponse
import androidx.recyclerview.widget.RecyclerView
import com.itcast.hmweather.R
import com.itcast.hmweather.bean.ChangeCityBean
import com.itcast.hmweather.event.ChangeCityClickEvent
import com.itcast.hmweather.ui.detail.WeatherDetailActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode.MAIN


class HomeFragment : BaseBindingFragment<FragmentHomeBinding>(), HomeView {

    // 1. 初始化homePresenter
    private var homePresenter: HomePresenter? = null

    // 2. 实现周期性时间更新
    // 创建与主线程关联的Handler实例，用于执行定时任务
    private val handler = Handler(Looper.getMainLooper())

    // 定义Runnable对象，用于周期性更新当前时间
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            showCurrentTime()  // 调用方法更新界面显示的时间
            handler.postDelayed(this, 1000) // 每隔1000毫秒（即1秒）再次执行此任务
        }
    }

    // 3. 定义Dialog
    private var changeCityDialog: ChangeCityDialog? = null
    private fun showChangeDialog(){
        mActivity?.let {
            changeCityDialog = ChangeCityDialog(it, R.style.BottomSheetDialog_Immersion)
            changeCityDialog?.show()
        }
    }

    // ---------------------------
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    // 订阅ChangeCityClickEvent事件
    @Subscribe(threadMode = MAIN)
    fun changeCitySuccess(event: ChangeCityClickEvent){
        event.changeCityBean.let {
            refreshLocationWeatherData(
                it.cityName,
                it.latitude.toString(),
                it.longitude.toString()
            )
        }
    }
    // ---------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (binding.topBar.tvCityName.text.isEmpty()) {
            refreshLocationWeatherData(
                WeatherGlobal.getCityName(),
                WeatherGlobal.getLatitude(),
                WeatherGlobal.getLongitude()
            )
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, parent, false)
    }

    override fun initView() {
        homePresenter = HomePresenter(this, HomeInteractor())

        // 1. 设置点击事件
        setupClickListener()
        
        // 2. 设置未来24小时天气卡片布局和适配器
        binding.hourlyForecast.rvHourlyForecast.apply {
            // 2.1 设置布局管理器为水平方向
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            // 2.2 设置适配器
            adapter = HourTempAdapter()

            // 2.3 设置每个单项的间隔
            if(binding.hourlyForecast.rvHourlyForecast.itemDecorationCount == 0){
                binding.hourlyForecast.rvHourlyForecast.addItemDecoration(
                    object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)

                            // 获取当前项的索引
                            val position = parent.getChildAdapterPosition(view)
                            if(position == 0){
                              // 如果是第一个, 则设置间隔为0
                                outRect.left = 0
                            }else{
                                outRect.left = WeatherUtils.dip2px(
                                    context,
                                    10f
                                )
                            }

                        }
                    }
                )
            }
        }
    }

    private fun setupClickListener() {
        // 1. 点我刷新
        binding.currentWeatherCard.btnRealTime.setOnClickListener {
            showLoading()
            homePresenter?.requestMainWeatherData()
        }

        // 2. 监听顶部城市点击
        binding.topBar.tvCityName.setOnClickListener {
            homePresenter?.requestTopCityListData()
            // 展示城市弹层
            showChangeDialog()
        }

        // 3. 监听当前天气卡片的点击
        binding.currentWeatherCard.root.setOnClickListener {
            var intent = Intent(context, WeatherDetailActivity::class.java)
            // 传递城市, 经纬度
            intent.putExtra("city", homePresenter?.getCurrShowCityName())
            intent.putExtra("latitude", homePresenter?.getCurrShowLatitude())
            intent.putExtra("longitude", homePresenter?.getCurrShowLongitude())

            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getMainWeatherDataSuccess(response: WeatherNowResponse) {
        hideLoading()

        stopUpdateTime()
        showCurrentTime()
        startUpdateTime()

        // 显示当前卡片
        binding.currentWeatherCard.llCurrentWeatherContainer.visibility = View.VISIBLE
        binding.currentWeatherCard.tvRefreshTime.text =
            WeatherUtils.formatTime(response.updateTime) + "更新"
        binding.currentWeatherCard.tvTemperature.text = response.now.temp
        binding.currentWeatherCard.tvDegreeSymbol.visibility = View.VISIBLE

        // 根据天气的状况的图标代码, 绘制对应的天气图标
        context?.let {
            var iconCode = "_" + response.now.icon.replace("-", "_")
            if (WeatherUtils.isDrawableInRClass(it, iconCode)) {
                val drawable = WeatherUtils.getDrawableByName(it, iconCode)
                drawable?.let {
                    binding.currentWeatherCard.ivWeatherIcon.setImageDrawable(drawable)
                }
                binding.currentWeatherCard.ivWeatherIcon.visibility = View.VISIBLE
            } else {
                binding.currentWeatherCard.ivWeatherIcon.visibility = View.GONE
            }
        }

        //数据拼接，如 晴 | 北风3级 | 当前体感20度
        var constructText = ""
        if (response.now.text.isNotEmpty()) {
            constructText += response.now.text
        }
        if (response.now.windDir.isNotEmpty() && response.now.windScale.isNotEmpty()) {
            constructText += " | " + response.now.windDir + response.now.windScale + "级"
        }
        if (response.now.feelsLike.isNotEmpty()) {
            constructText += " | 当前体感" + response.now.feelsLike + "°"
        }
        if (constructText.isNotEmpty()) {
            binding.currentWeatherCard.tvWeatherCondition.text = constructText
        } else {
            binding.currentWeatherCard.tvWeatherCondition.text = "暂无数据"
        }

        binding.currentWeatherCard.tvHumidity.text = response.now.humidity + "%"
        binding.currentWeatherCard.tvWindSpeed.text = response.now.windSpeed
        binding.currentWeatherCard.tvPressure.text = response.now.pressure


    }

    override fun getMainWeatherDataFailed(msg: String) {
        hideLoading()
        binding.currentWeatherCard.llCurrentWeatherContainer.visibility = View.GONE
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun getHourlyWeatherDataSuccess(response: WeatherHourlyResponse) {
      binding.hourlyForecast.root.visibility = View.VISIBLE
      // 提交数据
      response.hourly?.let { hourlyList ->
          (binding.hourlyForecast.rvHourlyForecast.adapter as HourTempAdapter).submitList(hourlyList)
      }
    }

    override fun getHourlyWeatherDataFailure(errorResponse: String) {
        binding.hourlyForecast.root.visibility = View.GONE
    }

    override fun refreshCityList(cityList: MutableList<ChangeCityBean>) {
        // 给城市弹层传递数据
        changeCityDialog?.refreshCityList(cityList)
    }

    // --------------------------------
    /**
     * 刷新数据
     */
    fun refreshLocationWeatherData(
        cityName: String,
        latitude: String,
        longitude: String
    ) {

        // 设置城市名称
        binding.topBar.tvCityName.text = cityName

        homePresenter?.setCityLocationParams(cityName, latitude, longitude)
        showLoading()
        homePresenter?.requestMainWeatherData()
    }

    /**
     * 显示当前时间
     */
    fun showCurrentTime() {
        val currentTime = System.currentTimeMillis()
        binding.topBar.tvCurrentTime.text =
            WeatherUtils.formatUTC(currentTime, "yyyy-MM-dd HH:mm:ss")
        binding.currentWeatherCard.tvWeekday.text = WeatherUtils.formatUTC(currentTime, "EEEE")
    }


    /**
     * 开始更新时间
     */
    private fun startUpdateTime() {
        handler.post(updateTimeRunnable)
    }

    /**
     * 停止更新时间
     */
    private fun stopUpdateTime() {
        handler.removeCallbacks(updateTimeRunnable)
    }

}