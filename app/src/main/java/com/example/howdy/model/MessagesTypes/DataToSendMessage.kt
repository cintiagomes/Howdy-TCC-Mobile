package com.example.howdy.model.MessagesTypes

class DataToSendMessage(
    idToken: String,
    idUserReceiver: Int,
    textContent: String
) {
    val idToken = idToken
    val idUserReceiver = idUserReceiver
    val message = textContent
}