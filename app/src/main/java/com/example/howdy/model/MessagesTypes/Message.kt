package com.example.howdy.model.MessagesTypes

import java.util.*

class Message(
    createdAt: Date,
    idMessage: Int,
    idUserReceiver: Int,
    idUserSender: Int,
    readAt: Date?,
    textContent: String,
    translatedTextContent: String?
) {
    var createdAt = createdAt
    var idMessage = idMessage
    var idUserReceiver = idUserReceiver
    var idUserSender = idUserSender
    var readAt = readAt
    var textContent = textContent
    var translatedTextContent = translatedTextContent
}