package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.MainActivity
import pt.ipt.dam2022.devicemanipulator.R
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class Nivel7 : AppCompatActivity() {

    private lateinit var progresso: Progresso
    private var proxNivel = 7
    private var stringDica = "Insere a distância entre o Planeta Terra e a lua e depois o ano de lançamento da Apollo 11"

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel7)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

        val layout = findViewById<View>(R.id.layoutNivel7)
        val btnDica = findViewById<ImageView>(R.id.dica)

        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }

        // ************** BOTÃO PROXIMO NIVEL **************
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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

        val distanciaLua = findViewById<TextView>(R.id.solucao1)
        val foguetao = findViewById<ImageView>(R.id.foguetao)
        val subtracao = findViewById<TextView>(R.id.subtracao)
        val apollo = findViewById<TextView>(R.id.apollo)

        //Esconde elementos da segunda parte do nivel
        distanciaLua.visibility = View.GONE
        foguetao.visibility = View.GONE
        subtracao.visibility = View.GONE
        apollo.visibility = View.GONE

        //Soluções do nivel
        val solucao1 = 384400 //Representa em quilometros a distância do Planeta Terra à lua
        val solucao2 = 1969 //Representa o ano em foi o lançamento da Apollo 11
        var solucao1Concluida = false

        //Cria a animação de movimento do foguetão do planeta para a lua
        val viagem = ObjectAnimator.ofFloat(foguetao, View.TRANSLATION_X, 0f, 450f)
        viagem.duration = 1000

        //Cria a animação de rtação do foguetão
        val rotacao = ObjectAnimator.ofFloat(foguetao, View.ROTATION, 0f, 100f)
        rotacao.duration = 1000

        val inputResposta = findViewById<EditText>(R.id.inputRespostas)

        //Lê o input e valida as respostas
        inputResposta.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val resposta = inputResposta.text.toString()
                    //Valida primeira resposta, se a respota do utilizador for igual à solução, retorna true
                    if(inputResposta.text.isNotEmpty() && resposta.toInt() == solucao1){
                        Log.d("Debug", "Parte 1: "+ resposta.toInt())
                        inputResposta.setText("")
                        solucao1Concluida = true
                        distanciaLua.visibility = View.VISIBLE
                        foguetao.visibility = View.VISIBLE
                        subtracao.visibility = View.VISIBLE
                        apollo.visibility = View.VISIBLE
                    }
                //Valida segunda resposta
                if(solucao1Concluida){
                    if(inputResposta.text.isNotEmpty() && resposta.toInt() == solucao2){
                        Log.d("Debug", "Parte 2: "+ resposta.toInt())
                        val animatorSet = AnimatorSet()
                        animatorSet.playTogether(viagem, rotacao)
                        animatorSet.start()

                        //*************** INICIO GUARDA NIVEL ****************
                        //Se o utilizador tiver conectado com uma conta, guarda na conta, se não, guarda localmente
                        if (googleSignInAccount != null) {
                            //Save na cloud
                            progresso = Progresso(googleSignInAccount, this)
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
                    }
                }
                //Para o Listener e tira o teclado do ecrã
                false
            } else {
                //Mantém o Listener e tira o teclado do ecrã
                true
            }
        }
    }
}