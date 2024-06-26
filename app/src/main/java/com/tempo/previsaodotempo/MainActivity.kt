package com.tempo.previsaodotempo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tempo.previsaodotempo.constants.Const
import com.tempo.previsaodotempo.databinding.ActivityMainBinding
import com.tempo.previsaodotempo.model.Main
import com.tempo.previsaodotempo.services.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.trocarTema.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) { // Tema escuro - Dark Mode
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#000000"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_escuro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#000000"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#000000"))
                window.statusBarColor = Color.parseColor("#000000")
            } else { // Tema claro
                binding.containerPrincipal.setBackgroundColor(Color.parseColor("#396BCB"))
                binding.containerInfo.setBackgroundResource(R.drawable.container_info_tema_claro)
                binding.txtTituloInfo.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.txtInformacoes2.setTextColor(Color.parseColor("#FFFFFF"))
                window.statusBarColor = Color.parseColor("#396BCB")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.containerPrincipal)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btBuscar.setOnClickListener {
            val cidade = binding.editBuscarCidade.text.toString()
            binding.progressBar.visibility = View.VISIBLE

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build().create(Api::class.java)

            retrofit.weatherMap(cidade, Const.API_KEY).enqueue(object : Callback<Main> {
                override fun onResponse(p0: Call<Main>, response: Response<Main>) {
                    if (response.isSuccessful) {
                        respostaServidor(response)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Cidade inválida!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }

                override fun onFailure(p0: Call<Main>, response: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Erro fatal de servidor!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.progressBar.visibility = View.GONE
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Api::class.java)

        retrofit.weatherMap("São Paulo", Const.API_KEY).enqueue(object : Callback<Main> {
            override fun onResponse(p0: Call<Main>, response: Response<Main>) {
                if (response.isSuccessful) {
                    respostaServidor(response)
                } else {
                    Toast.makeText(applicationContext, "Cidade inválida!", Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(p0: Call<Main>, response: Throwable) {
                Toast.makeText(applicationContext, "Erro fatal de servidor!", Toast.LENGTH_LONG)
                    .show()
                binding.progressBar.visibility = View.GONE
            }

        })
    }


    @SuppressLint("SetTextI18n")
    private fun respostaServidor(response: Response<Main>) {
        val main = response.body()!!.main
        val sys = response.body()!!.sys
        val weather = response.body()!!.weather

        val temp = main.get("temp").toString()
        val tempMin = main.get("temp_min").toString()
        val tempMax = main.get("temp_max").toString()
        val humidity = main.get("humidity").toString()

        val country = sys.get("country").asString
        var pais = ""
        val main_weather = weather[0].main
        val description = weather[0].description
        val name = response.body()!!.name

        // Converter Kelvin em graus Celsius - Fórmula: C = K - 273.15
        val temp_c: Double = (temp.toDouble() - 273.15)
        val tempMin_c: Double = (tempMin.toDouble() - 273.15)
        val tempMax_c: Double = (tempMax.toDouble() - 273.15)
        val decimalFormat = DecimalFormat("00")

        if (country.equals("BR")) {
            pais = "Brasil"
        } else if (country.equals("US")) {
            pais = "Estados Unidos"
        }

        if (main_weather.equals("Clouds") && description.equals("few clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.flewclouds)
        } else if (main_weather.equals("Clouds") && description.equals("scattered clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.clouds)
        } else if (main_weather.equals("Clouds") && description.equals("broken clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clouds") && description.equals("overcast clouds")) {
            binding.imgClima.setBackgroundResource(R.drawable.brokenclouds)
        } else if (main_weather.equals("Clear") && description.equals("clear sky")) {
            binding.imgClima.setBackgroundResource(R.drawable.clearsky)
        } else if (main_weather.equals("Rain") || main_weather.equals("Drizzle")) {
            binding.imgClima.setBackgroundResource(R.drawable.rain)
        } else if (main_weather.equals("Snow")) {
            binding.imgClima.setBackgroundResource(R.drawable.snow)
        } else if (main_weather.equals("Thunderstorm")) {
            binding.imgClima.setBackgroundResource(R.drawable.trunderstorm)
        }

        val descricaoClima = when (description) {
            "few clouds" -> {
                "Poucas Nuvens"
            }

            "scattered clouds" -> {
                "Nuvens dispersas"
            }

            "broken clouds" -> {
                "Nuvens quebradas"
            }

            "overcast clouds" -> {
                "Nuvens quebradas"
            }

            "clear sky" -> {
                "Céu Limpo"
            }

            "rain" -> {
                "Chuva"
            }

            "drizzle" -> {
                "Chuva"
            }

            "snow" -> {
                "Neve"
            }

            "thunderstorm" -> {
                "Tempestade"
            }

            else -> {
                "Desconhecido"
            }
        }

        binding.txtTemperatura.setText("${decimalFormat.format(temp_c)}°C")
        binding.txtPaisCidade.setText("$pais - $name")
        binding.txtInformacoes1.setText("Clima \n $descricaoClima \n\n Umidade \n $humidity")
        binding.txtInformacoes2.setText(
            "Temp.Min \n ${decimalFormat.format(tempMin_c)} \n\n Temp.Max \n ${
                decimalFormat.format(
                    tempMax_c
                )
            }"
        )
        binding.progressBar.visibility = View.GONE
    }

}