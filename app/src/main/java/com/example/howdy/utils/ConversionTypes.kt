package com.example.howdy.utils

import android.text.Editable
import java.text.SimpleDateFormat
import java.util.*

fun convertStringtoEditable(text: String) : Editable? {
    return Editable.Factory.getInstance().newEditable(text)
}