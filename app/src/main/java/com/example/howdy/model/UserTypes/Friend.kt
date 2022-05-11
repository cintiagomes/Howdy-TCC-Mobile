package com.example.howdy.model.UserTypes

class Friend(
    idUser: Int,
    profilePhoto: String,
    userName: String,
    idTargetLanguage: Int,
    targetLanguageName: String,
    totalXp: Int,
    patent: String,
    isPro: Boolean,
) {
    var idUser = idUser
    var profilePhoto = profilePhoto
    var userName = userName
    var idTargetLanguage = idTargetLanguage
    var targetLanguageName = targetLanguageName
    var totalXp = totalXp
    var patent = patent
    var isPro = isPro
}