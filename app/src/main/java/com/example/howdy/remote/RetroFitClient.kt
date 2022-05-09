package com.example.howdy.remote

import retrofit2.Retrofit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.example.howdy.remote.RetroFitClient
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitClient {
    private var retrofit: Retrofit? = null

    /** MÉTODO DE ACESSO AO CLIENT  */
    fun getClient(url: String?): Retrofit? {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit
    }
}