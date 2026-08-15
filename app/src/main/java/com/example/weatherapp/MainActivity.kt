package com.example.weatherapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://api.openweathermap.org/"

    // API key is loaded from local.properties through BuildConfig
    private val API_KEY = BuildConfig.OPENWEATHER_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Connect to the UI
        val cityInput = findViewById<EditText>(R.id.etCity)
        val searchButton = findViewById<Button>(R.id.btnSearch)

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create WeatherApi implementation
        val api = retrofit.create(WeatherApi::class.java)

        // Search Weather button
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

            // 3. Construct request
            // 4. Send GET request
            api.getWeather(city, API_KEY).enqueue(
                object : Callback<WeatherResponse> {

                    // 5. Receive and process response
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {

                        if (response.isSuccessful && response.body() != null) {

                            // Get the converted JSON response
                            val weatherData = response.body()!!

                            // Extract required values
                            val cityName = weatherData.name
                            val temperature = weatherData.main.temp
                            val condition =
                                weatherData.weather[0].description
                            val humidity = weatherData.main.humidity
                            val windSpeed = weatherData.wind.speed

                            // Display extracted values temporarily
                            Toast.makeText(
                                this@MainActivity,
                                """
                                City: $cityName
                                Temperature: $temperature°C
                                Condition: $condition
                                Humidity: $humidity%
                                Wind Speed: $windSpeed m/s
                                """.trimIndent(),
                                Toast.LENGTH_LONG
                            ).show()

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