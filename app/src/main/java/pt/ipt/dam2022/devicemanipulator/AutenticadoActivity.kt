package pt.ipt.dam2022.devicemanipulator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AutenticadoActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticado)

        auth = FirebaseAuth.getInstance()

        val nome = intent.getStringExtra("nome")

        val bemvindo = findViewById<TextView>(R.id.bemvindo)
        bemvindo.setText(getString(R.string.bemvindo, nome));


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