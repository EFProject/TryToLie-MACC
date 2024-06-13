package com.example.trytolie.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.example.trytolie.BuildConfig
import com.example.trytolie.ui.utils.HelperClassUser
import com.example.trytolie.ui.utils.UserAPI
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Date

class AuthUIClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val loginToggle : () -> Unit,
    private val loadingText : (s : String) -> Unit,
    private val signInViewModel: SignInViewModel
) {
    private val auth = Firebase.auth
    private val userRemoteService : UserAPI = HelperClassUser.getInstance()
    private val token = BuildConfig.TOKEN
    private val gson = Gson()

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            e.printStackTrace()
            when (e) {
                is ApiException -> {
                    Log.e("AuthUIClient", "ApiException: ${e.statusCode} - ${e.message}")
                }
                else -> {
                    Log.e("AuthUIClient", "Unknown exception: ${e.message}")
                }
            }
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun firebaseSignInWithEmailAndPassword(
        email: String, password: String
    ) = try {
        loadingText("Try to log in with email...")
        loginToggle()
        val user = auth.signInWithEmailAndPassword(email,password).await().user
        SignInResult(
            data = user?.run {
                UserData(
                    id = uid,
                    name = displayName,
                    email = email,
                )
            },
            errorMessage = null
        )
    } catch(e: Exception) {
        e.printStackTrace()
        if(e is CancellationException) throw e
        val error: String = when(e) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is FirebaseAuthInvalidUserException -> "User not found"
            else -> "Authentication failed. Please try again."
        }
        SignInResult(
            data = null,
            errorMessage = error
        )
    }

    suspend fun firebaseSignUpWithEmailAndPassword(
        email: String, password: String, username: String
    ) = try {
        loadingText("Try to create your profile...")
        loginToggle()
        val user  = auth.createUserWithEmailAndPassword(email, password).await().user
        if (user != null) {
            val data = gson.toJson(
                UserData(
                    id = user.uid,
                    name = username,
                    email = user.email,
                    provider =  "Email/password",
                    signupDate = Date(user.metadata?.creationTimestamp!!).toString()
                )
            )
            userRemoteService.create(token = token, body = data)
        }
        SignInResult(
            data = user?.run {
                UserData(
                    id = uid,
                    name = username,
                    email = email
                )
            },
            errorMessage = null
        )
    } catch(e: Exception) {
        e.printStackTrace()
        Log.e("AuthUIClient", "firebaseSignUpWithEmailAndPassword failed.", e)   // Error log message
        if(e is CancellationException) throw e
        val error: String = when(e) {
            is FirebaseAuthWeakPasswordException -> "The password is too weak. Please choose a stronger password."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format. Please enter a valid email address."
            is FirebaseAuthUserCollisionException -> "An account with this email address already exists. Please use a different email."
            else -> "Registration failed. Please try again."
        }
        SignInResult(
            data = null,
            errorMessage = error
        )
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            loadingText("Try to login with google...")
            loginToggle()
            val user = auth.signInWithCredential(googleCredentials).await().user
            if (user != null) {
                val userResponse =  userRemoteService.get(token=token,id= user.uid)
                if (userResponse.code() == 204) {
                    val data = gson.toJson(UserData(
                        id = user.uid,
                        email = user.email,
                        name = user.displayName,
                        provider =  "Google",
                        signupDate = Date(user.metadata?.creationTimestamp!!).toString()
                    )
                    )
                    userRemoteService.create(token=token, body = data)
                }
            }
            SignInResult(
                data = user?.run {
                    UserData(
                        id = uid,
                        email = email,
                        name = displayName,
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() : SignInResult = try {
        oneTapClient.signOut().await()
        auth.signOut()
        SignInResult(
            data = null,
            errorMessage = null
        )
    } catch(e: Exception) {
        e.printStackTrace()
        if(e is CancellationException) throw e
        SignInResult(
            data = null,
            errorMessage = e.message
        )
    }


    suspend fun getSignedInUser(): UserData? = auth.currentUser?.run {
        try {
            val userResponse =  userRemoteService.get(token = token,id = uid)
            Log.d("AuthUI Client",userResponse.body().toString())
            if (userResponse.code() == 200) {
                userResponse.body()
            } else {
                Toast.makeText(
                    context,
                    userResponse.message(),
                    Toast.LENGTH_LONG
                ).show()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun sendPasswordResetEmail(email: String) = try {
        auth.sendPasswordResetEmail(email).await()
        Toast.makeText(
            context,
            "Password reset email sent!",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: Exception) {
        e.printStackTrace()
        if(e is CancellationException) throw e
        Toast.makeText(
            context,
            e.message,
            Toast.LENGTH_LONG
        ).show()
    }

    suspend fun update(userData: UserData): Boolean {
        try {
            val data = gson.toJson(userData)
            userRemoteService.update(token=token, id= userData.id, body = data.toRequestBody("application/json".toMediaTypeOrNull()));
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
        return signInViewModel.setUserData(SignInResult(data = userData, errorMessage = null))
    }

    suspend fun confirmEdits(userId: String?, newEmail: String?, newUsername: String?, newAvatarFile: File?, userData: UserData): Boolean {
        val userDataObject = UserData(
            id = userId.toString(),
            email = newEmail,
            name = newUsername,
            matchesPlayed = userData.matchesPlayed,
            matchesWon = userData.matchesWon,
        )
        try {
            val data = gson.toJson(userDataObject)
            val jsonRequestBody = data.toRequestBody("application/json".toMediaTypeOrNull())

            if (newAvatarFile.toString() != "") {
                val fileRequestBody = newAvatarFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", newAvatarFile.name, fileRequestBody)
                userRemoteService.updateWithAvatar(token=token, id= userId.toString(), body = jsonRequestBody, image= filePart)
            } else {
                userRemoteService.update(token=token, id= userId.toString(), body = jsonRequestBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
        return signInViewModel.setUserData(SignInResult(data = userDataObject, errorMessage = null))
    }

    suspend fun deleteUser(userId: String?) {
        try{
            auth.currentUser?.delete()
            userRemoteService.delete(token=token, id= userId.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getProvider(): String? {
        return auth.currentUser?.providerData?.get(1)?.providerId
    }
}
