package com.example.howdy

import com.example.howdy.remote.APIUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class StripeAPIClient {
    private val httpClient = OkHttpClient()

    fun createPaymentIntent(
        idToken: String,
        howdyCoinsQuantity: Int,
        amount: Double,
        paymentMethodType: String,
        currency: String,
        completion: (paymentIntentClientSecret: String?, error: String?) -> Unit
    ) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = """
            {
            "idToken": "$idToken",
            "howdyCoinsQuantity": $howdyCoinsQuantity,
            "amount": $amount,
            "currency": "$currency",
            "paymentMethodType": "$paymentMethodType"
            }
        """.trimIndent()
        val body = requestJson.toRequestBody(mediaType)
        val request = Request.Builder().url("${APIUtil.API_URL}/buyHowdyCoin").post(body).build()
        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(null, "$e")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()
                    var paymentIntentClientSecret: String = responseJson.getString("clientSecret")
                    completion(paymentIntentClientSecret, null)
                } else {
                    completion(null, "$response")
                }
            }
        })
    }
}