package com.example.howdy.model.PostTypes.PostCommentaryTypes

import com.example.howdy.model.UserTypes.Commenter

class Commentary(
    commenter: Commenter,
    idPostCommentary: Int,
    textCommentary: String,
    postCommentaryCreatedAt: String,
    postCommentaryEditedAt: String
){
    var commenter = commenter
    var idPostCommentary = idPostCommentary
    var textCommentary = textCommentary
    var postCommentaryCreatedAt = postCommentaryCreatedAt
    var postCommentaryEditedAt = postCommentaryEditedAt
}