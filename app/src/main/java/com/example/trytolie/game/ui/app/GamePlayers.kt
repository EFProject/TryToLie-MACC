package com.example.trytolie.game.ui.app

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trytolie.R
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.multiplayer.room.RoomData
import com.example.trytolie.sign_in.UserData

@Composable
fun GamePlayers(gameData: GameData? = null, roomData: RoomData? = null, userData: UserData? = null) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(0.5f)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = roomData?.playerOneName ?: "Player 1",
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.dice_icon),
                    contentDescription = "Icon RED_DICE",
                    modifier = Modifier.size(17.dp)
                )
            }
        }
        Column (
            modifier = Modifier.fillMaxWidth()
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Right
            ){
                Text(
                    text = roomData?.playerTwoName ?: "Player 2",
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.dice_icon),
                    contentDescription = "Icon BLUE_DICE",
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}