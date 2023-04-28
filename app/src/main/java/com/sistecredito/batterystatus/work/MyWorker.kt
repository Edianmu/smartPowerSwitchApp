package com.sistecredito.batterystatus.work

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sistecredito.batterystatus.ui.theme.Constants
import com.sistecredito.batterystatus.ui.theme.Constants.BROKER_PASS
import com.sistecredito.batterystatus.ui.theme.Constants.BROKER_USER
import com.sistecredito.batterystatus.ui.theme.Constants.TOPIC_PUB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

@Suppress("BlockingMethodInNonBlockingContext")
class MyWorker(

    private val context: Context, workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private lateinit var mqttClient: MqttAndroidClient
    private var result: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        Log.d("Nivel de batería ", batteryPct.toString())
        Log.d("Conexión a energía: ", resultBattery())
        connect(context)
        Result.success()
    }

    var batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    var batteryPct: Int? = batteryStatus?.let { intent ->
        var level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        var scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        level * 100 / scale
    }

    private fun resultBattery(): String {
        if (batteryPct != null) {
            if (batteryPct!! < 20) {
                this.result = "connect"
            } else if (batteryPct!! > 98) {
                this.result = "disconnect"
            }
        }
        return this.result
    }

    ///Connect MQTT broker
    private fun connect(context: Context) {

        val serverURI = Constants.SERVER_URI //Domain
        mqttClient = MqttAndroidClient(context, serverURI, Constants.CLIENT_ID)
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(Constants.TAG, "Receive message: ${message.toString()} from topic: $topic")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(Constants.TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions()
        options.userName = BROKER_USER
        options.password = BROKER_PASS.toCharArray()

        try {

            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(Constants.TAG, Constants.CONECCTION_SUCCESS)
                    publish(TOPIC_PUB, resultBattery(), qos = 2)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(Constants.TAG, "Connection failure ${exception!!.message}")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {

            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(Constants.TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(Constants.TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}