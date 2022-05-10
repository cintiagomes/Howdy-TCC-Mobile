package com.example.howdy.model.UserTypes

class UserInRanking(
    idUser: Int,
    profilePhoto: String,
    userName: String,
    idTargetLanguage: Int,
    targetLanguageName: String,
    idNativeLanguage: Int,
    nativeLanguageName: String,
    totalXp: Int,
    patent: String,
    isPro: Boolean,
    positionRanking: Int
) {
    var idUser = idUser
    var profilePhoto = profilePhoto
    var userName = userName
    var idTargetLanguage = idTargetLanguage
    var targetLanguageName = targetLanguageName
    var idNativeLanguage = idNativeLanguage
    var nativeLanguageName = nativeLanguageName
    var totalXp = totalXp
    var patent = patent
    var isPro = isPro
    var positionRanking = positionRanking
}