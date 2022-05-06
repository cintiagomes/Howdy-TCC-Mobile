package com.example.howdy.model.UserTypes

class User(
    idUser: Int,
    birthDate: String,
    profilePhoto: String?,
    userName: String,
    description: String?,
    backgroundImage: String?,
    subscriptionEndDate: String?,
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
    var howdyCoin = howdyCoin
    var idTargetLanguage = idTargetLanguage
    var targetLanguageName = targetLanguageName
    var targetLanguageTranslatorName = targetLanguageTranslatorName
    var idNativeLanguage = idNativeLanguage
    var nativeLanguageName = nativeLanguageName
    var nativeLanguageTranslatorName = nativeLanguageTranslatorName
}