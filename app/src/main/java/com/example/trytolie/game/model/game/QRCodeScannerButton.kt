package com.example.trytolie.game.model.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.trytolie.multiplayer.room.RoomUIClient
import kotlinx.coroutines.launch
import qrscanner.QrScanner


@Composable
fun QrScannerCompose(
    roomUIClient: RoomUIClient,
) {
    var qrCodeURL by remember { mutableStateOf("") }
    var startBarCodeScan by remember { mutableStateOf(false) }
    var flashlightOn by remember { mutableStateOf(false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = { startBarCodeScan = true } ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            modifier = Modifier.size(16.dp),
            contentDescription = "QrCodeScanner icon"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Find a Room with QRCode")
    }
    Spacer(modifier = Modifier.height(10.dp))

    if (qrCodeURL.isEmpty() && startBarCodeScan) {
        Column(
            modifier = Modifier
                .background(color = Color.Black)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(top = 12.dp, end = 12.dp)
                        .size(24.dp)
                        .clickable {
                            startBarCodeScan = false
                        },
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(shape = RoundedCornerShape(size = 14.dp))
                    .clipToBounds()
                    .border(2.dp, Color.Gray, RoundedCornerShape(size = 14.dp)),
                contentAlignment = Alignment.Center
            ) {
                QrScanner(
                    modifier = Modifier
                        .clipToBounds()
                        .clip(shape = RoundedCornerShape(size = 14.dp)),
                    flashlightOn = flashlightOn,
                    launchGallery = launchGallery,
                    onCompletion = {
                        qrCodeURL = it
                        startBarCodeScan = false
                        coroutineScope.launch {
                            roomUIClient.findFreeRoom(it)
                        }
                    },
                    onGalleryCallBackHandler = {
                        launchGallery = it
                    },
                    onFailure = {}
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 30.dp)
                    .background(
                        color = Color(0xFFF9F9F9),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .height(35.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp, horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    Icon(imageVector = if (flashlightOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        "flash",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                flashlightOn = !flashlightOn
                            }
                    )
                }
            }
        }
    }
}
