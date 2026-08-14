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

    // Do NOT put your real API key into GitHub.
    // Replace this with a locally stored key later.
    private val API_KEY = BuildConfig.OPENWEATHER_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the UI components
        val cityInput = findViewById<EditText>(R.id.etCity)
        val searchButton = findViewById<Button>(R.id.btnSearch)

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create WeatherApi implementation
        val api = retrofit.create(WeatherApi::class.java)

        // When Search Weather is clicked
        searchButton.setOnClickListener {

            // 1. Read city name
            val city = cityInput.text.toString().trim()

            // 2. Validate input
            if (city.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a city name",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // 3. Construct and 4. Send GET request
            api.getWeather(city, API_KEY).enqueue(
                object : Callback<WeatherResponse> {

                    // 5. Receive response
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {

                        if (response.isSuccessful && response.body() != null) {
                            val weatherResponse = response.body()!!

                            findViewById<TextView>(R.id.tvCity).text = "City: ${weatherResponse.name}"
                            findViewById<TextView>(R.id.tvTemperature).text = "Temperature: ${weatherResponse.main.temp} °C"
                            findViewById<TextView>(R.id.tvCondition).text = "Condition: ${weatherResponse.weather.firstOrNull()?.description ?: "--"}"
                            findViewById<TextView>(R.id.tvHumidity).text = "Humidity: ${weatherResponse.main.humidity} %"
                            findViewById<TextView>(R.id.tvWindSpeed).text = "Wind Speed: ${weatherResponse.wind.speed} km/h"

                            Toast.makeText(
                                this@MainActivity,
                                "Weather response received successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                "API request failed",
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