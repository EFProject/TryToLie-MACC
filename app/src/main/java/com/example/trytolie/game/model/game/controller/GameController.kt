package com.example.trytolie.game.model.game.controller

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.ArrowCircleRight
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
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.components.CurrentPlayerCard
import com.example.trytolie.ui.components.DiceRender
import com.example.trytolie.ui.components.DiceRenderDeclaration
import kotlinx.coroutines.launch

@Composable
fun GameController(
    modifier: Modifier = Modifier,
    gameUIClient: GameUIClient,
    gameViewModel: GameViewModel,
    userData: UserData,
    showEndGameDialog: () -> Unit,
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

    CurrentPlayerCard( gameData, userData, modifier )

    Column(
        modifier = modifier
            .fillMaxHeight(0.8f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(gameData.currentPlayer == userData.id){

            val availableDice by remember { mutableIntStateOf(
                if(userData.id == gameData.playerOneId) gameData.playerOneDice else gameData.playerTwoDice
            ) }

            when (gameData.gameState) {

                GameStatus.LIAR_PHASE -> {
                    Text(
                        text = "Player Declaration: ",
                        modifier = Modifier.padding(16.dp)
                    )
                    DiceRenderDeclaration(declarationValues = gameData.declarationResults)
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = {
                            updateDiceResults = true
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Message,
                            modifier = Modifier.size(16.dp),
                            contentDescription = "ErrorOutline icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "LIAR !")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Then the current state of play is:\n")
                                    }
                                    append("${gameData.playerOneName.split(" ")[0]} has ${gameData.playerOneDice} dice\n")
                                    append("${gameData.playerTwoName.split(" ")[0]} has ${gameData.playerTwoDice} dice")
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
                        showEndGameDialog()
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

                        DiceRenderDeclaration(listOf(availableDice,1))

                        val isVibrating by remember { mutableStateOf(true) }
                        val infiniteTransition = rememberInfiniteTransition(label = "")
                        val offsetX by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 20f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(130, 1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ), label = ""
                        )

                        Surface(
                            modifier = Modifier
                                .padding(16.dp)
                                .offset(x = if (isVibrating) offsetX.dp else 0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Shake the phone to roll the dice !",
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
                            Text(text = "Roll Dice Button")
                        }
                    }
                }

                GameStatus.DECLARATION_PHASE -> {
                    var declaredValues by remember { mutableStateOf(IntArray(2)) }
                    var textSpoken by remember { mutableStateOf("") }

                    Text(
                        text = "Outcome: ",
                        modifier = Modifier.padding(16.dp)
                    )
                    DiceRender(diceValues = gameData.diceResults)

                    if(gameData.declarationResults.isNotEmpty()){
                        Text(
                            text = "Previous Declaration: ",
                            modifier = Modifier.padding(16.dp)
                        )
                        DiceRenderDeclaration(declarationValues = gameData.declarationResults)
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
            Text(
                text = "Wait your turn...",
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
