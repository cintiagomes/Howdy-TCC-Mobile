package com.example.howdy.model.UserTypes

import com.example.howdy.model.XpCharts

class UserCollectedWithId(
    idUser: Int,
    profilePhoto: String?,
    userName: String,
    idTargetLanguage: Int,
    targetLanguageName: String,
    idNativeLanguage: Int,
    nativeLanguageName: String,
    totalXp: Int,
    patent: String?,
    isPro: Boolean,
    backgroundImage: String?,
    description: String,
    targetLanguageTranslatorName: String,
    nativeLanguageTranslatorName: String,
    averageEvaluations: Double,
    xpCharts: XpCharts
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
    var backgroundImage = backgroundImage
    var description = description
    var targetLanguageTranslatorName = targetLanguageTranslatorName
    var nativeLanguageTranslatorName = nativeLanguageTranslatorName
    var averageEvaluations = averageEvaluations
    var xpCharts = xpCharts
}