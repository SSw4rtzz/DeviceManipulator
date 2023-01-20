package pt.ipt.dam2022.devicemanipulator.niveis

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
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
import com.google.android.material.snackbar.Snackbar
import pt.ipt.dam2022.devicemanipulator.R

class Nivel3 : AppCompatActivity() {

    private var animNoite = true
    private var animDia = false
    private var stringDica = "Experimente utilizar o brilho do telemóvel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nivel3)

        val layout = findViewById<View>(R.id.layoutNivel3)
        val btnDica = findViewById<ImageView>(R.id.dica)

        val dica = Snackbar.make(layout, stringDica, 5000)
        //Botão para mostrar uma dica
        btnDica.setOnClickListener {
            dica.show()
        }

        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        //Botão para passar para o próximo nivel
        btnProximoNivel.setOnClickListener {
            val intent = Intent(this, Nivel4::class.java)
            startActivity(intent)
        }
        btnProximoNivel.visibility = View.GONE
    }

    //Adquire o nivel de brilho do ecrã
    private val brilhoObs = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            val brilho = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            Log.d("Nivel3Debug", "Brilho: $brilho")
            updateBackgroundColor(brilho)
        }
    }

    private fun updateBackgroundColor(brilho: Int) {
        val layout = findViewById<View>(R.id.layoutNivel3)
        val btnProximoNivel = findViewById<Button>(R.id.btnProximoNivel)
        val lua = findViewById<ImageView>(R.id.lua)
        val sol = findViewById<ImageView>(R.id.sol)

        if(brilho >= 80 && !animDia) {
            val anim = ObjectAnimator.ofObject(layout, "backgroundColor", ArgbEvaluator(), Color.BLACK, Color.WHITE)
            anim.duration = 2000
            anim.start()
            animDia = true
            animNoite = false
            //cor = argbEvaluator.evaluate(brilho / 255f, Color.BLACK, Color.WHITE) as Int
        } else if (brilho < 80 && !animNoite){
            val anim = ObjectAnimator.ofObject(layout, "backgroundColor", ArgbEvaluator(), Color.WHITE, Color.BLACK)
            anim.duration = 2000
            anim.start()
            animNoite = true
            animDia = false
        }
        val h = Handler(Looper.getMainLooper())
        if(brilho == 255){
            h.postDelayed({btnProximoNivel.visibility = View.VISIBLE }, 2000)
        }

        // Máximo - (brilho / 255f) * (maxTranslationY - minTranslationY)
        val displaymetrics = resources.displayMetrics

        val yMax = displaymetrics.heightPixels - 600

        val y = 0 + (brilho / 255f) * (0 - yMax)

        val animLua = ObjectAnimator.ofFloat(lua, "translationY", lua.translationY, y)
        animLua.duration = 2000
        animLua.start()

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