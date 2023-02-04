package pt.ipt.dam2022.devicemanipulator

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
//Foi usado Firebase para auxiliar na implementação da autenticação do GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pt.ipt.dam2022.devicemanipulator.niveis.*
import pt.ipt.dam2022.devicemanipulator.utilizador.Progresso

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var nivelAtual = 1
    var progresso = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount != null) {
            startActivity(Intent(this, AutenticadoActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        findViewById<Button>(R.id.gAutenticacao).setOnClickListener{
            signInGoogle()
        }

        //Reação ao botão "Creditos"
        val btnCreditos = findViewById<Button>(R.id.creditos)
        btnCreditos.setOnClickListener {
            setContentView(R.layout.creditos)
        }

        //Se não existir um nivel guardado, o botão btnContinuar não aparece
        val btnContinuar = findViewById<Button>(R.id.continuar)
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        if(sharedPref.contains("nivel_atual")) {
            nivelAtual = sharedPref.getInt("nivel_atual", 1)
            if(nivelAtual != 1) {
                Log.d("Debug", "Existe save")
                progresso = true
            } else {
                btnContinuar.visibility = View.GONE
            }
        } else {
            Log.d("Debug", "Não existe save")
            btnContinuar.visibility = View.GONE
        }

        //Reação ao botão "Novo Jogo", limpa t0do o progresso faz o utilizador começar de novo
        val btnNovoJogo = findViewById<Button>(R.id.novoJogo)
        btnNovoJogo.setOnClickListener {
            if(progresso) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Se começar um novo jogo vai perder todo o progresso anterior. Tem a certeza de que quer começar um novo jogo?")
                    .setCancelable(false)
                    .setPositiveButton("Sim") { _, _ ->
                        val editor = sharedPref.edit()
                        editor.putInt("nivel_atual", 1)
                        editor.apply()
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

        //Reação ao botão "Jogar", atualmente direcionado para o nivel3
        btnContinuar.setOnClickListener {
            //Vai para ultimo nivel jogado
            val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$nivelAtual"))
            startActivity(intent)
        }



        //Reação ao botão "Entrar", direciona o utilizador para a view Login
        val btnEntrar = findViewById<Button>(R.id.entrar)
        btnEntrar.setOnClickListener {

            //DEBUG para saves, é para apagar
            val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt("nivel_atual", 4)
            editor.apply()
            Log.d("Debug", "Ficheiro criado")

        }

        if (googleSignInAccount != null) {
            val savesManager = Progresso(googleSignInAccount, this)
            // use savesManager to save and load game data
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val conta : GoogleSignInAccount? = task.result
            if(conta != null){
                updateUI(conta)
            }
        } else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(conta: GoogleSignInAccount?){
        val credential = GoogleAuthProvider.getCredential(conta?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                val intent = Intent(this, AutenticadoActivity::class.java)
                intent.putExtra("nome", conta?.displayName)
                intent.putExtra("email", conta?.email)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
