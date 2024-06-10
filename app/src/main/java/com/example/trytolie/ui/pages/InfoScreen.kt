package com.example.trytolie.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trytolie.R
import com.example.trytolie.sign_in.SignInState

@Composable
fun InfoScreen(
    state: SignInState? = SignInState(),
    modifier: Modifier,
) {
    val scroll = rememberScrollState(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.tab_info),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Image(
            painter = painterResource(id = R.drawable.trytolie_logo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier.size(200.dp),
            //colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun InfoScreenPreview() {
    InfoScreen(modifier = Modifier)
}