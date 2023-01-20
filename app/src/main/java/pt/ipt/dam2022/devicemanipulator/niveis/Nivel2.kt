package pt.ipt.dam2022.devicemanipulator.niveis

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R


class Nivel2 : AppCompatActivity() {

    //Animação em anim - zoom
    private lateinit var zoom: Animation

    private var xPos = 0.0f
    private var yPos = 0.0f
    private var xAccel = 0.0f
    private var yAccel = 0.0f
    private var xVel = 0.0f
    private var yVel = 0.0f
    private var stringDica = "Inclina o aparelho para mover o ponto preto"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel2)

        // ************** BOTÃO PROXIMO NIVEL **************
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        //Evento onClick do botão "Próximo Nivel" levando a aplicação à activity do próximo nivel
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel3::class.java)
            startActivity(intent)
        }
        //Esconde o botão "Próximo Nivel" quando a activity é criada
        btnProximoNivel.visibility = View.GONE
        //************** BOTÃO PROXIMO NIVEL **************

        val h = Handler(Looper.getMainLooper())

        zoom = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom)
        //Image em drawable - circulo_nivel2
        val ponto = findViewById<ImageView>(R.id.ponto)

        val layout = findViewById<View>(R.id.layoutNivel2)

        // ****************** BOTÃO DICA ******************
        val btnDica = findViewById<ImageView>(R.id.dica)
        val dica = Snackbar.make(layout, stringDica, 5000)
        //Evento onClick do botão "Dica" mostrando um pequeno texto no final do ecrã
        btnDica.setOnClickListener {
            dica.show()
        }
        // ****************** BOTÃO DICA ******************

        // Circulo objetivo que preenche o ecrã quando utilizador faz o objetivo
        val circPreto = findViewById<ImageView>(R.id.circPreto)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val displaymetrics = resources.displayMetrics
        // Converter 35dp (Raio da bola branca) em pixeis, isto servirá para criar a margem de erro
        val density = displaymetrics.density
        val radius = 10 * density

        //Lê o tamanho máximo do ecrã e define até onde o ponto pode ir, 35 representa o raio da bola
        val xMax = displaymetrics.widthPixels - (radius*2)
        val yMax = displaymetrics.heightPixels - (radius*2)
        Log.d("myTag", "Altura: $xMax")
        Log.d("myTag", "Largura: $yMax")

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                xAccel = event!!.values[0]
                yAccel = -event.values[1]
                updateCirculoNivel2()
                }

            private fun updateCirculoNivel2() {
                val frameTime = 1f
                //Velocidade
                xVel += (xAccel * frameTime)
                yVel += (yAccel * frameTime)

                //Distância a que o ponto se deslocou
                val xS = (xVel / 2) * frameTime
                val yS = (yVel / 2) * frameTime

                //xPos é a posição do ponto no ecrã
                xPos -= xS
                yPos -= yS

                //Quando o ponto atinge um dos cantos do ecrã a sua velocidade é zerada, e a posição mantém-se de forma a impedir que o ponto saia do ecrã
                if (xPos > xMax/2) {
                    xPos = xMax/2
                    xVel = 0f
                } else if (xPos < 0-xMax/2) {
                    xPos = -xMax/2
                    xVel = 0f
                }

                if (yPos > yMax/2) {
                    yPos = yMax/2
                    yVel = 0f
                } else if (yPos < 0-yMax/2) {
                    yPos = -yMax/2f
                    yVel = 0f
                }

                Log.d("myTag", "Posição:")
                Log.d("myTag", "Largura: $xPos")
                Log.d("myTag", "Altura: $yPos")
                Log.d("myTag", "Tem de estar entre x:" + ((xMax/2)/1.5f + radius) + " e " + (xMax/2)/1.5f)
                Log.d("myTag", "Tem de estar entre y:" + ((yMax/2)/3 + radius) + " e " + (yMax/2)/3)


            val anim = ponto.animate().translationX(xPos).translationY(yPos)
                anim.duration = 100 //250 Default tempo em milissegundos
                anim.start()

                val xScreen = xMax / 2 + xPos
                val yScreen = yMax / 2 + yPos
                //Posição objetivo
                val raioObjetivo = 29 * density- radius
                val posObjetivox = xMax * 0.739
                val posObjetivoy = yMax * 0.23

                //Quando concluido o objetivo, bloqueia o listener do sensor e começa a animação do circPreto
                //MODO DESENVOLVIMENTO
                //if ((xPos <= xMax/1.5f) && (yPos <= yMax/3)){
                if ((xScreen <= posObjetivox + raioObjetivo && xScreen >= posObjetivox - raioObjetivo) && (yScreen <= posObjetivoy + raioObjetivo && yScreen >= posObjetivoy - raioObjetivo)){
                    //Para o listener do sensor
                        sensorManager.unregisterListener(this, accelerometer)
                        btnProximoNivel.visibility = View.VISIBLE
                        //Faz animação
                        circPreto.startAnimation(zoom)
                    //Handler para colorir o backgroud de preto após a animação acabar
                    h.postDelayed({layout.setBackgroundColor(Color.BLACK) }, 2900)
                }



                //************ MODO DESENVOLVIMENTO ****************
                //ponto.x = xScreen
                //ponto.y = yScreen


                //Metodo a funcionar 100%, mas sem animação
                //ponto.x = xPos
                //ponto.y = yPos
            }

            override fun onAccuracyChanged(sensorEvent: Sensor?, accuracy: Int) {
            }

        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }
}
