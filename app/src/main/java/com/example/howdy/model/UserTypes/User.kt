package com.example.howdy.model.UserTypes

class User() {
    var idUser = 0
    var birthDate = ""
    var profilePhoto = ""
    var userName = ""
    var description = ""
    var backgroundImage = ""
    var subscriptionEndDate = ""
    var howdyCoin = 0
    var idTargetLanguage = 0
    var targetLanguageName = ""
    var targetLanguageTranslatorName = ""
    var idNativeLanguage = 0
    var nativeLanguageName = ""
    var nativeLanguageTranslatorName = ""
}

class UserRegister(userName: String, birthDate: String, targetLanguage: TargetLanguage, nativeLanguage: NativeLanguage){
    var userName = userName
    var birthDate = birthDate
    var targetLanguage = targetLanguage
    var nativeLanguage = nativeLanguage
}

class UserCreator(){
    var idUser = 0
    var birthDate = ""
    var profilePhoto = ""
    var userName = ""
    var isPro = true
    var totalXp = 0
    var patent = ""
}