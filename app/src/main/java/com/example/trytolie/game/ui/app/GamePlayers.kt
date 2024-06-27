package com.example.trytolie.game.ui.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.trytolie.R
import com.example.trytolie.multiplayer.game.GameData

@Composable
fun GamePlayers(gameData: GameData? = null) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(0.5f)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(modifier = Modifier.width(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_smartlogo_trytolie_noborder),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier.size(30.dp),
                )

                Text(
                    text = gameData?.playerOneName!!.split(" ")[0],
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(modifier = Modifier.width(10.dp))
                repeat(gameData!!.playerOneDice) {
                    Image(
                        painter = painterResource(id = R.drawable.dice_6),
                        contentDescription = "Dice available",
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                    text = gameData?.playerTwoName!!.split(" ")[0],
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_smartlogo_trytolie_noborder),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier.size(30.dp),
                )
                Spacer(modifier = Modifier.width(15.dp))
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Right
            ){
                repeat(gameData!!.playerTwoDice) {
                    Image(
                        painter = painterResource(id = R.drawable.dice_6),
                        contentDescription = "Dice available",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}