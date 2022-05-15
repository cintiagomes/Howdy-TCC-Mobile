package com.example.howdy.model.UserTypes

import java.util.*

class User(
    idUser: Int,
    birthDate: Date,
    profilePhoto: String?,
    userName: String,
    description: String?,
    backgroundImage: String?,
    subscriptionEndDate: Date?,
    isPro: Boolean,
    howdyCoin: Int,
    idTargetLanguage: Int,
    targetLanguageName: String,
    targetLanguageTranslatorName: String,
    idNativeLanguage : Int,
    nativeLanguageName: String,
    nativeLanguageTranslatorName: String
) {
    var idUser = idUser
    var birthDate = birthDate
    var profilePhoto = profilePhoto
    var userName = userName
    var description = description
    var backgroundImage = backgroundImage
    var subscriptionEndDate = subscriptionEndDate
    var isPro = isPro
    var howdyCoin = howdyCoin
    var idTargetLanguage = idTargetLanguage
    var targetLanguageName = targetLanguageName
    var targetLanguageTranslatorName = targetLanguageTranslatorName
    var idNativeLanguage = idNativeLanguage
    var nativeLanguageName = nativeLanguageName
    var nativeLanguageTranslatorName = nativeLanguageTranslatorName
}