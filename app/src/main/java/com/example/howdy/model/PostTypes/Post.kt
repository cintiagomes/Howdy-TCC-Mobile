package com.example.howdy.model.PostTypes
import com.example.howdy.model.UserTypes.UserCreator

class Post(
    userCreator: UserCreator,
    idPost: Int,
    imageContent: String?,
    createdAt: String,
    textContent: String,
    isPublic: Int,
    idTargetLanguage: Int,
    idPostCategory: Int,
    targetLanguageName: String,
    targetLanguageTranslatorName: String,
    categoryName: String,
    postCategoryIconImage: String,
    hexadecimalColor: String,
    totalLikes: Int,
    liked: Boolean,
    totalComments: Int
) {
    var userCreator = userCreator
    var idPost = idPost
    var imageContent = imageContent
    var createdAt = createdAt
    var textContent = textContent
    var isPublic = isPublic
    var idTargetLanguage = idTargetLanguage
    var idPostCategory = idPostCategory
    var targetLanguageName = targetLanguageName
    var targetLanguageTranslatorName = targetLanguageTranslatorName
    var categoryName = categoryName
    var postCategoryIconImage = postCategoryIconImage
    var hexadecimalColor = hexadecimalColor
    var totalLikes = totalLikes
    var liked = liked
    var totalComments = totalComments
}