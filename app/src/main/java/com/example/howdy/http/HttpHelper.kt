package com.example.howdy.http

import com.google.firebase.auth.GetTokenResult
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class HttpHelper {
    private val serverDomain:String = "http://192.168.10.249:3333"

    fun post(route:String, idToken: String, json:String ):String{
        //DEFINIR URL DO SERVIDOR
        val url = "$serverDomain$route"

        // DEFINIR O CABEÇALHO
        val headerHttp = MediaType.parse("application/json; charset=utf-8")

        //CRIANDO UM CLIENTE QUE IRÁ DISPARAR A REQUISIÇÃO
        val client = OkHttpClient()

        //CRIANDO BODY DA REQUISIÇÃO
        val body = RequestBody.create(headerHttp, json)

        //CONSTRUINDO REQUISIÇÃO HTTP DO TIPO POST AO SERVIDOR
        val request = Request.Builder().url(url)
            .addHeader("Authorization", idToken)
            .post(body).build()

        //UTILIZAR O CLIENT PARA FAZER A REQUISIÇÃO E RECEBER A RESPOSTA
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return "error " + response.code() + ": " + response.body()!!.string()

            return response.body()!!.string()
        }
    }

    fun get(route:String, idToken: String ):String{
        //DEFINIR URL DO SERVIDOR
        val url = "$serverDomain$route"

        //CRIANDO UM CLIENTE QUE IRÁ DISPARAR A REQUISIÇÃO
        val client = OkHttpClient()

        //CONSTRUINDO REQUISIÇÃO HTTP DO TIPO POST AO SERVIDOR
        val request = Request.Builder().url(url)
            .addHeader("Authorization", idToken)
            .build()

        //UTILIZAR O CLIENT PARA FAZER A REQUISIÇÃO E RECEBER A RESPOSTA
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return "error " + response.code() + ": " + response.body()!!.string()

            return response.body()!!.string()
        }
    }
}