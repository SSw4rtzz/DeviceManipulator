package pt.ipt.dam2022.devicemanipulator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import pt.ipt.dam2022.devicemanipulator.niveis.Nivel1
import pt.ipt.dam2022.devicemanipulator.niveis.Nivel4

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("myTag", "Principal")


        //Reação ao botão "Creditos"
        val btnCreditos = findViewById<Button>(R.id.creditos)
        btnCreditos.setOnClickListener {
            setContentView(R.layout.creditos)
        }

        //Reação ao botão "Jogar", atualmente direcionado para o nivel3
        val btnNivel1 = findViewById<Button>(R.id.jogar)
        btnNivel1.setOnClickListener {
            val intent = Intent(this, Nivel1::class.java)
            startActivity(intent)
        }

        //Reação ao botão "Entrar", direciona o utilizador para a view Login
        val btnEntrar = findViewById<Button>(R.id.entrar)
        btnEntrar.setOnClickListener {
            val intent = Intent(this, Nivel4::class.java)
            startActivity(intent)
        }
    }
}
