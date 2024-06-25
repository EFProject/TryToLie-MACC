package com.example.trytolie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.sign_in.UserData

@Composable
fun CurrentPlayerCard(
    gameData: GameData,
    userData: UserData,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = modifier.fillMaxWidth(0.6f).padding(10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                val text = when (userData.id) {
                    gameData.currentPlayer -> " It's your turn !"
                    gameData.playerOneId -> gameData.playerTwoName.split(" ")[0] + " is playing ..."
                    else -> gameData.playerOneName.split(" ")[0] + " is playing ..."
                }

                Text(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Preview
@Composable
fun CurrentPlayerCardPreview() {
    val gameData = GameData(
        playerOneId = "",
        playerOneName = "Player1",
        playerTwoId = "",
        playerTwoName = "Player2",
    )
    val userData = UserData(
        id = "1",
        name = "Username",
        email = "username@gmail.com",
        emailVerified = false,
        provider = "google"
    )

    CurrentPlayerCard(gameData, userData, modifier= Modifier)
}