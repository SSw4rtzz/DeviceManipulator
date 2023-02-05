package pt.ipt.dam2022.devicemanipulator.utilizador

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Progresso(private val googleSignInAccount: GoogleSignInAccount, private val context: Context) {

    // Obtém a instância da autenticação do Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()
    // Variável para armazenar o ID do utilizador
    private var userId: String? = null

    // Função que retorna o nome do utilizador
    fun getNome(): String {
        return googleSignInAccount.displayName ?: "Unknown"
    }

    // Guarda o nível do utilizador na base de dados
    fun guardaNivel(nivel: Int) {
        firebaseAuth.currentUser?.let { user ->
            userId = user.uid
            val data = hashMapOf("nivel" to nivel)
            // Guarda os dados na base de dados de dados associando-os ao id do utilizador
            FirebaseFirestore.getInstance().collection("users").document(userId!!).set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Debug", "Nível $nivel guardado com sucesso para o utilizador $userId")
                }
                .addOnFailureListener {
                    Log.d("Debug", "Erro ao guardar o nível $nivel para o utilizador $userId")
                }
        }
    }

    // Retorna o nível atual do utilizador
    fun getNivelAtual(callback: (nivel: Int?) -> Unit) {
        firebaseAuth.currentUser?.let { user ->
            userId = user.uid
            // Obtem os dados do utilizador na base de dados
            FirebaseFirestore.getInstance().collection("users").document(userId!!).get()
                .addOnSuccessListener {
                    // Converte o nível para inteiro e retorna o resultado por callback
                    val nivel = it.getLong("nivel")?.toInt()
                    callback(nivel)
                }
                // Retorna null via callback em caso de falha
                .addOnFailureListener {
                    Log.d("Debug", "Erro ao adquirir o nível para o utilizadir $userId")
                    callback(null)
                }
        // Retorna null via callback caso o utilizador não esteja autenticado
        } ?: run {
            callback(null)
        }
    }
}
