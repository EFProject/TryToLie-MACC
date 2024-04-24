package com.example.trytolie.game.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.trytolie.multiplayer.room.RoomData
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.AuthUIClient
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun GameOrchestrator(
    roomViewModel: RoomViewModel,
    signInViewModel: SignInViewModel?,
    gameViewModel: GameViewModel,
    gameUIClient: GameUIClient? = null,
    roomUIClient: RoomUIClient? = null,
    authUIClient: AuthUIClient? = null,
    userData: UserData? = null
) {
    val showGameDialog = remember { mutableStateOf(false) }
    val showOnlineExitDialog = remember { mutableStateOf(false) }
    val roomData = roomViewModel.roomData.collectAsState()
    val gameData = gameViewModel.gameData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        GamePlayers(gameData = gameData.value,userData=userData,roomData = roomData.value)

        Status(gameData = gameData.value, roomData = roomData.value)

        GameControls(
            showGameMenu = { showGameDialog.value = true },
            gameData = gameData.value
        )

        GameController(
            modifier = Modifier,
            roomViewModel = roomViewModel,
            roomUIClient = roomUIClient!!,
            gameViewModel = gameViewModel,
            gameUIClient = gameUIClient!!,
            userData = userData!!
        )

        GameDialogs(
            showGameDialog = showGameDialog,
            showOnlineExitDialog = showOnlineExitDialog,
            gameViewModel = gameViewModel,
            gameUIClient = gameUIClient,
            roomViewModel = roomViewModel,
            roomUIClient = roomUIClient,
            authUIClient = authUIClient,
            signInViewModel = signInViewModel,
            roomData = roomData.value,
            gameData = gameData.value,
            userData = userData
        )

    }
}

@Composable
private fun Status(
    gameData: GameData,
    roomData: RoomData
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Game: " + gameData.gameId + "Room: " + roomData.roomId,
            modifier = Modifier.padding(start = 12.dp),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun GameControls(
    showGameMenu: () -> Unit,
    gameData: GameData,
) {
    var alreadySelected by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(4.dp))
        Button(
            onClick = showGameMenu,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = stringResource(R.string.action_game_menu)
            )
        }
    }
    if(showErrorDialog){
        OnErrorDialog { showErrorDialog = false }
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

/*
@Composable
fun OnFinishedGameDialog(
    gameType : GameType,
    userData: UserData?,
    roomViewModel: RoomViewModel,
    signInViewModel: SignInViewModel?,
) {
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = isLoading) {
        coroutineScope.launch {
            delay(2000)
            isLoading = false
        }
    }

    if (userData != null){
        LaunchedEffect(Unit) {
            val matchType = gameType.toString()
            val (userIdOne, userIdTwo) = getUserIds(roomViewModel = roomViewModel, matchType = matchType, userData = userData)
            val results = null //getResults(matchType = matchType, resolution = gamePlayState.value.gameState.resolution, result = gamePlayState.value.gameState.gameMetaInfo.result, startColor = roomViewModel.startColor.value )
            val roomId = generateRandomString(10)
            //val updatedUserData = getNewUserData(userData = userData, results = results)
        }
    }

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
                    text = "gameMetaInfo.result",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "gameMetaInfo.termination",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                if(isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Updating...", color = Color.White)
                } else {
                    Button(
                        onClick = {
                            roomViewModel.setFullViewPage("")
                            isLoading = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            modifier = Modifier.size(8.dp),
                            contentDescription = "Left icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Back to Home", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun OnFinishedGameDialogOnline(
    gameType : GameType,
    userData: UserData,
    roomViewModel: RoomViewModel,
    roomUIClient: RoomUIClient?,
    authUIClient: AuthUIClient,
    roomData: RoomData,
) {

    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = isLoading) {
        coroutineScope.launch {
            delay(2000)
            isLoading = false
        }
    }

    val matchesNew = userData.matchesPlayed + 1
    val matchType = gameType.toString()
    val (userIdOne, userIdTwo) = getUserIds(roomViewModel = roomViewModel, matchType = matchType, userData = userData)
    */
/*val result = gamePlayState.value.gameState.gameMetaInfo.result
    val resolution = gamePlayState.value.gameState.resolution

    val win =
        if(resolution != Resolution.DICE){
            2
        } else if(startColor == Set.WHITE){
            if (result == "1-0") 0 else 1
        } else{
            if (result == "0-1") 0 else 1
        }

    val results = getResults(matchType = matchType, resolution = resolution, result = result, startColor = startColor )*//*


    LaunchedEffect(Unit) {
        val matchesWon = userData.matchesWon
        authUIClient.update(
            userData = userData.copy(
                matchesPlayed = matchesNew,
                //matchesWon = if (results == 1) matchesWon + 1 else matchesWon,
            )
        )

        val roomId = if(roomData.roomId == "-1") generateRandomString(10) else  roomData.roomId
    }

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
                    text = "gamePlayState.value.gameState.gameMetaInfo.result!!",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "gamePlayState.value.gameState.gameMetaInfo.termination!!",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Updated elo rank in your profile",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(15.dp))
                if(isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Updating...", color = Color.White)
                } else {
                    Button(
                        onClick = {
                            //roomUIClient!!.deleteRoom(roomData)
                            roomViewModel.setFullViewPage("")
                            isLoading = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            modifier = Modifier.size(8.dp),
                            contentDescription = "Left icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Back to Home", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
*/

/*fun getUserIds(roomViewModel: RoomViewModel, matchType: String, userData: UserData?): Pair<String, String> {
    var userIdOne = ""
    var userIdTwo = ""

    when (matchType) {
        "ONLINE" -> {
            userIdOne = roomViewModel.roomData.value.playerOneId
            userIdTwo = roomViewModel.roomData.value.playerTwoId!!
        }
        "ONE_OFFLINE" -> {
            userIdOne = userData!!.id
            userIdTwo = "AI Player"
        }
        "TWO_OFFLINE" -> {
            userIdOne = userData!!.id
            userIdTwo = "Local player"
        }
    }

    return Pair(userIdOne, userIdTwo)
}*/

/*fun getResults(matchType: String, resolution: Resolution, result: String?, startColor: Set?): Int{
    if(resolution != Resolution.CHECKMATE){
        return 2
    }

    when (matchType) {
        "ONLINE" -> {
            return if(startColor == Set.WHITE){
                if (result == "1-0") 0 else 1
            } else{ //startColor == Set.BLACK
                if (result == "0-1") 1 else 0
            }
        }
        "ONE_OFFLINE" -> {
            return if(startColor == Set.WHITE){
                if (result == "1-0") 0 else 1
            } else{ //startColor == Set.BLACK
                if (result == "1-0") 1 else 0
            }
        }
        "TWO_OFFLINE" -> {
            return if (result == "1-0") 0 else 1
        }
    }
    return 0
}*/

/*
fun getNewUserData(userData: UserData, results: Int): UserData {
    //need to check if when online, if i'm black and i won, matchesWon increase
    return if (results == 0) {
        userData.copy(matchesPlayed = userData.matchesPlayed+1, matchesWon = userData.matchesWon+1)
    } else {
        userData.copy(matchesPlayed = userData.matchesPlayed+1)
    }
}*/

@Preview
@Composable
fun GameOrchestratorPreview() {
    GameOrchestrator(
        roomViewModel = RoomViewModel(),
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
            userData = UserData()
        ),
        gameViewModel = GameViewModel(),
        signInViewModel = SignInViewModel(
        )
    )
}