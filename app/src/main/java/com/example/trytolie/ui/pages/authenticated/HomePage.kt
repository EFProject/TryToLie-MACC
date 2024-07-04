package com.example.trytolie.ui.pages.authenticated

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trytolie.R
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.SignInViewModel
import com.example.trytolie.ui.navigation.TryToLieRoute
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

@Composable

fun HomePage(
    modifier: Modifier = Modifier,
    authViewModel: SignInViewModel? = null,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient,
) {
    val scroll = rememberScrollState(0)
    val localContext = LocalContext.current
    val resumeGameError =  stringResource(id = R.string.resume_game)
    val lifeScope = rememberCoroutineScope()
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_smartlogo_trytolie_noborder_small),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier.size(250.dp),
        )

        Text(
            modifier = Modifier.padding(12.dp),
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                lifeScope.launch {
                    roomViewModel.setFullViewPage(TryToLieRoute.FIND_GAME)
                }
            },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .height(65.dp)
        ) {
            Icon(
                imageVector = Icons.Default.People,
                modifier = Modifier.size(16.dp),
                contentDescription = "Players icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Play 1vs1 online")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                lifeScope.launch {
                    val userData = authViewModel?.getUserData()
                    val gamesList = gameUIClient.getAllGames(userData!!.id)
                    var gameResume: JsonObject? = null
                    for (game in gamesList!!) {
                        if(game.winner == ""){
                            gameResume = gameUIClient.getGame(game.gameId)
                            if (gameResume != null) {
                                roomViewModel.setFullViewPage(TryToLieRoute.ONLINE_GAME)
                            }
                            break
                        }
                    }
                    if (gameResume == null){
                        Toast.makeText(
                            localContext,
                            resumeGameError,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.6f)
                .height(65.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RestartAlt,
                modifier = Modifier.size(16.dp),
                contentDescription = "Restart icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Resume online game")
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage(roomViewModel = RoomViewModel(),
        gameUIClient = GameUIClient(context = LocalContext.current,
        db = FirebaseFirestore.getInstance(), gameViewModel = GameViewModel())
    )
}

