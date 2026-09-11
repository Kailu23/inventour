package com.kailu.inventour

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.firebase.messaging.FirebaseMessaging
import com.kailu.inventour.navigation.WarehouseNavGraph
import com.kailu.inventour.ui.theme.InventourTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Obavijesti su uključene!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Obavijesti su isključene.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        logFcmToken()

        setContent {
            InventourTheme {
                WarehouseNavGraph()
            }
        }
    }

    private fun logFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.i("FCM", "FCM Token: $token")

            subscribeToGlobalTopic()
        }
    }

    private fun subscribeToGlobalTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("inventory_updates")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("FCM", "Successfully subscribed to global topic: inventory_updates")
                } else {
                    Log.e("FCM", "Failed to subscribe to global topic", task.exception)
                }
            }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
