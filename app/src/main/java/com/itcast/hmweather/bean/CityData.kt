package com.itcast.hmweather.bean

data class CityData(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val weather: String,
    val temperature: String
) {
    // 重写equals和hashCode，使用name作为唯一标识
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CityData
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
