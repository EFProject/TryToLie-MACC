package com.example.trytolie.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.trytolie.R
import com.example.trytolie.sign_in.UserData

@Composable
fun CardProfile(
    userData: UserData?
){
    val painter = rememberAsyncImagePainter(R.drawable.trytolie_logo)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .size(width = 300.dp, height = 120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(start = 8.dp, top = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier.padding(end=8.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ){
                ProfileImage(painter = painter)
            }
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                if (userData?.name != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column{
                            Text(
                                text = userData.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(start=10.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.End
                        ) {
                            if(userData.provider == "Email/password"){
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = MaterialTheme.colorScheme.inversePrimary
                                )
                            }
                            if(userData.provider == "Google"){
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = "Google Icon",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = userData.email.toString(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    text = "Signup on ${userData?.signupDate?.substring(0,11)}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.inversePrimary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}




@Composable
fun ProfileImage(painter: AsyncImagePainter) {
    Surface(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
        color = Color.Transparent
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
    }
}

@Preview
@Composable
fun CardProfilePreview() {
    val userData = UserData(
        id = "1",
        name = "Username",
        email = "eugenio.facciolo00@gmail.com",
        emailVerified = false,
        provider = "Google",
        signupDate = "Thu Jun 06 12:44:53 GMT 2024"
    )

    CardProfile(userData)
}