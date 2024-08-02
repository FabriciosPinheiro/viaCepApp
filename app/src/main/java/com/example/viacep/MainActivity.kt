package com.example.viacep

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.viacep.api.Api
import com.example.viacep.databinding.ActivityMainBinding
import com.example.viacep.model.Endereco
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#FF018786")
        val actionBar = supportActionBar
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF018786")))

        //Configurar retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

        binding.btnBuscarCep.setOnClickListener{
            val cep = binding.edtCep.text.toString()

            if (cep.isEmpty()){
                Toast.makeText(this, "Preencha o CEP!", Toast.LENGTH_SHORT).show()
            }else{
                retrofit.setEndereco(cep).enqueue(object : Callback<Endereco>{
                    override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                        if (response.code() == 200){
                            val logradouro = response.body()?.logradouro.toString()
                            val bairro = response.body()?.bairro.toString()
                            val localidade = response.body()?.localidade.toString()
                            val uf = response.body()?.uf.toString()

                            setFormularios(logradouro, bairro, localidade, uf)
                        }else{
                            Toast.makeText(applicationContext, "CEP inválido!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    private fun setFormularios(logradouro: String?, bairro: String?, localidade: String?, uf: String?) {
                        binding.edtLogradouro.setText(logradouro)
                        binding.edtBairro.setText(bairro)
                        binding.edtCidade.setText(localidade)
                        binding.edtEstado.setText(uf)

                    }

                    override fun onFailure(call: Call<Endereco>, t: Throwable) {
                        Toast.makeText(applicationContext, "Erro ao buscar o CEP!", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

    }
}