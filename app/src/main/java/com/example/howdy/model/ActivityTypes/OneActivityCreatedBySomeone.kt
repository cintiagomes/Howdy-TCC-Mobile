package com.example.howdy.model.ActivityTypes

import com.example.howdy.model.UserTypes.Commenter
import java.util.*

class OneActivityCreatedBySomeone(
    didIUnlockThisActivity: String?,
    idActivity: Int,
    activityCoverPhoto: String,
    activitySubtitle: String,
    activityTitle: String,
    description: String,
    createdAt: Date,
    priceHowdyCoin: Int,
    idTargetLanguage: Int,
    targetLanguageName: String,
    totalRating: Int,
    totalStars: Int,
    starsRating: Int,
    totalQuestion: Int,
    totalTheoricalContentBlock: Int,
    totalStudent: Int,
) {
    var didIUnlockThisActivity = didIUnlockThisActivity
    var idActivity = idActivity
    var activityCoverPhoto = activityCoverPhoto
    var activitySubtitle = activitySubtitle
    var activityTitle = activityTitle
    var description = description
    var createdAt = createdAt
    var priceHowdyCoin = priceHowdyCoin
    var idTargetLanguage = idTargetLanguage
    var targetLanguageName = targetLanguageName
    var totalRating = totalRating
    var totalStars = totalStars
    var starsRating = starsRating
    var totalQuestion = totalQuestion
    var totalTheoricalContentBlock = totalTheoricalContentBlock
    var totalStudent = totalStudent
}