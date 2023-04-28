package com.sistecredito.batterystatus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.sistecredito.batterystatus.domain.BatteryStatusScreen
import com.sistecredito.batterystatus.ui.theme.BatteryStatusTheme
import com.sistecredito.batterystatus.work.MyWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myWorkRequest = PeriodicWorkRequest.Builder(
            MyWorker::class.java,
            15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(myWorkRequest)

        setContent {
            BatteryStatusTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BatteryStatusScreen()
                }
            }
        }
    }
}

