package com.example.weather_wearos.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LocationManager  {
    val dataLoaded = mutableStateOf(false)

    val data = mutableStateOf(
        CardData(
            weatherInfo = "",
            time = "", name = "", temp = 0.0
        )
    )

    fun createLocationRequest(
        context: Context,
        fusedLocationclient:
        FusedLocationProviderClient
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 100
        ).build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationclient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val weatherDTO = WeatherApi.apiInstance.getWeatherDetails(
                                location.latitude,
                                location.longitude, "my weather api")
                            data.value = CardData(
                                weatherInfo = weatherDTO.weather[0].description,
                                time = "${(weatherDTO.main.temp - 273).roundToInt()}°C",
                                name = weatherDTO.name,
                                temp = (weatherDTO.main.temp - 273).roundToInt().toDouble()
                            )
                        }
                    }
                }
            }, Looper.getMainLooper()
        )
    }
}
