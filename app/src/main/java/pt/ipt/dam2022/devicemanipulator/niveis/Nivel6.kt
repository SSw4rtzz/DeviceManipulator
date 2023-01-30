package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel6 : AppCompatActivity() {

    private lateinit var pisca: Animation
    private lateinit var aviao : ImageView

    var nivelConcluido = false

    private var nivelAtual = 6
    private var stringDica = "Imagina-te num avião, segue as regras"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel6)

        val layout = findViewById<View>(R.id.layoutNivel6)
        val btnDica = findViewById<ImageView>(R.id.dica)

        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }
        // ************** BOTÃO PROXIMO NIVEL **************
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        var btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel6::class.java)
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


        pisca = AnimationUtils.loadAnimation(this, R.anim.animluzes)
    }



    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val modoAviaoON = intent.getBooleanExtra("state",false)
            if (modoAviaoON){
                Log.d("Debug", "Modo voo ativado")
                val handlerThread = HandlerThread("thread1")
                handlerThread.start()
                val handler = Handler(handlerThread.looper)
                handler.postDelayed({

                    //"Acende as luzes da pista" Coloca os circulos ld e le a verde gradualmente e 2 segundos após a ativação do modo avião
                    for (i in 1..5) {
                        val idDir = "luzDir$i"
                        val ld = findViewById<ImageView>(
                            resources.getIdentifier(
                                idDir,
                                "id",
                                packageName
                            )
                        )
                        val idEsq = "luzEsq$i"
                        val le = findViewById<ImageView>(
                            resources.getIdentifier(
                                idEsq,
                                "id",
                                packageName
                            )
                        )
                        handler.postDelayed({
                            ld.setColorFilter(Color.parseColor("#0aae00"))
                            le.setColorFilter(Color.parseColor("#0aae00"))
                        }, (i - 1L) * 800)
                    }
                }, 2000)

                //Coloca as luzes da pista a piscar
                handler.postDelayed({
                for (i in 1..5) {
                    val idDir = "luzDir$i"
                    val ld = findViewById<ImageView>(
                        resources.getIdentifier(
                            idDir,
                            "id",
                            packageName
                        )
                    )
                    val idEsq = "luzEsq$i"
                    val le = findViewById<ImageView>(
                        resources.getIdentifier(
                            idEsq,
                            "id",
                            packageName
                        )
                    )
                    ld.startAnimation(pisca)
                    le.startAnimation(pisca)
                }
                }, 5500)

                //Altera a dica para o segundo passo do nível
                stringDica = "Força o avião a levantar voo"
                aviao = findViewById<ImageView>(R.id.aviao)
                aviao.setOnTouchListener { view, event ->
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            val newY = event.rawY - view.height / 2
                            view.y = newY
                            if (newY < 500) {
                                Log.d("Debug", "Nivel passado")
                                nivelConcluido = true

                                val descolagem = TranslateAnimation(0f, 0f, aviao.y, aviao.y - aviao.height)
                                descolagem.duration = 1000
                                descolagem.fillAfter = true
                                aviao.startAnimation(descolagem)

                            }
                        }
                        MotionEvent.ACTION_UP -> {

                        }
                    }
                    true
                }



            } else{
                Log.d("Debug", "Modo voo desativado")
                //Volta a colocar a dica na primeira fase do nível
                stringDica = "Imagina-te num avião, segue as regras"

                for(i in 1..5 ){
                    val idDir = "luzDir$i"
                    val ld = findViewById<ImageView>(resources.getIdentifier(idDir, "id", packageName))
                    val idEsq = "luzEsq$i"
                    val le = findViewById<ImageView>(resources.getIdentifier(idEsq, "id", packageName))
                        ld.setColorFilter(Color.parseColor("#242424"))
                        le.setColorFilter(Color.parseColor("#242424"))
                    ld.clearAnimation()
                    le.clearAnimation()
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(receiver,filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}