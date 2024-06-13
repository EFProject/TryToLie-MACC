package com.example.trytolie.game.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trytolie.R
import com.example.trytolie.game.model.game.controller.GameController
import com.example.trytolie.multiplayer.game.GameData
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GameOrchestrator(
    signInViewModel: SignInViewModel?,
    gameViewModel: GameViewModel,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient? = null,
    roomUIClient: RoomUIClient? = null,
    authUIClient: AuthUIClient? = null,
    userData: UserData? = null,
) {
    val showOnlineExitDialog = remember { mutableStateOf(false) }
    val gameData = gameViewModel.gameData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        GamePlayers(gameData = gameData.value,userData=userData)

        Status(gameData = gameData.value)

        GameController(
            modifier = Modifier,
            roomViewModel = roomViewModel,
            gameViewModel = gameViewModel,
            gameUIClient = gameUIClient!!,
            userData = userData!!
        )

        GameControls(
            showGameMenu = { showOnlineExitDialog.value = true },
        )

        GameDialogs(
            showOnlineExitDialog = showOnlineExitDialog,
            gameUIClient = gameUIClient,
            roomViewModel = roomViewModel,
            authUIClient = authUIClient,
            signInViewModel = signInViewModel,
            gameData = gameData.value,
            userData = userData
        )

    }
}

@Composable
private fun Status(
    gameData: GameData,
) {
    var text = ""
    if(gameData.gameState.toString() == "LIAR_PHASE")
        text = "Liar Phase"
    if(gameData.gameState.toString() == "RESOLVE_PHASE")
        text = "Resolve Phase"
    if(gameData.gameState.toString() == "DICE_PHASE")
        text = "Dice Phase"
    if(gameData.gameState.toString() == "DECLARATION_PHASE")
        text = "Declaration phase"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Turn  " + gameData.currentTurn + "   -   " + text,
            modifier = Modifier.padding(start = 12.dp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun GameControls(
    showGameMenu: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(4.dp))
        Button(
            onClick = showGameMenu,
        ) {
            Text(
                text = "Exit",
                modifier = Modifier.padding(start = 12.dp),
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = stringResource(R.string.action_game_menu)
            )
        }
    }
}


@Composable
fun OnErrorDialog(
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }){
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.LightGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "Declaration not valid. Retry.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun GameOrchestratorPreview() {
    GameOrchestrator(
        roomUIClient = RoomUIClient(
            context = LocalContext.current,
            db = FirebaseFirestore.getInstance(),
            roomViewModel = RoomViewModel(),
            userData = UserData()
        ),
        gameUIClient = GameUIClient(
            context = LocalContext.current,
            db = FirebaseFirestore.getInstance(),
            gameViewModel = GameViewModel(),
        ),
        gameViewModel = GameViewModel(),
        signInViewModel = SignInViewModel(),
        roomViewModel = RoomViewModel(),
    )
}