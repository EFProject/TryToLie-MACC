package com.example.trytolie.sign_in

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SignInViewModel: ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(UserAuthState(loading = true))
    val isAuthenticated : StateFlow<UserAuthState> = _isAuthenticated

    private val _userData = MutableStateFlow(SignInResult(data = null,errorMessage = null))
    val userData = _userData.asStateFlow()

    private val idGeneration: String = UUID.randomUUID().toString()
    private val shortId = idGeneration.substring(0,8)
    private val guestData = UserData(id = shortId, name = "guest$shortId")

    fun onSignInResult(result: SignInResult,context : Context) {
        _signInState.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
        _isAuthenticated.value = UserAuthState( state = if(result.data != null)  UserAuthStateType.AUTHENTICATED else UserAuthStateType.UNAUTHENTICATED ,loading = false)
        _userData.value = SignInResult(data = result.data , errorMessage = result.errorMessage)
        val toastText = if (result.data != null) {
            "Sign in successful"
        } else result.errorMessage ?: "Sign out successful"
        Toast.makeText(
            context,
            toastText,
            Toast.LENGTH_LONG
        ).show()
    }


    fun getAuthenticationState(handler: AuthUIClient) {
        viewModelScope.launch {
            var userData: UserData? = null
            if(!isGuest() && getUserData() != null) {
                userData = handler.getSignedInUser()
            }
            if (userData != null && !isGuest()) {
                _isAuthenticated.value = UserAuthState( state = UserAuthStateType.AUTHENTICATED,loading = false)
                _userData.value = SignInResult(data = userData, errorMessage = null)
                _signInState.value = SignInState(
                    isSignInSuccessful = true,
                    signInError = null
                )
            } else if (isGuest()){
                _isAuthenticated.value = UserAuthState( state = UserAuthStateType.GUEST,loading = false)
                _userData.value = SignInResult(data = guestData, errorMessage = null)
                _signInState.value = SignInState(
                    isSignInSuccessful = true,
                    signInError = null
                )
            } else {
                _isAuthenticated.value = UserAuthState( state = UserAuthStateType.UNAUTHENTICATED,loading = false)
                _userData.value = SignInResult(data = null, errorMessage = null)
                _signInState.value = SignInState(
                    isSignInSuccessful = false,
                    signInError = null
                )
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _signInState.value = SignInState(
                isSignInSuccessful = true,
                signInError = null
            )
            _isAuthenticated.value = UserAuthState(state = UserAuthStateType.GUEST, loading = false)
            _userData.value = SignInResult(data = guestData , errorMessage = null)
        }
    }

    fun signOutFromGuest() {
        viewModelScope.launch {
            _isAuthenticated.value = UserAuthState(state = UserAuthStateType.UNAUTHENTICATED, loading = false)
        }
    }

    fun isGuest(): Boolean {
        return _isAuthenticated.value.state == UserAuthStateType.GUEST
    }

    fun getUserData(): UserData? {
        return _userData.value.data
    }

    fun setUserData(newValue : SignInResult) : Boolean {
        _userData.update { newValue }
        return true
    }

}