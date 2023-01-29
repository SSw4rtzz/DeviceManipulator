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
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel5 : AppCompatActivity() {

    // Tempo inicial em segundos
    private var tempoInicial = 21
    // Intervalo de segundos em milissegundos, o 'L' informa o compilador que se trata de um numero do tipo long
    private var intervalo = 1000L

    private var contagem: CountDownTimer? = null
    private lateinit var contagemTxt : TextView
    private lateinit var btnProximoNivel : TextView
    var bloqueia = false

    private var nivelAtual = 5
    private var stringDica = "O melhor é não fazeres nada"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel5)

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
            val intent = Intent(this, Nivel5::class.java)
            startActivity(intent)
        }
        //Esconde o botão "Próximo Nivel" quando a activity é criada
        btnProximoNivel.visibility = View.GONE

        //************** BOTÃO PROXIMO NIVEL **************

        //*************** INICIO GUARDA NIVEL ****************
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("nivel_atual", nivelAtual)
        editor.apply()
        Log.d("Debug", "Save Criado $nivelAtual")
        //**************** FIM GUARDA NIVEL ****************

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
                    btnProximoNivel.visibility = View.VISIBLE
                    bloqueia = true
                }
            }
            contagem?.start()
        }





}