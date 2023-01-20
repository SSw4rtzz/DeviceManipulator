package pt.ipt.dam2022.devicemanipulator.niveis

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel3 : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var gifImageView: ImageView
    private val shakeThreshold = 3f
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var abanado = false
    private var abanCount = 0
    private var stringDica = "Experimente abanar o telemóvel"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel3)

        val layout = findViewById<View>(R.id.layoutNivel3)

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

        // ****************** BOTÃO DICA ******************
        val btnDica = findViewById<ImageView>(R.id.dica)
        val dica = Snackbar.make(layout, stringDica, 5000)
        //Evento onClick do botão "Dica" mostrando um pequeno texto no final do ecrã
        btnDica.setOnClickListener {
            dica.show()
        }
        // ****************** BOTÃO DICA ******************

        // Inicialização do sensor acelerometro
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        abanado = false

        gifImageView = findViewById(R.id.gatoAcordado)

        // Carrega o primeiro gif (Gato a dormir)
        loadGif()
    }

    // Valida se o telémovel já foi abanado para carregar o gif correto
    private fun loadGif() {
        if (abanado) {
            Glide.with(this)
                .asGif()
                .load(R.drawable.gatoacordado)
                .into(gifImageView)
        } else {
            Glide.with(this)
                .asGif()
                .load(R.drawable.gatodormir)
                .into(gifImageView)
        }
    }

    // Regista o listener do sensor quando a atividade é retomada
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // Remove o listener do sensor quando a atividade é pausada
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // Função executado sempre que há mudanças no sensor de acelerometro
    override fun onSensorChanged(event: SensorEvent) {
        // Recolhe os valores do acelarometro e coloca-os em variáveis
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calcula a diferença entre os valores atuais e anteriores dos eixos x, y e z
        val deltaX = kotlin.math.abs(x - lastX)
        val deltaY = kotlin.math.abs(y - lastY)
        val deltaZ = kotlin.math.abs(z - lastZ)

        // Se a diferença for maior que o threshold, adiciona 1 à variável abanCount
        if (deltaX > shakeThreshold || deltaY > shakeThreshold || deltaZ > shakeThreshold) {
            abanCount++
        }

        // Se o abanCount for 6 ou mais, considera-se que o aparelho foi abanado o suficiente
        if (abanCount >= 6) {
            abanado = true
            loadGif()

            val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
            // Mostra o botão de próximo nível depois de 2 segundos
            val h = Handler(Looper.getMainLooper())
            h.postDelayed({btnProximoNivel.visibility = View.VISIBLE }, 2000)
        }

        // Atualiza os valores dos eixos x, y e z
        lastX = x
        lastY = y
        lastZ = z
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

