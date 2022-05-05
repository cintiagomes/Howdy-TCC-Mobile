package com.example.howdy.model.PostTypes
import com.example.howdy.model.UserTypes.UserCreator

class DataToCreatePostWithoutImage(
    textContent: String,
    isPublic: Boolean,
    idPostCategory: Int
) {
    var textContent = textContent
    var isPublic = isPublic
    var idPostCategory = idPostCategory
}