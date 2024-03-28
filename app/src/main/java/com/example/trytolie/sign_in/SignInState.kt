package com.example.trytolie.sign_in

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    var signInError: String? = null,
    var fieldType: String? = null
)
