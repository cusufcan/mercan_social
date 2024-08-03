package com.cusufcan.mercansocial.util

import android.widget.EditText

fun String.validate(): Boolean {
    return this.isNotEmpty() && this.isNotBlank()
}

fun EditText.trimmedText(): String {
    return this.text.toString().trim()
}