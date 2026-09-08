package com.kailu.inventour.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String? = null
)
