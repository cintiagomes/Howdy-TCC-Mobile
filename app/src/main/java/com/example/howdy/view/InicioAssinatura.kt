package com.example.howdy.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.howdy.R
import com.example.howdy.StripeAPIClient
import com.example.howdy.databinding.ActivityInicioAssinaturaBinding
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.activity_inicio_assinatura.*
import kotlinx.android.synthetic.main.dialog_purchase.*
import kotlinx.android.synthetic.main.dialog_purchase.view.*
import kotlinx.coroutines.launch

class InicioAssinatura : AppCompatActivity() {
    private val context = this

    private lateinit var paymentIntentClientSecret: String
    private lateinit var paymentLauncher: PaymentLauncher
    private lateinit var stripe: Stripe

    private lateinit var binding: ActivityInicioAssinaturaBinding
    private lateinit var btnBuy150HowdyCoins: Button
    private lateinit var btnBuy450HowdyCoins: Button
    private lateinit var btnBuy650HowdyCoins: Button
    private lateinit var btnBuy1000HowdyCoins: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val publicKey = "pk_test_51KoCAOJqzk2oN85Uv9mQWrHTy28bqpqnWCqWqIULYCSxH3YvPQp5qQZQpXqJZEBlwPdDtE6XCXMN8S0WKbnDDkbN00gJlG99YF"
        binding = ActivityInicioAssinaturaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stripe = Stripe(
            this,
            publicKey
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

        btnBuy150HowdyCoins.setOnClickListener { openPurchaseModal(150) }
        btnBuy450HowdyCoins.setOnClickListener { openPurchaseModal(450) }
        btnBuy650HowdyCoins.setOnClickListener { openPurchaseModal(650) }
        btnBuy1000HowdyCoins.setOnClickListener { openPurchaseModal(1000) }
    }

    private fun startCheckout(howdyCoinsQuantity: Int) {
        //RESGATANDO IDTOKEN DO FIREBASE
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                val idToken = result.token!!
        // Create a PaymentIntent by calling the sample server's /create-payment-intent endpoint.
        StripeAPIClient().createPaymentIntent(
            idToken,
            howdyCoinsQuantity,
            312.0,
            "brl",
            "card",
            completion =  {
                paymentIntentClientSecret, error ->
            run {
                paymentIntentClientSecret?.let {
                    this.paymentIntentClientSecret = it
                }
                error?.let {
                    println("DEBUGANDO ERRO NO PAGAMENTO: ${error}")
                }
            }
        })
        }
    }

    private fun onPaymentResult(paymentResult: PaymentResult) {
        val message = when (paymentResult) {
            is PaymentResult.Completed -> {
                "Completed!"
            }
            is PaymentResult.Canceled -> {
                Toast.makeText(context, "O pagamento foi cancelado", Toast.LENGTH_SHORT).show()
            }
            is PaymentResult.Failed -> {
                // This string comes from the PaymentIntent's error message.
                // See here: https://stripe.com/docs/api/payment_intents/object#payment_intent_object-last_payment_error-message
                println("DEBUGANDO ERRO NO PAGAMENTO: ${paymentResult.throwable.message}")
            }
        }
        println(
            "Payment Result:" +
            message
        )
    }

    private fun openPurchaseModal(howdyCoins: Int) {

        val view = View.inflate(this, R.layout.dialog_purchase, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        view.tv_title.text = "Comprar $howdyCoins Howdy Coins"

        view.btn_close_modal.setOnClickListener {
            dialog.dismiss()
        }

        startCheckout(howdyCoins)

        view.btn_buy_howdy_coins.setOnClickListener {
            // Confirm the PaymentIntent with the card widget
//            println("DEBUGANDO PAYMENTINTENTCLIENTSECRET: ${paymentIntentClientSecret}")
//            println("DEBUGANDO cardInputWidget: $cardInputWidgetView")
//            if (cardInputWidgetView!= null){
//                cardInputWidgetView.paymentMethodCreateParams?.let { params ->
//                    println("DEBUGANDO params: $params")
//                    val confirmParams = ConfirmPaymentIntentParams
//                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
//
//                    stripe.confirmPayment(this, confirmParams)
//                    lifecycleScope.launch { paymentLauncher.confirm(confirmParams)
//                    }
//                }
//            }
            dialog.dismiss()
        }
    }
}