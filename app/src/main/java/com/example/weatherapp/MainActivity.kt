package com.example.weatherapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.openweathermap.org/"
    private val API_KEY = BuildConfig.OPENWEATHER_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityInput = findViewById<EditText>(R.id.etCity)
        val searchButton = findViewById<Button>(R.id.btnSearch)

        val cityText = findViewById<TextView>(R.id.tvCity)
        val temperatureText = findViewById<TextView>(R.id.tvTemperature)
        val conditionText = findViewById<TextView>(R.id.tvCondition)
        val humidityText = findViewById<TextView>(R.id.tvHumidity)
        val windText = findViewById<TextView>(R.id.tvWindSpeed)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)

        searchButton.setOnClickListener {

            // CASE 1: Empty City Name
            val city = cityInput.text.toString().trim()

            if (city.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a city name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            api.getWeather(city, API_KEY).enqueue(
                object : Callback<WeatherResponse> {

                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {

                        // SUCCESS
                        if (response.isSuccessful && response.body() != null) {

                            val weatherData = response.body()!!

                            val cityName = weatherData.name
                            val temperature = weatherData.main.temp
                            val condition =
                                weatherData.weather[0].description
                            val humidity = weatherData.main.humidity
                            val windSpeed = weatherData.wind.speed

                            cityText.text = cityName
                            temperatureText.text = "${temperature}°C"
                            conditionText.text = condition
                            humidityText.text = "$humidity%"
                            windText.text = "$windSpeed m/s"

                        } else {

                            // CASE 2 & 4: Invalid City / API Error

                            when (response.code()) {

                                401 -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Invalid API key",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                404 -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "City not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                429 -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Too many requests. Try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "API error: ${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    // CASE 3: Network Error
                    override fun onFailure(
                        call: Call<WeatherResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            "Network error. Please check your internet connection.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}