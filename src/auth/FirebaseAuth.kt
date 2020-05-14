package site.kirimin_chan.board.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import db.entities.Users
import java.io.FileInputStream

object FirebaseAuth {

    private val token = mutableMapOf<String, FirebaseToken>()

    init {
        val serviceAccount = FileInputStream("firebaseKey.json")
        val options: FirebaseOptions = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://kiriminchanboard.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun getUidByToken(idToken: String): String {
        val decodedToken = if (token.containsKey(idToken)) {
            token[idToken]!!
        } else {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
            token[idToken] = decodedToken
            decodedToken
        }
        return decodedToken.uid
    }

    fun checkToken(userId: Int, requestToken: String) =
        Users.getUserById(userId).token == requestToken
}