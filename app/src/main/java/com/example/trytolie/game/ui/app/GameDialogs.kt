package com.example.trytolie.game.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trytolie.game.ui.dialogs.GameDialog
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomData
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData

@Composable
fun GameDialogs(
    showGameDialog: MutableState<Boolean>,
    showOnlineExitDialog: MutableState<Boolean>,
    gameViewModel: GameViewModel,
    gameUIClient: GameUIClient? = null,
    roomViewModel: RoomViewModel,
    roomUIClient: RoomUIClient? = null,
    authUIClient: AuthUIClient? = null,
    signInViewModel: SignInViewModel? = null,
    roomData: RoomData? = null,
    gameData: GameData? = null,
    userData: UserData? = null
) {

    ManagedGameDialog(
        showGameDialog = showGameDialog,
        roomViewModel = roomViewModel,
        showOnlineExitDialog = showOnlineExitDialog
    )

    ManagedOnlineExitDialog(
        showOnlineExitDialog = showOnlineExitDialog,
        roomUIClient = roomUIClient,
        gameUIClient = gameUIClient,
        roomViewModel = roomViewModel,
        signInViewModel = signInViewModel,
        userData = userData,
        authUIClient = authUIClient,
    )

}

@Composable
fun ManagedOnlineExitDialog(
    showOnlineExitDialog : MutableState<Boolean>,
    roomViewModel: RoomViewModel,
    roomUIClient: RoomUIClient? = null,
    gameUIClient: GameUIClient?,
    userData : UserData?,
    authUIClient: AuthUIClient?,
    signInViewModel: SignInViewModel?,
) {
    if (showOnlineExitDialog.value) {
        Dialog(onDismissRequest = { null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Do you want quit the game?",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You will loose the match.",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = {
                            //val matchesNew = userData!!.matchesPlayed + 1

                           /* roomViewModel.viewModelScope.launch {
                                val matchesWon = userData.matchesWon
                                authUIClient!!.update(
                                    userData = userData.copy(
                                        matchesPlayed = matchesNew,
                                        matchesWon = matchesWon,
                                    )
                                )
                            }*/

                            gameUIClient?.exitFromGame()
                            showOnlineExitDialog.value = false
                            roomViewModel.setFullViewPage("")
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            modifier = Modifier.size(8.dp),
                            contentDescription = "quit icon",
                            tint = MaterialTheme.colorScheme.onError
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quit Game",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showOnlineExitDialog.value = false
                        }
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Resume", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ManagedGameDialog(
    showGameDialog: MutableState<Boolean>,
    showOnlineExitDialog: MutableState<Boolean>,
    roomViewModel: RoomViewModel,
) {
    if (showGameDialog.value) {

        GameDialog(
            onDismiss = {
                showGameDialog.value = false
            },
            onExitGame = {
                showGameDialog.value = false
                showOnlineExitDialog.value = true
            }
        )
    }
}