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
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.MainActivity
import pt.ipt.dam2022.devicemanipulator.R
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

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
    private lateinit var progresso: Progresso
    private lateinit var context: Context
    private var proxNivel = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel3)

        context = this //Coloca o context da activity numa variável para permitir ser usada fora do onCreate()

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

        // ****************** BOTÃO INICIO ******************
        val btnInicio = findViewById<ImageView>(R.id.inicio)
        //Evento onClick do botão "Inicio" volta ao MainActivity
        btnInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            onPause()
        }
        // ****************** BOTÃO INICIO ******************

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
            h.postDelayed({
                //*************** INICIO GUARDA NIVEL ****************
                val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                //Se o utilizador tiver conectado com uma conta, guarda na conta, se não, guarda localmente
                if (googleSignInAccount != null) {
                    //Verifica se existe um nivel guardado a cima do proxNivel, se houver não guarda
                    val autenticado = Progresso(googleSignInAccount, context)
                    autenticado.getNivelAtual { nivel ->
                        if(nivel == null || nivel <= proxNivel){
                            //Save na cloud
                            Log.d("Debug", "Guardei")
                            progresso = Progresso(googleSignInAccount, context)
                            progresso.guardaNivel(proxNivel)
                        } else {
                            Log.d("Debug", "Existe um nivel mais avançado guardado na cloud")
                        }
                    }
                } else {
                    //Save local
                    val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
                    //Verifica se existe um nivel guardado a cima do proxNivel, se houver não guarda
                    val nivelGuardo = sharedPref.getInt("nivel_atual", 1)
                    if(proxNivel > nivelGuardo) {
                        val editor = sharedPref.edit()
                        editor.putInt("nivel_atual", proxNivel)
                        editor.apply()
                        Log.d("Debug", "Save Criado $proxNivel")
                    }
                }
                //**************** FIM GUARDA NIVEL ****************
                btnProximoNivel.visibility = View.VISIBLE }, 2000)
        }

        // Atualiza os valores dos eixos x, y e z
        lastX = x
        lastY = y
        lastZ = z
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

