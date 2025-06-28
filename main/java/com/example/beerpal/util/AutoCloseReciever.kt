package com.example.beerpal.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoCloseReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        showAutoCloseNotification(context)
    }
}
