package pt.ipt.dam2022.devicemanipulator.niveis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel6 : AppCompatActivity() {

    private lateinit var pisca: Animation
    private lateinit var aviao : ImageView
    private lateinit var btnProximoNivel : Button


    var nivelConcluido = false

    private var nivelAtual = 6
    private var stringDica = "Segue as regras de segurança de um passageiro e depois dá uma ajudinha na descolagem"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel6)

        val layout = findViewById<View>(R.id.layoutNivel6)
        val btnDica = findViewById<ImageView>(R.id.dica)

        // Cria um Snackbar com a mensagem stringDica e tempo de duração de 5000 milissegundos
        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }
        // ************** BOTÃO PROXIMO NIVEL **************
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        btnProximoNivel = findViewById(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel6::class.java)
            startActivity(intent)
        }
        //Esconde o botão "Próximo Nivel" quando a activity é criada
        btnProximoNivel.visibility = View.GONE

        //************** BOTÃO PROXIMO NIVEL **************

        //*************** INICIO GUARDA NIVEL ****************
        // Salva o nível atual em shared preferences
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("nivel_atual", nivelAtual)
        editor.apply()
        Log.d("Debug", "Save Criado $nivelAtual")
        //**************** FIM GUARDA NIVEL ****************

        // Carrega a animação "pisca"
        pisca = AnimationUtils.loadAnimation(this, R.anim.animluzes)
    }



    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            // Recebe o estado atual do modo avião
            val modoAviaoON = intent.getBooleanExtra("state",false)
            if (modoAviaoON){
                Log.d("Debug", "Modo voo ativado")

                // Inicia uma thread separada para manipular os animations
                val handlerThread = HandlerThread("thread1")
                handlerThread.start()
                val handler = Handler(handlerThread.looper)
                handler.postDelayed({

                    //"Acende as luzes da pista" gradualmente e 2 segundos após a ativação do modo avião
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
                            // Define a cor verde gradualmente para cada luz da pista
                            ld.setColorFilter(Color.parseColor("#0aae00"))
                            le.setColorFilter(Color.parseColor("#0aae00"))
                        }, (i - 1L) * 800)
                    }
                }, 2000)

                // Coloca as luzes da pista a piscar após 5.5 segundos de ativação do modo avião
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

                aviao = findViewById(R.id.aviao)
                var animationStarted = false

                // Move o avião através de um listener que lê o toque no ecrã
                aviao.setOnTouchListener { view, event ->
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            // Verifica se a animação já começou, se ainda não tiver começado, permite que o utilizador continue a mover o aviao
                            if (!animationStarted) {
                                // Move o avião para o local tocado pelo usuário
                                view.animate().y(event.rawY - view.height / 2).setDuration(0).start()
                                // Obtém a nova posição Y do avião
                                val newY = event.rawY - view.height / 2
                                // Verifica se a nova posição Y é menor que 300, se for conclui o nível
                                if (newY < 300) {
                                    Log.d("Debug", "Nivel passado")
                                    // Define a variável nivelConcluido como verdadeira
                                    nivelConcluido = true
                                    // Cria uma animação de descolagem para o avião
                                    val descolagem = TranslateAnimation(
                                        0f,
                                        0f,
                                        view.y - 300,
                                        view.y - view.height * 4
                                    )
                                    animationStarted = true
                                    // Define a duração da animação como 1 segundo
                                    descolagem.duration = 1000
                                    // Mantém o avião na posição final após a animação
                                    descolagem.fillAfter = true
                                    // Inicia a animação de descolagem do avião
                                    view.startAnimation(descolagem)
                                }
                            }
                        }
                        // Quando o utilizador para de tocar no ecrã
                        MotionEvent.ACTION_UP -> {
                            // Define a variável animationStarted como falsa
                            animationStarted = false
                            // Verifica se o nível foi concluído, se tiver sido, coloca o botão "Próximo Nivel" visivel
                            if(nivelConcluido) {
                                btnProximoNivel.visibility = View.VISIBLE
                            }
                        }
                    }
                    true
                }
            } else{
                Log.d("Debug", "Modo voo desativado")

                // Loop de 1 a 5 para alterar as propriedades de 10 imagens
                for(i in 1..5 ){
                    // Construção da identificação da circulo "luzDir" com o número atual do loop
                    val idDir = "luzDir$i"
                    val ld = findViewById<ImageView>(resources.getIdentifier(idDir, "id", packageName))
                    // Construção da identificação do circulo "luzEsq" com o número atual do loop
                    val idEsq = "luzEsq$i"
                    val le = findViewById<ImageView>(resources.getIdentifier(idEsq, "id", packageName))

                    // Alteração da cor das luzes da pista para verde
                        ld.setColorFilter(Color.parseColor("#242424"))
                        le.setColorFilter(Color.parseColor("#242424"))

                    // Limpeza das animações das imagens
                    ld.clearAnimation()
                    le.clearAnimation()
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        //Começa o listener do receiver
        registerReceiver(receiver,filter)
    }

    override fun onPause() {
        super.onPause()
        //Para o listener do receiver
        unregisterReceiver(receiver)
    }
}