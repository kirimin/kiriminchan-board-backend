package site.kirimin_chan.board.auth

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import db.entities.Users
import io.ktor.application.Application
import site.kirimin_chan.board.isDev
import site.kirimin_chan.board.isProd
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.lang.IllegalStateException

object FirebaseAuth {

    private val token = mutableMapOf<String, FirebaseToken>()

    fun initFirebase(application: Application) {
        val firebaseKeys = when {
            application.isDev -> {
                FileInputStream("firebaseKey.json")
            }
            application.isProd -> {
                ByteArrayInputStream(System.getenv("FIREBASE_KEY").toByteArray(charset("utf-8")))
            }
            else -> {
                throw IllegalStateException("Could not distinguish the environment.")
            }
        }
        val options: FirebaseOptions = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(firebaseKeys))
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