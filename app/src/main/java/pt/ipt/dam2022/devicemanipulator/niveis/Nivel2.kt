package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam2022.devicemanipulator.R


class Nivel2 : AppCompatActivity() {

    //Animação em anim - zoom
    private lateinit var zoom: Animation
    //Image em drawable - circulo_nivel2
    private lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel2)


        zoom = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom);
        img = findViewById(R.id.image);
        img.startAnimation(zoom);

        val h = Handler(Looper.getMainLooper())

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val displayMetrics = DisplayMetrics()
        val display = displayManager.displays[0]
        val screenWidth = display.getBounds().width()
        val screenHeight = display.getBounds().height()


        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val x = event!!.values[0]
                val y = event.values[1]
                val z = event.values[2]
                Log.d("myTag", "" + x)
                Log.d("myTag", "" + y)
                Log.d("myTag", "" + z)

                if (z > 0.5f){
                    window.decorView.setBackgroundColor(Color.RED);
                } else if(z < -0.5f){
                    window.decorView.setBackgroundColor(Color.YELLOW);
                    if(y > -0.5f){
                        window.decorView.setBackgroundColor(Color.BLUE);
                    }
                }

                h.postDelayed({
                    //val i = Intent(applicationContext, Nivel1::class.java)
                    //startActivity(i)
                    val objectAnimatorX = ObjectAnimator.ofFloat(img, "translationX", img.x, img.x + x)
                    val objectAnimatorY = ObjectAnimator.ofFloat(img, "translationY", img.y, img.y + y)
                    val objectAnimatorZ = ObjectAnimator.ofFloat(img, "translationZ", img.z, img.z + z)
                    objectAnimatorX.duration = 1000
                    objectAnimatorY.duration = 1000
                    objectAnimatorZ.duration = 1000
                    objectAnimatorX.start()
                    objectAnimatorY.start()
                    objectAnimatorZ.start()

                }, 10000)

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Verifique se a imagem está fora da tela na horizontal
                if (img.x < 0) {
                    img.x = 0
                } else if (img.x + img.width > screenWidth) {
                    img.x = screenWidth - img.width
                }

                // Verifique se a imagem está fora da tela na vertical
                if (img.y < 0) {
                    img.y = 0
                } else if (img.y + img.height > screenHeight) {
                    img.y = screenHeight - img.height
                }
            }
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

    }
}
