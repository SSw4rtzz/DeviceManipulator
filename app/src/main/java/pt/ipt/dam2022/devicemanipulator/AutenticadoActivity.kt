package pt.ipt.dam2022.devicemanipulator

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        // Get conta Google sign-in do utilizador
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        auth = FirebaseAuth.getInstance()

        //Mostra nome guardado na classe Progresso
        if (googleSignInAccount != null) {
            val autenticado = Progresso(googleSignInAccount, this)
            val bemvindo = findViewById<TextView>(R.id.bemvindo)
            bemvindo.text = getString(R.string.bemvindo, autenticado.getNome())
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
            // Se o utilizador não estiver autenticado, volta à MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Ação do botão "Continuar", leva o utilizador para o último nível jogado
        btnContinuar.setOnClickListener {
            val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$nivelAtual"))
            startActivity(intent)
        }

        // Reação ao botão "Novo Jogo", limpa t0do o progresso faz o utilizador começar de novo
        val btnNovoJogo = findViewById<Button>(R.id.novoJogo)
        btnNovoJogo.setOnClickListener {
            // Verifica se o utilizador tem algum progresso guardado
            if(progresso) {
                // Mostra um alerta que pergunta ao utilizador se tem certeza de que quer apagar o progresso
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Se começar um novo jogo vai perder todo o progresso anterior. Tem a certeza de que quer começar um novo jogo?")
                    .setCancelable(false)
                    // Se o utilizador carregar no "Sim" é reencamihado para o primeiro nivel e coloca o progresso do utilizador no nível 1
                    .setPositiveButton("Sim") { _, _ ->
                        if (googleSignInAccount != null) {
                            val autenticado = Progresso(googleSignInAccount, this)
                            autenticado.guardaNivel(1)
                        }
                        val intent = Intent(this, Nivel1::class.java)
                        startActivity(intent)
                        btnContinuar.visibility = View.GONE
                    }
                    //Se o utilizador carregar em não, nada é alterado e fecha o aviso
                    .setNegativeButton("Não") { dialog, _ ->
                        dialog.cancel()
                    }
                val alert = builder.create()
                alert.show()
            //Se o utilizador não tiver progresso guardado abre o nível 1
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

        //Reação ao botão "Creditos"
        findViewById<Button>(R.id.btnCreditos).setOnClickListener{
            setContentView(R.layout.creditos)
            // ****************** BOTÃO INICIO ******************
            val btnInicio = findViewById<ImageView>(R.id.inicio)
            //Evento onClick do botão "Inicio" volta ao MainActivity
            btnInicio.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            // ****************** BOTÃO INICIO ******************
        }

        //Ver niveis
        findViewById<Button>(R.id.btnNiveis).setOnClickListener{
            setContentView(R.layout.ver_niveis)

            // ****************** BOTÃO INICIO ******************
            val btnInicio = findViewById<ImageView>(R.id.inicio)
            //Evento onClick do botão "Inicio" volta ao MainActivity
            btnInicio.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            // ****************** BOTÃO INICIO ******************

            //**** INICIO AQUISIÇÃO DAS REFERENCIAS DOS BOTÕES ****
            //Não foi conseguido encontrar id's de variáveis dinamicamente com o "val nivelBloqueado = findViewById<Button>(resources.getIdentifier(paths, "id", packageName))"
            val btnNivel1 = findViewById<Button>(R.id.btnNivel1)
            val btnNivel2 = findViewById<Button>(R.id.btnNivel2)
            val btnNivel3 = findViewById<Button>(R.id.btnNivel3)
            val btnNivel4 = findViewById<Button>(R.id.btnNivel4)
            val btnNivel5 = findViewById<Button>(R.id.btnNivel5)
            val btnNivel6 = findViewById<Button>(R.id.btnNivel6)
            val btnNivel7 = findViewById<Button>(R.id.btnNivel7)
            // Declara a variável "btnNiveis" como um mapa que associa os números dos níveis aos seus respectivos botões
            val btnNiveis = mapOf(
                1 to btnNivel1,
                2 to btnNivel2,
                3 to btnNivel3,
                4 to btnNivel4,
                5 to btnNivel5,
                6 to btnNivel6,
                7 to btnNivel7
            )
            //**** FIM AQUISIÇÃO DAS REFERENCIAS DOS BOTÕES ****
            Log.d("Debug", "NivelAtual: $nivelAtual")
            // declara variável "bloqueado" que irá receber a imagem "bloqueado" na pasta drawable
            val bloqueado = ContextCompat.getDrawable(this, R.drawable.bloqueado)
            // Verifica se o nível atual do utilizador é maior ou igual a 1
            if (nivelAtual >= 1) {
                val proxNivel = nivelAtual + 1
                // Bloqueia todos os botões dos níveis a seguir ao nível atual
                for (i in proxNivel..7) {
                    btnNiveis[i]?.isEnabled = false
                    btnNiveis[i]?.setCompoundDrawablesWithIntrinsicBounds(bloqueado, null, null, null)
                    btnNiveis[i]?.setTextColor(Color.parseColor("#666666"))
            }
                // Adiciona um "listener" aos botões dos niveis já passados e para o nivel a seguir
                for (i in 1 until proxNivel) {
                        btnNiveis[i]?.setOnClickListener {
                            val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$i"))
                            startActivity(intent)
                        }
                    }
            }
        }
    }
}