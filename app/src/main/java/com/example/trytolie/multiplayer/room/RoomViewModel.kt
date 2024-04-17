package com.example.trytolie.multiplayer.room

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoomViewModel: ViewModel() {
    private val _fullViewPage = MutableStateFlow("")
    val fullViewPage = _fullViewPage.asStateFlow()

    private val _roomData = MutableStateFlow(RoomData())
    val roomData : StateFlow<RoomData> = _roomData.asStateFlow()

    fun setFullViewPage(newValue : String) {
        _fullViewPage.update { newValue }
    }

    fun getRoomData(): RoomData {
        return roomData.value
    }

    fun setRoomData(newValue : RoomData) {
        _roomData.update { newValue }
    }

}