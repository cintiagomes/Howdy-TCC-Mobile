package com.example.howdy.model.PostTypes.PostCommentaryTypes

import com.example.howdy.model.UserTypes.Commenter
import java.util.*

class Commentary(
    commenter: Commenter,
    idPostCommentary: Int,
    textCommentary: String,
    translatedtTextCommentary: String?,
    postCommentaryCreatedAt: Date,
    postCommentaryEditedAt: Date?
) {
    var commenter = commenter
    var idPostCommentary = idPostCommentary
    var textCommentary = textCommentary
    var translatedtTextCommentary = translatedtTextCommentary
    var postCommentaryCreatedAt = postCommentaryCreatedAt
    var postCommentaryEditedAt = postCommentaryEditedAt

}