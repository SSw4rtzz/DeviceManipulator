package pt.ipt.dam2022.devicemanipulator

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
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

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var nivelAtual = 1
    var progresso = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get conta Google sign-in do utilizador
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        // Se o utilizador já estiver autenticado redireciona-o para a activity AutenticadoActivity
        if (googleSignInAccount != null) {
            startActivity(Intent(this, AutenticadoActivity::class.java))
        }

        // Obtém a instância da autenticação do Firebase
        auth = FirebaseAuth.getInstance()

        // Configurações de autenticação com o GoogleSignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Requisita o ID do token
            .requestEmail() // Requisita o email do usuário
            .build() // Constroi as opções de autenticação

        // Cria o cliente de autenticação do Google
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Adiciona um clique ao botão de autenticação com o Google e chama a função de autenticação, se o utilizador não tiver internet aparece um aviso
        findViewById<Button>(R.id.gAutenticacao).setOnClickListener{
            if (isConnectedToInternet()) {
            signInGoogle()
            } else {
                Toast.makeText(this, "É preciso ter uma conexão com a internet para efectuar a autenticação", Toast.LENGTH_SHORT).show()
            }
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

        //Se não existir um nivel guardado, o botão btnContinuar não aparece
        val btnContinuar = findViewById<Button>(R.id.continuar)
        val sharedPref = getSharedPreferences("game_data", Context.MODE_PRIVATE)
        //Verifica se existe a palavra "nivel_atual" está presente, se estiver é porque existe um progresso já guardado
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
            // Verifica se o utilizador tem algum progresso guardado
            if(progresso) {
                // Mostra um alerta que pergunta ao utilizador se tem certeza de que quer apagar o progresso
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Se começar um novo jogo vai perder todo o progresso anterior. Tem a certeza de que quer começar um novo jogo?")
                    .setCancelable(false)
                    // Se o utilizador carregar no "Sim" é reencamihado para o primeiro nivel e coloca o progresso do utilizador no nível 1
                    .setPositiveButton("Sim") { _, _ ->
                        val editor = sharedPref.edit()
                        editor.putInt("nivel_atual", 1)
                        editor.apply()
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

        // Ação do botão "Continuar", leva o utilizador para o último nível jogado
        btnContinuar.setOnClickListener {
            val intent = Intent(this, Class.forName("pt.ipt.dam2022.devicemanipulator.niveis.Nivel$nivelAtual"))
            startActivity(intent)
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    //Inicia o processo de autenticação com Google
    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    //Recebe o resultado da autenticação e armazena em result
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        //Verifica se o resultado foi bem sucedido
        if(result.resultCode == Activity.RESULT_OK){
            //Armazena o resultado da autenticação na variável task
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        //Verifica se a autenticação foi bem sucedida
        if(task.isSuccessful){
            val conta : GoogleSignInAccount? = task.result
            //Verifica se conta não é nula e atualiza a interface com o resultado da autenticação
            if(conta != null){
                updateUI(conta)
            }
        } else{
            //Exibe uma mensagem de erro com o resultado da autenticação
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Função para atualizar a interface após a autenticação bem-sucedida
    private fun updateUI(conta: GoogleSignInAccount?){
        // Cria uma credencial utilizando o token de ID fornecido pela conta do Google
        val credential = GoogleAuthProvider.getCredential(conta?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{
            // Verifica se o login foi bem-sucedido
            if(it.isSuccessful){
                // Criando uma Intent para a activity de AutenticadoActivity
                val intent = Intent(this, AutenticadoActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
