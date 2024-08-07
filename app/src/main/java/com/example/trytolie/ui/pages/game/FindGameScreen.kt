package com.example.trytolie.ui.pages.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.trytolie.R
import com.example.trytolie.game.model.game.QrScannerCompose
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.room.RoomData
import com.example.trytolie.multiplayer.room.RoomStatus
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.components.CardProfile
import com.example.trytolie.ui.components.CardProfileSearch
import com.example.trytolie.ui.navigation.TryToLieRoute
import kotlinx.coroutines.launch

@Composable
fun FindGameScreen(
    modifier: Modifier = Modifier,
    roomUIClient: RoomUIClient,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient,
    authViewModel: SignInViewModel? = null,
    userData: UserData
) {

    val roomData by roomViewModel.roomData.collectAsState()
    val lifeScope = rememberCoroutineScope()
    var hostingRoom by remember { mutableStateOf(false) }
    var creatingRoom by remember { mutableStateOf(false) }
    var findingRoom by remember { mutableStateOf(false) }
    var deletingRoom by remember { mutableStateOf(false) }
    var creatingGame by remember { mutableStateOf(false) }
    var settingGame by remember { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(R.drawable.ic_smartlogo_trytolie)

    LaunchedEffect(creatingRoom,findingRoom,deletingRoom,creatingGame,settingGame) {
        when {
            deletingRoom -> {
                lifeScope.launch {
                    roomUIClient.exitFromRoom()
                    deletingRoom = false
                }
            }
            creatingGame -> {
                lifeScope.launch {
                    gameUIClient.createGame(roomViewModel.getRoomData())
                    creatingGame = false
                }
            }
            settingGame -> {
                lifeScope.launch {
                    gameUIClient.getGame(roomViewModel.getRoomData().gameId)
                    if(hostingRoom){
                        roomUIClient.deleteRoom(roomViewModel.getRoomData())
                        hostingRoom = false
                    } else
                        roomUIClient.exitFromRoom()

                    settingGame = false
                    roomViewModel.setFullViewPage(TryToLieRoute.ONLINE_GAME)
                }
            }
            creatingRoom -> {
                lifeScope.launch {
                    roomUIClient.createRoom()
                    creatingRoom = false
                }
            }
            findingRoom -> {
                lifeScope.launch {
                    roomUIClient.findFreeRoom("")
                    findingRoom = false
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(authViewModel?.isGuest() == true)
                CardProfileSearch(userData = userData, painter = painter)
            else 
                CardProfile(userData = userData)
        }
        Spacer(modifier = Modifier.height(20.dp))
        when(roomData.roomState) {
            RoomStatus.WAITING -> {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { findingRoom = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Search icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Find a free Room")
                }
                Spacer(modifier = Modifier.height(10.dp))
                QrScannerCompose(roomUIClient = roomUIClient)

                Button(
                    onClick = {
                        hostingRoom = true
                        creatingRoom = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Create icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Create a Room")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        roomViewModel.setFullViewPage("")
                        roomViewModel.setRoomData(RoomData())
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Back icon",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Back home", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            RoomStatus.CREATED -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Waiting for opponent...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(roomData.qrCodeUrl),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { deletingRoom = true }) {
                    Icon(
                        imageVector = Icons.Default.StopCircle,
                        modifier = Modifier.size(16.dp),
                        contentDescription = "Exit icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Exit")
                }
            }
            RoomStatus.JOINED -> {
                val painterTwo = rememberAsyncImagePainter(R.drawable.ic_smartlogo_trytolie)
                Text(
                    "VS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
                CardProfileSearch(userData = UserData(name = if(userData.id == roomData.playerOneId) roomData.playerTwoName else roomData.playerOneName), painter = painterTwo)
                Spacer(modifier = Modifier.height(20.dp))
                if(hostingRoom){
                    Button(
                        onClick = {
                            creatingGame = true
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
                        Text(text = "Start Game", color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "Enter icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Waiting for host to start the game...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            RoomStatus.IN_PROGRESS -> {
                settingGame = true
            }
        }
    }
}