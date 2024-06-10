package com.example.trytolie.game.model.game.controller

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.trytolie.game.model.game.MotionSensitiveButton
import com.example.trytolie.game.model.game.speechParser.SpeechParser
import com.example.trytolie.game.ui.app.ButtonSpeechToText
import com.example.trytolie.multiplayer.game.GameStatus
import com.example.trytolie.multiplayer.game.GameUIClient
import com.example.trytolie.multiplayer.game.GameViewModel
import com.example.trytolie.multiplayer.room.RoomViewModel
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.components.DiceRender
import com.example.trytolie.ui.navigation.TryToLieRoute
import kotlinx.coroutines.launch

@Composable
fun GameController(
    modifier: Modifier = Modifier,
    roomViewModel: RoomViewModel,
    gameUIClient: GameUIClient,
    gameViewModel: GameViewModel,
    userData: UserData
) {

    val gameData by gameViewModel.gameData.collectAsState()
    val lifeScope = rememberCoroutineScope()
    var updateDiceResults by remember { mutableStateOf(false) }
    var msgBE by remember { mutableStateOf("") }

    LaunchedEffect(updateDiceResults) {
        when {
            updateDiceResults -> {
                lifeScope.launch {
                    val msgBEJson = gameUIClient.updateGame(gameData)
                    msgBE = msgBEJson?.get("msg").toString()
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

            val availableDice by remember { mutableIntStateOf(
                if(userData.id == gameData.playerOneId) gameData.playerOneDice else gameData.playerTwoDice
            ) }

            when (gameData.gameState) {

                GameStatus.LIAR_PHASE -> {
                    Text(
                        text = "${gameData.declarationResults[0]} times the value ${gameData.declarationResults[1]}",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "You have $availableDice dice",
                        modifier = Modifier.padding(16.dp)
                    )
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
                    var enable = true
                    // use cases when player can't make an higher declaration
                    if(gameData.declarationResults[0] > availableDice) enable= false
                    if(gameData.declarationResults[0] == availableDice && gameData.declarationResults[1] == 6) enable= false
                    Button(
                        enabled = enable,
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

                GameStatus.RESOLVE_PHASE -> {
                    if(gameData.winner == "") {
                        Text(
                            text = msgBE,
                            modifier = Modifier.padding(16.dp)
                        )
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            // elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Then the current state of play is:\n")
                                    }
                                    append("Player 1 has ${gameData.playerOneDice} dice\n")
                                    append("Player 2 has ${gameData.playerTwoDice} dice")
                                },
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
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
                            Text(text = "Keep playing")
                        }
                    } else {
                        Text(
                            text = msgBE,
                            modifier = Modifier.padding(16.dp)
                        )
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            // elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Player ${gameData.winner} won the game!!",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            onClick = {
                                gameUIClient.exitFromGame()
                                roomViewModel.setFullViewPage(TryToLieRoute.HOME)
                            },
                            colors = ButtonDefaults.buttonColors(Color.Red) // Set the background color to red
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp, // Use an exit-related icon
                                modifier = Modifier.size(16.dp),
                                contentDescription = "Exit icon"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Exit")
                        }
                    }
                }

                GameStatus.DICE_PHASE -> {

                    var diceValues by remember { mutableStateOf(IntArray(availableDice)) }

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.inverseOnSurface),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            // elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Shake the phone to roll the dice!",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        MotionSensitiveButton(
                            onClick = {
                                diceValues = IntArray(availableDice) { (1..6).random() }
                                gameData.diceResults = diceValues.toList()
                                updateDiceResults = true
                                Log.d("MotionSensitiveButton", "Button clicked with motion detected")
                            }
                        )

                        Button(
                            onClick = {
                                diceValues = IntArray(availableDice) { (1..6).random() }
                                gameData.diceResults = diceValues.toList()
                                updateDiceResults = true
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Roll Dice Click")
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
                    DiceRender(diceValues = gameData.diceResults)

                    if(gameData.declarationResults.isNotEmpty()){
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Previous Declaration: ${gameData.declarationResults[0]} times ${gameData.declarationResults[1]}",
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                        Text(text = "Declare your dice randomly")
                    }
                    ButtonSpeechToText(setSpokenText = {textSpoken = it})
                    if (textSpoken != "") {
                        val declaration = SpeechParser.parseSpeechToDeclaration(textSpoken)
                        val checkDeclarationOutcome = SpeechParser.checkDeclaration(declaration, availableDice, gameData.declarationResults)
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "$textSpoken  $declaration",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = checkDeclarationOutcome,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        if (checkDeclarationOutcome == "OK") {
                            declaredValues = intArrayOf(declaration?.get(0).toString().toInt(), declaration?.get(1).toString().toInt())
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
