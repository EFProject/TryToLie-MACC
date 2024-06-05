package com.example.trytolie.multiplayer.game

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import com.example.trytolie.BuildConfig
import com.example.trytolie.R
import com.example.trytolie.multiplayer.room.RoomData
import com.example.trytolie.ui.utils.GameAPI
import com.example.trytolie.ui.utils.HelperGameAPI
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlin.coroutines.cancellation.CancellationException

class GameUIClient(
    private val context: Context,
    db : FirebaseFirestore,
    private val gameViewModel: GameViewModel,
) {
    private val gameRemoteService : GameAPI = HelperGameAPI.getInstance()
    private val token = BuildConfig.TOKEN
    private val gson = Gson()
    private val gameDbReference = getString(context,R.string.gameDbReference)
    private val dbGames: CollectionReference = db.collection(gameDbReference)
    private var gameDataListener: ListenerRegistration? = null


    private fun fetchGameData() {
        gameViewModel.gameData.value.apply {
            if (gameId != "-1") {
                stopListeningToGameData()
                gameDataListener = dbGames.document(gameId).addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w("Game fetch", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val source = if (value != null && value.metadata.hasPendingWrites()) {
                        "Local"
                    } else {
                        "Server"
                    }

                    if (value != null && value.exists()) {
                        Log.d("Game fetch", "$source data: ${value.data}")
                        val model = value.toObject(GameData::class.java)!!
                        gameViewModel.setGameData(model)
                    } else {
                        Log.d("Game fetch","$source data: null")
                    }
                }
            }
        }
    }

    private fun saveGameData(model : GameData) {
        Log.d("Game fetch","$model")
        gameViewModel.setGameData(model)
        fetchGameData()
    }

    fun updateGameState(gameState: GameStatus) {
        try {
            val game = gameViewModel.getGameData()
            val emptyResults: List<Int> = emptyList()
            val updateData = mapOf(
                "currentTurn" to game.currentTurn + 1,
                "gameState" to gameState.toString(),
                //"declarationResults" to emptyResults,
                "diceResults" to emptyResults
            )
            dbGames.document(game.gameId).update(updateData)
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    suspend fun updateGame(model : GameData): JsonObject? {
        return try {
            val data = gson.toJson(model)
            val gameResponse = gameRemoteService.update(token = token, id = model.gameId, body = data)
            val responseBody = gameResponse.body()
            Log.d("Game Client",responseBody.toString())
            responseBody
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
    }

    private fun stopListeningToGameData() {
        Log.d("Game Client", "gameDataListener has been closed")
        gameDataListener?.remove()
    }

    suspend fun getGame(gameId: String): JsonObject? {
        return try {
            val gameResponse =  gameRemoteService.get(token=token, id = gameId)
            val responseBody = gameResponse.body()
            if (gameResponse.isSuccessful) {
                val gameData = gson.fromJson(responseBody, GameData::class.java)
                saveGameData(gameData)
            }
            Log.d("Game Client",responseBody.toString())
            return gameResponse.body()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
    }

    suspend fun createGame(roomData: RoomData): Boolean {
        return try {
            val gameResponse = gameRemoteService.create(token = token, id = roomData.roomId)
            val responseBody = gameResponse.body()
            Log.d("Game Client",responseBody.toString())
            gameResponse.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Toast.makeText(
                context,
                "Could not create a game!",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }


    fun exitFromGame()  {
        stopListeningToGameData()
        // gameRemoteService.delete(token=token, id = model.roomId)
        gameViewModel.setGameData(GameData())
    }

}