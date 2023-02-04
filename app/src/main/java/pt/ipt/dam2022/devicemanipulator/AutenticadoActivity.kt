package pt.ipt.dam2022.devicemanipulator

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import pt.ipt.dam2022.devicemanipulator.niveis.Nivel1
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class AutenticadoActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private var nivelAtual = 1
    var progresso = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticado)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        auth = FirebaseAuth.getInstance()

        //Mostra nome guardado na classe Progresso
        if (googleSignInAccount != null) {
            val autenticado = Progresso(googleSignInAccount, this)
            val bemvindo = findViewById<TextView>(R.id.bemvindo)
            bemvindo.setText(getString(R.string.bemvindo, autenticado.getNome()));
        }


        val btnContinuar = findViewById<Button>(R.id.continuar)

        //Se o utilizador estiver autenticado verifica se tem niveis guardados, se não estiver autenticado volta para a MainActivity
        if (googleSignInAccount != null) {
            val autenticado = Progresso(googleSignInAccount, this)
            autenticado.getNivelAtual { nivel ->
                //Verifica se o utilizador tem niveis guardados
                if (nivel != 1 && nivel != null) {
                    Log.d("Debug", "Existe save na cloud")
                    nivelAtual = nivel
                    progresso=true
                } else {
                    Log.d("Debug", "Não existe save na cloud")
                    btnContinuar.visibility = View.GONE
                }
            }
        } else {
            //Volta à MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnContinuar.setOnClickListener {
            //Vai para ultimo nivel jogado
            val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$nivelAtual"))
            startActivity(intent)
        }

        //Reação ao botão "Novo Jogo", limpa t0do o progresso faz o utilizador começar de novo
        val btnNovoJogo = findViewById<Button>(R.id.novoJogo)
        btnNovoJogo.setOnClickListener {
            if(progresso) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Se começar um novo jogo vai perder todo o progresso anterior. Tem a certeza de que quer começar um novo jogo?")
                    .setCancelable(false)
                    .setPositiveButton("Sim") { _, _ ->
                        if (googleSignInAccount != null) {
                            val autenticado = Progresso(googleSignInAccount, this)
                            autenticado.guardaNivel(1)
                        }
                        val intent = Intent(this, Nivel1::class.java)
                        startActivity(intent)
                        btnContinuar.visibility = View.GONE
                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        dialog.cancel()
                    }
                val alert = builder.create()
                alert.show()
            } else {
                val intent = Intent(this, Nivel1::class.java)
                startActivity(intent)
            }
        }

        //Botão sair, permite que o utilizador saia da sua conta
        findViewById<Button>(R.id.btnSair).setOnClickListener{
            auth.signOut()
            //Volta para a class MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut()
        }
    }
}