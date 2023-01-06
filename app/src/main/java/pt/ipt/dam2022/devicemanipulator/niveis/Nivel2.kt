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
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel2)

        //Botão para passar para o próximo nivel
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel3::class.java)
            startActivity(intent)
        }
        btnProximoNivel.visibility = View.GONE

        val h = Handler(Looper.getMainLooper())

        zoom = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom)
        //Image em drawable - circulo_nivel2
        val ponto = findViewById<ImageView>(R.id.ponto)

        val layout = findViewById<View>(R.id.layoutNivel2)
        // Circulo objetivo que preenche o ecrã quando utilizador faz o objetivo
        val circPreto = findViewById<ImageView>(R.id.circPreto)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //Lê o tamanho máximo do ecrã e define até onde o ponto pode ir
        val displaymetrics = resources.displayMetrics
        val xMax = displaymetrics.widthPixels - 200f
        val yMax = displaymetrics.heightPixels - 400f

        // Converter 35dp (Raio da bola branca) em pixeis, isto servirá para criar a margem de erro
        val density = displaymetrics.density
        val radius = 35 * density
        Log.d("myTag", "Altura: $xMax")
        Log.d("myTag", "Largura: $yMax")

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                xAccel = event!!.values[0]
                yAccel = -event.values[1]
                updateCirculoNivel2()
                //Quando concluido o objetivo, bloqueia o listener do sensor e começa a animação do circPreto
                if ((xPos <= xMax/1.5f) && (yPos <= yMax/3)){
                    //if ((xPos <= xMax/1.5f + radius && xPos >= xMax/1.5f - radius) && (yPos <= yMax/3 + radius && yPos >= yMax/3 - radius)){
                    //Para o listener do sensor
                    sensorManager.unregisterListener(this, accelerometer)
                    btnProximoNivel.visibility = View.VISIBLE
                    //Faz animação
                    circPreto.startAnimation(zoom)
                    //Handler para colorir o backgroud de preto após a animação acabar
                    h.postDelayed({layout.setBackgroundColor(Color.BLACK) }, 2900)
                }

                //Log.d("myTag", "Y: $targetY")
                //Log.d("myTag", "X: $targetX")
            }

            private fun updateCirculoNivel2() {
                val frameTime = 1f
                xVel += (xAccel * frameTime)
                yVel += (yAccel * frameTime)

                val xS = (xVel / 2) * frameTime
                val yS = (yVel / 2) * frameTime

                xPos -= xS
                yPos -= yS

                if (xPos > xMax/2) {
                    xPos = xMax/2
                } else if (xPos < 0-xMax/2) {
                    xPos = -xMax/2
                }

                if (yPos > yMax/2) {
                    yPos = yMax/2
                } else if (yPos < 0-yMax/2) {
                    yPos = -yMax/2f
                }

                xPos = xMax/1.5f
                yPos = yMax/3

                Log.d("myTag", "Largura: $xPos")
                Log.d("myTag", "Altura: $yPos")

                // Fica a piscar e muito Bugado, se o metodo animate() funcionar é para apagar
                /*val objectAnimatorX = ObjectAnimator.ofFloat(ponto, "translationX", ponto.x, xPos)
                objectAnimatorX.duration = 100
                objectAnimatorX.start()
                val objectAnimatorY = ObjectAnimator.ofFloat(ponto, "translationY", ponto.y, yPos)
                objectAnimatorY.duration = 100
                objectAnimatorY.start()*/

            //val anim = ponto.animate().translationX(xPos).translationY(yPos)
                //anim.duration = 100 //250 Default tempo em milissegundos
                //anim.start()

                //************ DEBUG ****************
                ponto.x = xMax/1.5f
                ponto.y = yMax/3


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
