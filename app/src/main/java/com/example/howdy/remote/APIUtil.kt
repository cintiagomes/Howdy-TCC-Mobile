package com.example.howdy.remote

object APIUtil {
    const val API_URL = "http://10.107.144.21:3333"
    val `interface`: RouterInterface
        get() = RetroFitClient.getClient(API_URL)
            ?.create(RouterInterface::class.java)!!
}