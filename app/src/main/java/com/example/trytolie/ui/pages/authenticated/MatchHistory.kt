package com.example.trytolie.ui.pages.authenticated

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchHistory(
    gameUIClient: GameUIClient,
    authViewModel: SignInViewModel? = null
){
    var gamesList by remember { mutableStateOf<List<GameData>>(emptyList()) }
    val userData = authViewModel?.getUserData()
    LaunchedEffect(Unit) {
        gamesList = gameUIClient.getAllGames(userData!!.id)!!
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                .size(width = 300.dp, height = 320.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Recent Games",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            if (gamesList.isEmpty()){
                item{
                    EmptyMatchList()
                }
            } else {
                for (game in gamesList) {
                    item {
                        GameRow(game, userData!!)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onPrimary,
                            thickness = 1.dp,
                            modifier = Modifier
                                .width(250.dp)
                                .padding(start = 60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMatchList(){
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .height(290.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Recent Games Found",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inversePrimary,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun GameRow(game: GameData, userData: UserData) {
    val backgroundColor = if (game.winner == userData.id) {
        MaterialTheme.colorScheme.primary
    } else if (game.winner != "") {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.inversePrimary
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(start = 8.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Icon",
            tint = Color.White,
            modifier = Modifier.padding(8.dp)
        )
        if(game.playerOneId == userData.id) VSTag(playerTwoName = game.playerTwoName)
        else VSTag(playerTwoName = game.playerOneName)
        Spacer(modifier = Modifier.width(24.dp))
        IconResultGame(game = game)
    }
}



@Composable
fun VSTag(playerTwoName: String){
    Column(
        modifier = Modifier.width(140.dp)
    ) {
        Text(text = playerTwoName, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun IconResultGame(game: GameData){
    return Text(text = "${game.playerOneDice}/${game.playerTwoDice}", color = MaterialTheme.colorScheme.onPrimary)
}
