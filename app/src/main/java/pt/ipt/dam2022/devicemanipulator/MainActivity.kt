package pt.ipt.dam2022.devicemanipulator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import pt.ipt.dam2022.devicemanipulator.niveis.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var nivelAtual = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("myTag", "Principal")



        //Reação ao botão "Creditos"
        val btnCreditos = findViewById<Button>(R.id.creditos)
        btnCreditos.setOnClickListener {
            setContentView(R.layout.creditos)
        }

        //Reação ao botão "Novo Jogo", limpa t0do o progresso faz o utilizador começar de novo
        val btnNovoJogo = findViewById<Button>(R.id.novoJogo)
        btnNovoJogo.setOnClickListener {
            val intent = Intent(this, Nivel5::class.java)
            startActivity(intent)
        }

        //Reação ao botão "Jogar", atualmente direcionado para o nivel3
        val btnContinuar = findViewById<Button>(R.id.continuar)
        btnContinuar.setOnClickListener {
            //Vai para ultimo nivel jogado
            val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)

            if(sharedPref.contains("nivel_atual")) {
                Log.d("Debug", "Existe save")

                nivelAtual = sharedPref.getInt("nivel_atual", 1)
                    val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$nivelAtual"))
                    startActivity(intent)
            } else {
                Log.d("Debug", "Não existe save")
            }
        }

        //Reação ao botão "Entrar", direciona o utilizador para a view Login
        val btnEntrar = findViewById<Button>(R.id.entrar)
        btnEntrar.setOnClickListener {

            //DEBUG para saves, é para apagar
            val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt("nivel_atual", 4)
            editor.apply();
            Log.d("Debug", "Ficheiro criado")



            /*val intent = Intent(this, Nivel4::class.java)
            startActivity(intent)*/
        }
    }
}
