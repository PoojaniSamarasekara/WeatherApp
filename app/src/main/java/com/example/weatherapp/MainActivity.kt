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

    // API key comes from local.properties through BuildConfig
    private val API_KEY = BuildConfig.OPENWEATHER_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Input field and search button
        val cityInput = findViewById<EditText>(R.id.etCity)
        val searchButton = findViewById<Button>(R.id.btnSearch)

        // Weather information TextViews
        val cityText = findViewById<TextView>(R.id.tvCity)
        val temperatureText = findViewById<TextView>(R.id.tvTemperature)
        val conditionText = findViewById<TextView>(R.id.tvCondition)
        val humidityText = findViewById<TextView>(R.id.tvHumidity)
        val windText = findViewById<TextView>(R.id.tvWindSpeed)

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create WeatherApi
        val api = retrofit.create(WeatherApi::class.java)

        // Search Weather button
        searchButton.setOnClickListener {

            // Read city name
            val city = cityInput.text.toString().trim()

            // Validate input
            if (city.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a city name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Send API request
            api.getWeather(city, API_KEY).enqueue(
                object : Callback<WeatherResponse> {

                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {

                        if (response.isSuccessful && response.body() != null) {

                            // Get parsed response
                            val weatherData = response.body()!!

                            // Extract data from API response
                            val cityName = weatherData.name
                            val temperature = weatherData.main.temp
                            val condition =
                                weatherData.weather[0].description
                            val humidity = weatherData.main.humidity
                            val windSpeed = weatherData.wind.speed

                            // Display API data in the application
                            cityText.text = "City: $cityName"
                            temperatureText.text =
                                "Temperature: ${temperature}°C"
                            conditionText.text =
                                "Condition: $condition"
                            humidityText.text =
                                "Humidity: $humidity%"
                            windText.text =
                                "Wind Speed: $windSpeed m/s"

                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                "City not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<WeatherResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@MainActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}