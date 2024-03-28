package com.example.trytolie.sign_in

data class UserAuthState(
    val state: UserAuthStateType = UserAuthStateType.UNDEFINED,
    val loading: Boolean = false,
    val error: String? = null
)

enum class UserAuthStateType {
    UNAUTHENTICATED,
    AUTHENTICATED,
    UNDEFINED,
    GUEST
}