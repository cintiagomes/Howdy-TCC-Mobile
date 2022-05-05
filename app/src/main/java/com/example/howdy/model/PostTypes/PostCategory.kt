package com.example.howdy.model.PostTypes
import com.example.howdy.model.UserTypes.UserCreator

class PostCategory(
    idPostCategory: Int,
    categoryName: String,
    iconImage: String,
    hexadecimalColor: String
) {
    var idPostCategory = idPostCategory
    var categoryName = categoryName
    var iconImage = iconImage
    var hexadecimalColor = hexadecimalColor
}