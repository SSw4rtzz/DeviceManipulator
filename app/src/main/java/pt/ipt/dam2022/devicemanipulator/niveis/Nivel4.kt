package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.MainActivity
import pt.ipt.dam2022.devicemanipulator.R
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class Nivel4 : AppCompatActivity() {

    private var animNoite = true
    private var animDia = false
    private var stringDica = "Experimente utilizar o brilho do telemóvel"
    private lateinit var progresso: Progresso
    private lateinit var context: Context
    private var proxNivel = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel4)

        context = this //Coloca o context da activity numa variável para permitir ser usada fora do onCreate()

        val layout = findViewById<View>(R.id.layoutNivel4)
        val btnDica = findViewById<ImageView>(R.id.dica)

        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }

        // ************** BOTÃO PROXIMO NIVEL **************
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel5::class.java)
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

    }

    //Adquire o nivel de brilho do ecrã
    private val brilhoObs = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            val brilho = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            Log.d("Nivel4Debug", "Brilho: $brilho")
            updateBackgroundColor(brilho)
        }
    }

    //Consoante o valor do sensor de brilho do telemóvel muda a cor de fundo da tela e provoca uma animação tanto na imagem "lua" como na "sol"
    private fun updateBackgroundColor(brilho: Int) {
        val layout = findViewById<View>(R.id.layoutNivel4)
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        val lua = findViewById<ImageView>(R.id.lua)
        val sol = findViewById<ImageView>(R.id.sol)

        // Se o brilho for maior ou igual a 80 e a animação de dia não estiver já a ser executada, cria uma animação do objeto "layout" para mudar a cor de preto para branco progressivamente
        if(brilho >= 80 && !animDia) {
            val anim = ObjectAnimator.ofObject(layout, "backgroundColor", ArgbEvaluator(), Color.BLACK, Color.WHITE)
            anim.duration = 2000
            anim.start()
            animDia = true
            animNoite = false
        // Se o brilho for menor que 80 e a animação de noite não estiver a ser executada cria uma animação do objeto "layout" para mudar a cor de branco para preto
        } else if (brilho < 80 && !animNoite){
            val anim = ObjectAnimator.ofObject(layout, "backgroundColor", ArgbEvaluator(), Color.WHITE, Color.BLACK)
            anim.duration = 2000
            anim.start()
            animNoite = true
            animDia = false
        }

        // Cria um handler usando o Looper da Thread Principal
        val h = Handler(Looper.getMainLooper())

        // Se o brilho for igual a 255, que corresponde ao máximo, o utilizador passa de nível
        if(brilho == 255){
            h.postDelayed({
                //*************** INICIO GUARDA NIVEL ****************
                val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                //Se o utilizador tiver conectado com uma conta, guarda na conta, se não, guarda localmente
                if (googleSignInAccount != null) {
                    //Verifica se existe um nivel guardado a cima do proxNivel, se houver não guarda
                    val autenticado = Progresso(googleSignInAccount, context)
                    autenticado.getNivelAtual { nivel ->
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
                btnProximoNivel.visibility = View.VISIBLE }, 2000)
        }

        // Cálculo para mover a imagem da lua e do sol no eixo do Y do ecrã
        // Máximo - (brilho / 255f) * (maxTranslationY - minTranslationY)
        val displaymetrics = resources.displayMetrics

        // Altura total da tela - 600, altura da imagem
        val yMax = displaymetrics.heightPixels - 600

        // Valor inicial é 0 e o máximo é yMax
        val y = 0 + (brilho / 255f) * (0 - yMax)

        //Inicia animação da imagem "lua"
        val animLua = ObjectAnimator.ofFloat(lua, "translationY", lua.translationY, y)
        animLua.duration = 2000
        animLua.start()

        //Inicia animação da imagem "sol"
        val animSol = ObjectAnimator.ofFloat(sol, "translationY", sol.translationY, y)
        animSol.duration = 2000
        animSol.start()
    }

    override fun onResume() {
        super.onResume()
        contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, brilhoObs)
    }

    override fun onPause() {
        super.onPause()
        contentResolver.unregisterContentObserver(brilhoObs)
    }
}