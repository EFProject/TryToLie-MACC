package com.example.trytolie.multiplayer

data class RoomData(
    val roomId: String = "-1",
    val playerOneId: String = "",
    val playerTwoId: String? = null,
    val playerOneName: String = "",
    val playerTwoName: String? = null,
    val pictureUrlOne : String = "",
    val pictureUrlTwo : String? = null,
    val gameState: RoomStatus = RoomStatus.WAITING,
    val diceNumber: Int = 0,
    val playerOneDice: Int = 0,
    val playerTwoDice: Int = 0,
    val currentTurn: String? = null,
    val winner: String = "",
)
{
    constructor() : this("-1", "", null,"",null,"",null,RoomStatus.WAITING,0,0,0,null,"")
}

enum class RoomStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED
}

enum class GameType {
    ONLINE,
    TWO_OFFLINE,
    ONE_OFFLINE
}