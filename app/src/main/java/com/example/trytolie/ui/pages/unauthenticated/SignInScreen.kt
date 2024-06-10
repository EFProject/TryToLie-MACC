package com.example.trytolie.ui.pages.unauthenticated

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.trytolie.R
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInState
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.ui.components.EmailFieldComponent
import com.example.trytolie.ui.components.PasswordFieldComponent
import com.example.trytolie.ui.navigation.TryToLieRoute
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    state: SignInState? = SignInState(),
    modifier: Modifier,
    authHandler: AuthUIClient? = null,
    authViewModel: SignInViewModel? = null,
    googleIntentLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>? = null,
    navController: NavController? = null,
    context: Context? = null
) {
    val scroll = rememberScrollState(0)
    LaunchedEffect(key1 = state!!.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    var email by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )
    var password by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailFieldComponent(
            email = email,
            onEmailValueChange = { newValue ->
                email = newValue
                state.signInError = null
            },
            errors = state.signInError
        )
        Spacer(modifier = Modifier.size(8.dp))
        PasswordFieldComponent(
            password = password,
            label = "Password",
            onPasswordValueChange = { newValue ->
                password = newValue
                state.signInError = null
            },
            error = state.signInError
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                keyboard?.hide()
                if(email.text == "") {
                    state.signInError = "Email is needed"
                    state.fieldType = "Email"
                    return@Button
                }
                if (password.text == "") {
                    state.signInError = "Password is needed"
                    state.fieldType = "Password"
                    return@Button
                }

                authViewModel!!.viewModelScope.launch {
                    val signInResult = authHandler?.firebaseSignInWithEmailAndPassword(
                        email.text,
                        password.text
                    )
                    authViewModel.onSignInResult(
                        signInResult!!,
                        context!!
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                modifier = Modifier.size(16.dp),
                contentDescription = "AccountCircle icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.size(8.dp))
        Button(onClick = {
            authViewModel?.signInAsGuest()
        }
        ) {
            Icon(
                imageVector = Icons.Default.NoAccounts,
                modifier = Modifier.size(16.dp),
                contentDescription = "Guest icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Login as Guest")
        }
        Spacer(modifier = Modifier.size(8.dp))
        Button(onClick = {
            authViewModel!!.viewModelScope.launch {
                val signInIntentSender =
                    authHandler!!.signIn()
                googleIntentLauncher!!.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender
                            ?: return@launch
                    ).build()
                )}}
        ) {
            Icon(
                painterResource(id = R.drawable.ic_google),
                modifier = Modifier.size(16.dp),
                contentDescription = "Google icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign in with Google")
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            modifier = Modifier.clickable {
                navController?.navigate(TryToLieRoute.PASSWORD_RESET) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            text = "Forgot Password?",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            modifier = Modifier.clickable {
                navController?.navigate(TryToLieRoute.SIGN_UP) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            text = "Do you want Sign Up?",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Preview
@Composable
fun SignInPagePreview() {
    SignInScreen(modifier = Modifier)
}