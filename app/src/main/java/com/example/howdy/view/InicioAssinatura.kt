package com.example.howdy.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageButton
import com.example.howdy.R
import com.example.howdy.databinding.ActivityInicioAssinaturaBinding
import com.stripe.android.PaymentConfiguration
import kotlinx.android.synthetic.main.activity_inicio_assinatura.*

class InicioAssinatura : AppCompatActivity() {
    private lateinit var binding: ActivityInicioAssinaturaBinding
    private lateinit var btnBuy150HowdyCoins: Button
    private lateinit var btnBuy450HowdyCoins: Button
    private lateinit var btnBuy650HowdyCoins: Button
    private lateinit var btnBuy1000HowdyCoins: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioAssinaturaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51KoCAOJqzk2oN85Uv9mQWrHTy28bqpqnWCqWqIULYCSxH3YvPQp5qQZQpXqJZEBlwPdDtE6XCXMN8S0WKbnDDkbN00gJlG99YF"
        )

        val actionBar = supportActionBar

        actionBar!!.hide()
        setContentView(R.layout.activity_inicio_assinatura)

        val arrowButton = findViewById<ImageButton>(R.id.arrow_button)
        arrowButton.setOnClickListener{ finish() }

        btnBuy150HowdyCoins = btn_buy_150_howdy_coins
        btnBuy450HowdyCoins = btn_buy_450_howdy_coins
        btnBuy650HowdyCoins = btn_buy_650_howdy_coins
        btnBuy1000HowdyCoins = btn_buy_1000_howdy_coins
    }

}