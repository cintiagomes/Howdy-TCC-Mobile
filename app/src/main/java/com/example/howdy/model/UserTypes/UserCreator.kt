package com.example.howdy.model.UserTypes

class UserCreator(
    idUser: Int,
    birthDate: String,
    profilePhoto: String?,
    userName: String,
    isPro: Boolean,
    totalXp: Int,
    patent: String?
){
    var idUser = idUser
    var birthDate = birthDate
    var profilePhoto = profilePhoto
    var userName = userName
    var isPro = isPro
    var totalXp = totalXp
    var patent = patent
}