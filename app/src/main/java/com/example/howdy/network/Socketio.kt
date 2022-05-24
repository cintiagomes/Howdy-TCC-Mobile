package com.example.howdy.network

import android.app.Application
import com.example.howdy.remote.APIUtil
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

private const val URL = APIUtil.API_URL

class SocketInstance : Application() {
    //socket.io connection url
    private var mSocket: Socket? = null

    override fun onCreate() {
        super.onCreate()
        try {
            //creating socket instance
            mSocket = IO.socket(URL).connect()
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    //return socket instance
    fun getMSocket(): Socket? {
        return mSocket
    }
}