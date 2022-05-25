package com.example.howdy.model.NotificationTypes

import java.util.*

class Notification(
    idNotification: Int,
    notificationText: String,
    wasRead: Int,
    createdAt: Date,
    idNotificationType: Int,
    idUserSender: Int,
    idUserReceiver: Int,
    type: String,
    userSenderName: String,
    userSenderProfilePhoto: String?
) {
    var idNotification = idNotification
    var notificationText = notificationText
    var wasRead = wasRead
    var createdAt = createdAt
    var idNotificationType = idNotificationType
    var idUserSender = idUserSender
    var idUserReceiver = idUserReceiver
    var type = type
    var userSenderName = userSenderName
    var userSenderProfilePhoto = userSenderProfilePhoto
}