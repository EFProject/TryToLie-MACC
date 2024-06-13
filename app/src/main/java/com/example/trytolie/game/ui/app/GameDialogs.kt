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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.navigation.TryToLieRoute
import kotlinx.coroutines.launch

@Composable
fun GameDialogs(
    showOnlineExitDialog: MutableState<Boolean>,
    gameUIClient: GameUIClient? = null,
    roomViewModel: RoomViewModel,
    authUIClient: AuthUIClient? = null,
    signInViewModel: SignInViewModel? = null,
    gameData: GameData? = null,
    userData: UserData? = null
) {

    ManagedOnlineExitDialog(
        showOnlineExitDialog = showOnlineExitDialog,
        gameUIClient = gameUIClient,
        roomViewModel = roomViewModel,
        signInViewModel = signInViewModel,
        gameData = gameData,
        userData = userData,
        authUIClient = authUIClient,
    )

}

@Composable
fun ManagedOnlineExitDialog(
    showOnlineExitDialog : MutableState<Boolean>,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient?,
    gameData: GameData? = null,
    userData : UserData?,
    authUIClient: AuthUIClient?,
    signInViewModel: SignInViewModel?,
) {
    val lifeScope = rememberCoroutineScope()

    if (showOnlineExitDialog.value) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Do you really want to exit ?",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "You will loose the match",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(13.dp))

                        Button(
                            onClick = {
                                lifeScope.launch {
                                    gameUIClient?.exitFromGame(
                                        gameId = gameData!!.gameId,
                                        playerId = userData!!.id
                                    )
                                }
                                showOnlineExitDialog.value = false
                                roomViewModel.setFullViewPage(TryToLieRoute.HOME)
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                modifier = Modifier.size(15.dp),
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

                        Button(
                            onClick = {
                                showOnlineExitDialog.value = false
                            }
                        ) {
                            Text(text = "Back to Game", style = MaterialTheme.typography.bodySmall)
                        }

                }
            }
        }
    }
}