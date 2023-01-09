package pt.ipt.dam2022.devicemanipulator.niveis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import pt.ipt.dam2022.devicemanipulator.R

class Nivel3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel3)
        //Adquire o nivel de brilho do ecrã
        val brilho = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)

        Log.d("Nivel3", "Nivel de brilho: $brilho")
    }
}