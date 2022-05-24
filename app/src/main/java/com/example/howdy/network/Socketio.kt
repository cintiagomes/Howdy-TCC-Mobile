package com.example.howdy.network

import com.example.howdy.remote.APIUtil
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

private const val URL = APIUtil.API_URL

object Socketio {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket(URL)
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}