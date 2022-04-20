package com.example.howdy.http

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class HttpHelper {
    private val serverDomain:String = "http://ipDaMaquina:3333"

    fun post(json: String, route:String ):String{
        //DEFINIR URL DO SERVIDOR
        val url = "$serverDomain$route"

        // DEFINIR O CABEÇALHO
        val headerHttp = MediaType.parse("application/json; charset=utf-8")

        //CRIANDO UM CLIENTE QUE IRÁ DISPARAR A REQUISIÇÃO
        val client = OkHttpClient()

        //CRIANDO BODY DA REQUISIÇÃO
        val body = RequestBody.create(headerHttp, json)

        //CONSTRUINDO REQUISIÇÃO HTTP DO TIPO POST AO SERVIDOR
        val request = Request.Builder().url(url).post(body).build()

        //UTILIZAR O CLIENT PARA FAZER A REQUISIÇÃO E RECEBER A RESPOSTA
        val response = client.newCall(request).execute()

        return response.body().toString()
    }

    /*fun get(route:String ):String{
        //DEFINIR URL DO SERVIDOR
        val url = "$serverDomain$route"

        // DEFINIR O CABEÇALHO
        val headerHttp = MediaType.parse("application/json; charset=utf-8")

        //CRIANDO UM CLIENTE QUE IRÁ DISPARAR A REQUISIÇÃO
        val client = OkHttpClient()

        //CRIANDO BODY DA REQUISIÇÃO
        //val body = RequestBody.create(headerHttp, json)

        //CONSTRUINDO REQUISIÇÃO HTTP DO TIPO POST AO SERVIDOR
        //val request = Request.Builder().url(url).post(body).build()

        //UTILIZAR O CLIENT PARA FAZER A REQUISIÇÃO E RECEBER A RESPOSTA
        //val response = client.newCall(request).execute()

        //return response.body().toString()
    }*/
}