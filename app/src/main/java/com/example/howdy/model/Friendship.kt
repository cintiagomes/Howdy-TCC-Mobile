package com.example.howdy.model

class Friendship(
    idFriendship: Int,
    idUserSender: Int,
    idUserAcceptor: Int,
    isPending: Int,
    message: String?
) {
    var idFriendship: Int = idFriendship
    var idUserSender: Int = idUserSender
    var idUserAcceptor: Int = idUserAcceptor
    var isPending: Int = isPending
    var message: String? = message
}