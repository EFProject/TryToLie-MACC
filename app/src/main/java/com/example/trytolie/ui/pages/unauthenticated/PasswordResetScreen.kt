package com.example.trytolie.ui.pages.unauthenticated

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trytolie.R
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInState
import com.example.trytolie.ui.components.EmailFieldComponent
import kotlinx.coroutines.launch

@Composable
fun PasswordResetScreen(
    state: SignInState? = SignInState(),
    modifier: Modifier,
    authHandler: AuthUIClient? = null,
) {
    val lyfescope = rememberCoroutineScope()
    val scroll = rememberScrollState(0)
    val context = LocalContext.current
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

    val keyboard = LocalSoftwareKeyboardController.current
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.password_reset_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.password_reset_subtitle),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                        .width(300.dp)
                        .height(50.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
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
            Button(
                onClick = {
                    keyboard?.hide()
                    if(emailError !=null) {
                        Toast.makeText(
                            context,
                            "Please insert a valid email address.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    lyfescope.launch {
                        authHandler?.sendPasswordResetEmail(email.text)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Email icon"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Send Request")
            }
    }
}

@Preview
@Composable
fun PasswordResetScreenPreview() {
    PasswordResetScreen(modifier = Modifier)
}