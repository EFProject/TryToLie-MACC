package com.example.trytolie.ui.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInState
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.ui.components.EmailFieldComponent
import com.example.trytolie.ui.components.PasswordFieldComponent
import com.example.trytolie.ui.components.UsernameFieldComponent
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun SignUpScreen(
    state: SignInState? = SignInState(),
    modifier: Modifier,
    authHandler: AuthUIClient? = null,
    authViewModel: SignInViewModel? = null,
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

    var username by rememberSaveable(
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

    var passwordTwo by rememberSaveable(
        stateSaver = TextFieldValue.Saver,
        init = {
            mutableStateOf(
                value = TextFieldValue(
                    text = ""
                )
            )
        }
    )

    // Error variables for each field
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var usernameError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordTwoError by rememberSaveable { mutableStateOf<String?>(null) }
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{7,}$"

    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UsernameFieldComponent(
            username = username,
            onUsernameValueChange = { newValue ->
                username = newValue
                usernameError = if (!newValue.text.matches("[a-zA-Z_]+".toRegex())) {
                    "Invalid characters. Use only letters and underscores."
                } else {
                    null
                }
            },
            error = usernameError
        )
        Spacer(modifier = Modifier.size(8.dp))
        EmailFieldComponent(
            email = email,
            onEmailValueChange = { newValue ->
                email = newValue
                emailError =
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newValue.text).matches()) {
                        "Invalid email address."
                    } else {
                        null
                    }
            },
            errors = emailError
        )
        Spacer(modifier = Modifier.size(8.dp))
        PasswordFieldComponent(
            password = password,
            label = "Password",
            onPasswordValueChange = { newValue ->
                password = newValue
                passwordError = if (!Pattern.matches(passwordRegex, newValue.text)) {
                    "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character (@#\$%^&+=), no whitespaces, and be at least 7 characters long."
                } else {
                    null
                }
            },
            error = passwordError,
        )
        Spacer(modifier = Modifier.size(8.dp))
        PasswordFieldComponent(
            password = passwordTwo,
            label = "Confirm Password",
            onPasswordValueChange = { newValue ->
                passwordTwo = newValue
                passwordTwoError = if (newValue.text != password.text) {
                    "Passwords do not match."
                } else {
                    null
                }
            },
            error = passwordTwoError
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                keyboard?.hide()

                if (emailError != null && passwordError != null && passwordTwoError != null  && usernameError != null) {
                    Toast.makeText(
                        context,
                        "Please fill in all fields correctly.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@Button
                }

                authViewModel!!.viewModelScope.launch {
                    val signInResult = authHandler?.firebaseSignUpWithEmailAndPassword(
                        email.text,
                        password.text,
                        username.text
                    )
                    authViewModel.onSignInResult(
                        signInResult!!,
                        context = context!!
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                modifier = Modifier.size(16.dp),
                contentDescription = "Email icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Sign Up")
        }
    }

}

@Preview
@Composable
fun SignUpPagePreview() {
    SignUpScreen(modifier = Modifier)
}