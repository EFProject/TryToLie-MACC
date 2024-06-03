package com.example.trytolie.multiplayer.room

data class RoomData(
    var roomId: String = "-1",
    var gameId: String = "-1",
    val playerOneId: String = "",
    val playerTwoId: String? = null,
    val playerOneName: String = "",
    val playerTwoName: String? = null,
    val pictureUrlOne : String = "",
    val pictureUrlTwo : String? = null,
    val qrCodeUrl : String? = "",
    val roomState: RoomStatus = RoomStatus.WAITING,
)
{
    constructor() : this("-1","-1", "", null,"",null,"",null, "", RoomStatus.WAITING)
}

enum class RoomStatus {
    CREATED,                //handle creation of a room
    WAITING,                //handle FindGameScreen
    JOINED,                 //handle room is ready for start the game
    IN_PROGRESS,            //handle game development
    // FINISHED                //handle game termination
}