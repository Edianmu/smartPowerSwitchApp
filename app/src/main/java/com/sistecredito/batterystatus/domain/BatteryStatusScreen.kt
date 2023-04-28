package com.sistecredito.batterystatus.domain

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sistecredito.batterystatus.R

@Composable
fun BatteryStatusScreen(

    batteryStatusViewModel: BatteryStatusViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    )
    {
        Text(
            text = "Prototipo de fuente de poder inteligente",
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier                .padding(top = 80.dp),
            fontWeight = FontWeight.Bold        )
        Text(
            text = "Granja de dispositivos",
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
        Image(
            painter = painterResource(id = R.drawable.farm),
            contentDescription = null,
            modifier = Modifier                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.width(30.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier                .fillMaxWidth()
        )

}
}