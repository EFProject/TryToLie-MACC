package com.example.trytolie.ui.pages.authenticated

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.components.CardProfile
import com.example.trytolie.ui.components.Score
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authHandler: AuthUIClient? = null,
    authViewModel: SignInViewModel? = null,
) {
    var userData by remember { mutableStateOf(UserData()) }
    val lifeScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifeScope.launch {
            userData = authHandler?.getSignedInUser()!!
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CardProfile(userData = userData)
        Spacer(modifier = Modifier.height(16.dp))

        Score(userData = userData)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                onClick = {
                    lifeScope.launch {
                        val signInResult = authHandler?.signOut()
                        authViewModel!!.onSignInResult(
                            signInResult!!,
                            context = context
                        )
                    }
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Exit icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sign Out", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Preview
@Composable
fun ProfilePageGuestPreview() {
    ProfileScreen(
        modifier = Modifier,
    )
}