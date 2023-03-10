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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.MainActivity
import pt.ipt.dam2022.devicemanipulator.R
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class Nivel1 : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var orientationSensor: Sensor
    private var orientationListener: OrientationEventListener? = null
    private var stringDica = "Experimente rodar o telemóvel"

    private lateinit var progresso: Progresso
    private lateinit var context: Context
    private var proxNivel = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel1)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        context = this //Coloca o context da activity numa variável para permitir ser usada fora do onCreate()

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
            val intent = Intent(this, Nivel2::class.java)
            startActivity(intent)
        }
        //Esconde o botão "Próximo Nivel" quando a activity é criada
        btnProximoNivel.visibility = View.GONE
        //************** BOTÃO PROXIMO NIVEL **************

        // ****************** BOTÃO INICIO ******************
        val btnInicio = findViewById<ImageView>(R.id.inicio)
        //Evento onClick do botão "Inicio" volta ao MainActivity
        btnInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            onPause()
        }
        // ****************** BOTÃO INICIO ******************

        btnProximoNivel.visibility = View.GONE

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        orientationListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
               //Verifica se a orientação está entre 160 e 200 graus, para saber se o aparelho está invertido.
                Log.d("myTag", "" + orientation)
                if (orientation in 160..200) {
                    //*************** INICIO GUARDA NIVEL ****************
                    //Se o utilizador tiver conectado com uma conta, guarda na conta, se não, guarda localmente
                    if (googleSignInAccount != null) {
                        //Verifica se existe um nivel guardado a cima do proxNivel, se houver não guarda
                        val autenticado = Progresso(googleSignInAccount, context)
                        autenticado.getNivelAtual { nivel ->
                            Log.d("Debug", "Nivel Guardado: $nivel")
                            if(nivel == null || nivel < proxNivel){
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

                    btnProximoNivel.visibility = View.VISIBLE
                    Log.d("myTag", "Virado ao contrário")
                }
            }
        }
    }

    //Ativa os sensores quando a aplicação é focada
    override fun onResume() {
        super.onResume()
        orientationListener?.enable()
    }

    //Para os sensores em pausa quando a aplicação é colocada em pausa
    override fun onPause() {
        super.onPause()
        orientationListener?.disable()
    }
}