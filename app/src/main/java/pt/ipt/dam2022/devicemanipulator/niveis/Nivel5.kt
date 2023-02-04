package pt.ipt.dam2022.devicemanipulator.niveis

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.MainActivity
import pt.ipt.dam2022.devicemanipulator.R
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class Nivel5 : AppCompatActivity() {

    // Tempo inicial em segundos
    private var tempoInicial = 21
    // Intervalo de segundos em milissegundos, o 'L' informa o compilador que se trata de um numero do tipo long
    private var intervalo = 1000L

    private var contagem: CountDownTimer? = null
    private lateinit var contagemTxt : TextView
    private lateinit var btnProximoNivel : TextView
    var bloqueia = false

    private lateinit var progresso: Progresso
    private lateinit var context: Context
    private var proxNivel = 6
    private var stringDica = "O melhor é não fazeres nada"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel5)

        context = this //Coloca o context da activity numa variável para permitir ser usada fora do onCreate()

        val layout = findViewById<View>(R.id.layoutNivel5)
        val btnDica = findViewById<ImageView>(R.id.dica)

        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }

        // ************** BOTÃO PROXIMO NIVEL **************
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel6::class.java)
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
        }
        // ****************** BOTÃO INICIO ******************

        contagemTxt = findViewById(R.id.contagem)

        contagemTxt.text = "20"
        initContagem()
        layout.setOnClickListener {
            if(!bloqueia){
                 initContagem()
            }
        }
    }

        private fun initContagem(){
            contagem?.cancel()

            var tempo = tempoInicial

            contagem = object : CountDownTimer(tempoInicial * intervalo, intervalo){
                override fun onTick(millisUnitFinished: Long){
                    tempo--
                    contagemTxt.text = tempo.toString()
                }

                override fun onFinish() {
                    //*************** INICIO GUARDA NIVEL ****************
                    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
                    //Se o utilizador tiver conectado com uma conta, guarda na conta, se não, guarda localmente
                    if (googleSignInAccount != null) {
                        //Save na cloud
                        progresso = Progresso(googleSignInAccount, context)
                        progresso.guardaNivel(proxNivel)
                    } else {
                        //Save local
                        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putInt("nivel_atual", proxNivel)
                        editor.apply()
                        Log.d("Debug", "Save Criado $proxNivel")
                    }
                    //**************** FIM GUARDA NIVEL ****************
                    btnProximoNivel.visibility = View.VISIBLE
                    bloqueia = true
                }
            }
            contagem?.start()
        }
}