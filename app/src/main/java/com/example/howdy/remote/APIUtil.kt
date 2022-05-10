package com.example.howdy.remote

object APIUtil {
    private const val API_URL = "http://192.168.10.249:3333"
    val `interface`: RouterInterface
        get() = RetroFitClient.getClient(API_URL)
            ?.create(RouterInterface::class.java)!!
}