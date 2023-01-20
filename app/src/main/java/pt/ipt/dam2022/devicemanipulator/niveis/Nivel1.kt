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
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel1 : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var orientationSensor: Sensor
    private var orientationListener: OrientationEventListener? = null
    private var stringDica = "Experimente rodar o telemóvel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel1)

        val layout = findViewById<View>(R.id.layoutNivel1)
        // ****************** BOTÃO DICA ******************
        val btnDica = findViewById<ImageView>(R.id.dica)
        val dica = Snackbar.make(layout, stringDica, 5000)
        //Evento onClick do botão "Dica" mostrando um pequeno texto no final do ecrã
        btnDica.setOnClickListener {
            dica.show()
        }
        // ****************** BOTÃO DICA ******************

        // ************** BOTÃO PROXIMO NIVEL **************
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel4::class.java)
            startActivity(intent)
        }
        //Esconde o botão "Próximo Nivel" quando a activity é criada
        btnProximoNivel.visibility = View.GONE
        //************** BOTÃO PROXIMO NIVEL **************

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