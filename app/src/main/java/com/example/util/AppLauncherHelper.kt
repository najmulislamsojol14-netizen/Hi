package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.AppHubItem

object AppLauncherHelper {
    fun launchApp(context: Context, app: AppHubItem): Boolean {
        // 1. Try launching installed native Android application
        if (!app.packageName.isNullOrBlank()) {
            val pm = context.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return true
            }
        }

        // 2. Fallback to web platform in browser
        return try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.webUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open ${app.name}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
