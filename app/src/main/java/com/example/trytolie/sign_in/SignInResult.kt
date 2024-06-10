package com.example.trytolie.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val id: String = "-1",
    val name: String? =  "",
    val email: String? = null,
    val emailVerified: Boolean = false,
    val provider: String? = null,
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val signupDate: String? = null,
)