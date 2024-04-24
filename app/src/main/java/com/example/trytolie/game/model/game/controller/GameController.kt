package com.example.trytolie.game.model.game.controller

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trytolie.game.model.game.speechParser.SpeechParser
import com.example.trytolie.game.ui.app.ButtonSpeechToText
import com.example.trytolie.multiplayer.game.GameStatus
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomUIClient
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.UserData
import kotlinx.coroutines.launch

@Composable
fun GameController(
    modifier: Modifier = Modifier,
    roomUIClient: RoomUIClient,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient,
    gameViewModel: GameViewModel,
    userData: UserData
) {

    val roomData by roomViewModel.roomData.collectAsState()
    val gameData by gameViewModel.gameData.collectAsState()
    val lifeScope = rememberCoroutineScope()
    var updateDiceResults by remember { mutableStateOf(false) }

    LaunchedEffect(updateDiceResults) {
        when {
            updateDiceResults -> {
                lifeScope.launch {
                    gameUIClient.updateGame(gameData)
                    updateDiceResults = false
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            //CardProfileSearch(userData = userData, modifier = modifier, painter = painter)
        }
        Spacer(modifier = Modifier.height(20.dp))
        if(gameData.currentPlayer == userData.id || true){
            when (gameData.gameState) {

                GameStatus.LIAR_PHASE -> {
                    Button(
                        onClick = {
                            updateDiceResults = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "ErrorOutline icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "LIAR!")
                    }
                    Button(
                        onClick = {
                            gameUIClient.updateGameState(GameStatus.DICE_PHASE)
                        }) {
                        Icon(
                            imageVector = Icons.Default.ArrowCircleRight,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "ArrowCircleRight icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Play your turn")
                    }
                }

                GameStatus.DICE_PHASE -> {
                    val availableDice by remember { mutableIntStateOf(
                        if(userData.id == gameData.playerOneId) gameData.playerOneDice else gameData.playerTwoDice
                    ) }
                    var diceValues by remember { mutableStateOf(IntArray(availableDice)) }

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.inverseOnSurface),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                diceValues = IntArray(availableDice) { (1..6).random() }
                                gameData.diceResults = diceValues.toList()
                                updateDiceResults = true
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Roll Dice")
                        }
                    }
                }

                GameStatus.DECLARATION_PHASE -> {
                    var declaredValues by remember { mutableStateOf(IntArray(2)) }
                    var textSpoken by remember { mutableStateOf("") }
                    gameData.diceResults.forEachIndexed { index, diceValue ->
                        Text(
                            text = "Dice ${index + 1} Value: $diceValue",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            declaredValues = IntArray(2) { (1..6).random() }
                            gameData.declarationResults = declaredValues.toList()
                            updateDiceResults = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "ChatBubbleOutline icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Declare your dice")
                    }
                    ButtonSpeechToText(setSpokenText = {textSpoken = it})
                    if (textSpoken != "") {
                        val declaration = SpeechParser.parseSpeechToMove(textSpoken)
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = textSpoken,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = declaration ?: "Retry",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (declaration != null) {
                            declaredValues = intArrayOf(declaration[0].toString().toInt(), declaration[1].toString().toInt())
                            gameData.declarationResults = declaredValues.toList()
                            textSpoken = ""
                            updateDiceResults = true
                        }
                    }
                }
            }
        } else {
            val loadingText = "Wait your turn..."

            Text(
                text = loadingText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
