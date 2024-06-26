package com.example.trytolie.multiplayer.game

data class GameData(
    var gameId: String = "-1",
    var roomId: String = "-1",
    val playerOneId: String = "",
    val playerOneName: String = "",
    val playerTwoId: String = "",
    val playerTwoName: String = "",
    val gameState: GameStatus = GameStatus.DICE_PHASE,
    val playerOneDice: Int = 2,
    val playerTwoDice: Int = 2,
    var diceResults: List<Int> = emptyList(),
    var declarationResults: List<Int> = emptyList(),
    val currentTurn: Int = 1,
    val currentPlayer: String? = "",
    val winner: String? = "",
)
{
    constructor() : this("-1", "-1", "", "", "", "", GameStatus.DICE_PHASE ,2,2, emptyList(), emptyList(),1,"","")
}

enum class GameStatus {
    LIAR_PHASE,                 //handle liar calls
    RESOLVE_PHASE,              //resolve lair calls and possible endgame
    DICE_PHASE,                 //handle dice rolling phase
    DECLARATION_PHASE,          //handle dice declaration phase
}

data class GamesList(
    val games: List<GameData>
)