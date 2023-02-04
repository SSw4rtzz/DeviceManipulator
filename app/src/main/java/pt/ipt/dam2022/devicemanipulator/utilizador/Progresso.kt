package pt.ipt.dam2022.devicemanipulator.utilizador

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Progresso(private val googleSignInAccount: GoogleSignInAccount, private val context: Context) {


    private val firebaseAuth = FirebaseAuth.getInstance()
    private var userId: String? = null


    fun getNome(): String {
        return googleSignInAccount.displayName ?: "Unknown"
    }

    fun guardaNivel(nivel: Int) {
        firebaseAuth.currentUser?.let { user ->
            userId = user.uid
            val data = hashMapOf("nivel" to nivel)
            FirebaseFirestore.getInstance().collection("users").document(userId!!).set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Debug", "Nível $nivel guardado com sucesso para o utilizador $userId")
                }
                .addOnFailureListener {
                    Log.d("Debug", "Erro ao guardar o nível $nivel para o utilizador $userId")
                }
        }
    }

    fun getNivelAtual(callback: (nivel: Int?) -> Unit) {
        firebaseAuth.currentUser?.let { user ->
            userId = user.uid
            FirebaseFirestore.getInstance().collection("users").document(userId!!).get()
                .addOnSuccessListener {
                    val nivel = it.getLong("nivel")?.toInt()
                    callback(nivel)
                }
                .addOnFailureListener {
                    Log.d("Debug", "Erro ao adquirir o nível para o utilizadir $userId")
                    callback(null)
                }
        } ?: run {
            callback(null)
        }
    }
}
