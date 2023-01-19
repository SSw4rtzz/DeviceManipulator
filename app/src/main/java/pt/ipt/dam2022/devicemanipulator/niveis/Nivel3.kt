package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.ArgbEvaluator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pt.ipt.dam2022.devicemanipulator.R
import java.util.Collections.max
import java.util.Collections.min

class Nivel3 : AppCompatActivity() {

    private val argbEvaluator = ArgbEvaluator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel3)
        //Adquire o nivel de brilho do ecrã
        //val brilho = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)

    }

    private val brightnessObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            val brilho = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            Log.d("Nivel3", "Brilho: $brilho")
            updateBackgroundColor(brilho)
        }
    }

    private fun updateBackgroundColor(brilho: Int) {
        val color = argbEvaluator.evaluate(brilho / 100f, Color.BLACK, Color.WHITE) as Int
        val layout = findViewById<View>(R.id.layoutNivel3)
        layout.setBackgroundColor(color)
    }

    override fun onResume() {
        super.onResume()
        contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brightnessObserver)
    }

    override fun onPause() {
        super.onPause()
        contentResolver.unregisterContentObserver(brightnessObserver)
    }
}