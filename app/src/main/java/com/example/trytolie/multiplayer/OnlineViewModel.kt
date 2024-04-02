package com.example.trytolie.multiplayer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnlineViewModel: ViewModel() {
    private val _roomData = MutableStateFlow(RoomData())
    val roomData : StateFlow<RoomData> = _roomData.asStateFlow()

    fun getRoomData(): RoomData {
        return roomData.value
    }

    fun setRoomData(newValue : RoomData) {
        _roomData.update { newValue }
    }

}