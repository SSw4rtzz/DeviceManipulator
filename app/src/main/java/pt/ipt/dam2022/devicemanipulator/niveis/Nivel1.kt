package pt.ipt.dam2022.devicemanipulator.niveis

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.widget.Button
import pt.ipt.dam2022.devicemanipulator.R

class Nivel1 : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var orientationSensor: Sensor
    private var orientationListener: OrientationEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel1)
        Log.d("myTag", "Nivel1")

        //Botão para passar para o próximo nivel, é mostrado apenas quando o nivel é concluido
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel2::class.java)
            startActivity(intent)
        }
        btnProximoNivel.visibility = View.GONE

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        orientationListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
               //Verifica se a orientação está entre 160 e 200 graus, para saber se o aparelho está invertido.
                Log.d("myTag", "" + orientation)
                if (orientation in 160..200) {
                    btnProximoNivel.visibility = View.VISIBLE
                    Log.d("myTag", "Virado ao contrário")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        orientationListener?.enable()
    }

    override fun onPause() {
        super.onPause()
        orientationListener?.disable()
    }
}