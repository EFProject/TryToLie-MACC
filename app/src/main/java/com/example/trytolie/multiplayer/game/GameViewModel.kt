package com.example.trytolie.multiplayer.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {

    private val _gameData = MutableStateFlow(GameData())
    val gameData : StateFlow<GameData> = _gameData.asStateFlow()

    fun getGameData(): GameData {
        return gameData.value
    }

    fun setGameData(newValue : GameData) {
        _gameData.update { newValue }
    }

}