package com.example.trytolie.multiplayer.room

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import com.example.trytolie.BuildConfig
import com.example.trytolie.R
import com.example.trytolie.sign_in.UserData
import com.example.trytolie.ui.utils.HelperRoomAPI
import com.example.trytolie.ui.utils.RoomAPI
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlin.coroutines.cancellation.CancellationException

class RoomUIClient(
    private val context: Context,
    db : FirebaseFirestore,
    private val roomViewModel: RoomViewModel,
    private val userData : UserData
) {
    private val roomRemoteService : RoomAPI = HelperRoomAPI.getInstance()
    private val token = BuildConfig.TOKEN
    private val gson = Gson()
    private val roomDbReference = getString(context,R.string.roomDbReference)
    private val dbRooms: CollectionReference = db.collection(roomDbReference)
    private var roomDataListener: ListenerRegistration? = null


    private fun fetchRoomData() {
        roomViewModel.roomData.value.apply {
            if (roomId != "-1") {
                stopListeningToRoomData()
                roomDataListener = dbRooms.document(roomId).addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w("Room fetch", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val source = if (value != null && value.metadata.hasPendingWrites()) {
                        "Local"
                    } else {
                        "Server"
                    }

                    if (value != null && value.exists()) {
                        Log.d("Room fetch", "$source data: ${value.data}")
                        val model = value.toObject(RoomData::class.java)!!
                        roomViewModel.setRoomData(model)
                    } else {
                        Log.d("Room fetch","$source data: null")
                    }
                }
            }
        }
    }

    private fun saveRoomData(model : RoomData) {
        Log.d("Room fetch","$model")
        roomViewModel.setRoomData(model)
        fetchRoomData()
    }

    fun updateRoomState(roomState: RoomStatus) {
        try {
            val room = roomViewModel.getRoomData()
            val updateData = mapOf("roomState" to roomState.toString())
            dbRooms.document(room.roomId).update(updateData)
            } catch(e: Exception) {
                e.printStackTrace()
                if(e is CancellationException) throw e
            }
    }

    fun stopListeningToRoomData() {
        roomDataListener?.remove()
    }

    suspend fun getRoom(roomId: String): JsonObject? {
        return try {
            val roomResponse =  roomRemoteService.get(token=token, id = roomId)
            val responseBody = roomResponse.body()
            if (roomResponse.isSuccessful) {
                val roomFreeData = gson.fromJson(responseBody, RoomData::class.java)
                saveRoomData(roomFreeData)
            }
            Log.d("Room Client",responseBody.toString())
            return roomResponse.body()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
    }

    suspend fun findRoom(): JsonObject? {
        return try {
            val roomDataFind = RoomData(
                playerTwoId = userData.id,
                playerTwoName = userData.name!!,
                roomState = RoomStatus.JOINED,
            )
            val data = gson.toJson(roomDataFind)
            val roomResponse = roomRemoteService.getFreeRoom(token = token, body = data)
            val responseBody = roomResponse.body()
            if (roomResponse.isSuccessful) {
                val roomFreeData = gson.fromJson(responseBody, RoomData::class.java)
                saveRoomData(roomFreeData)
            }
            Log.d("Room Client",responseBody.toString())
            return roomResponse.body()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Toast.makeText(
                context,
                "Could not find a room!",
                Toast.LENGTH_LONG
            ).show()
            saveRoomData(RoomData())
            null
        }
    }

    suspend fun createRoom() : JsonObject? {
        return try {
            val roomDataCreate = RoomData(
                playerOneId = userData.id,
                playerOneName = userData.name!!,
                roomState = RoomStatus.CREATED,
            )
            val data = gson.toJson(roomDataCreate)
            val roomResponse = roomRemoteService.create(token = token, body = data)
            val responseBody = roomResponse.body()
            if (roomResponse.isSuccessful) {
                val roomFreeData = gson.fromJson(responseBody, RoomData::class.java)
                saveRoomData(roomFreeData)
            }
            Log.d("Room Client",responseBody.toString())
            return roomResponse.body()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Toast.makeText(
                context,
                "Could not create a room!",
                Toast.LENGTH_LONG
            ).show()
            saveRoomData(RoomData())
            null
        }
    }


    suspend fun deleteRoom(model: RoomData)  {
        stopListeningToRoomData()
        roomRemoteService.delete(token=token, id = model.roomId)
        roomViewModel.setRoomData(RoomData())
    }

}