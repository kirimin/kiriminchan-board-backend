package site.kirimin_chan.board

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import site.kirimin_chan.board.api.request.*
import site.kirimin_chan.board.auth.FirebaseAuth
import site.kirimin_chan.board.db.KiriminchanBoardDb
import site.kirimin_chan.board.api.request.UsersApi
import site.kirimin_chan.board.exceptions.TokenCheckException
import java.lang.Exception
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson()
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { cause ->
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    install(CORS) {
        method(HttpMethod.Options)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = Duration.ofDays(1).seconds
    }

    KiriminchanBoardDb.connect(this)
    KiriminchanBoardDb.initTables()
    FirebaseAuth.initFirebase(this)

    routing {
        get("/api/getThreadsSumally") {
            val response = ThreadsApi.getThreadsSumally()
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        get("/api/getThreadDetail/{threadId}") {
            val response = try {
                ThreadsApi.getThreadDetail(call.parameters["threadid"])
            } catch (e: Exception) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.BadRequest)
                return@get
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        post("/api/createNewThread") {
            try {
                ThreadsApi.createNewThread(call.receive())
            } catch (e: TokenCheckException) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteThread") {
            try {
                ThreadsApi.deleteThread(call.receive())
            } catch (e: TokenCheckException) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/createNewComment") {
            try {
                CommentsApi.createNewComment(call.receive())
            } catch (e: TokenCheckException) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteComment") {
            try {
                CommentsApi.deleteComment(call.receive())
            } catch (e: TokenCheckException) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        get("/api/getUser/{uid}") {
            val response = try {
                UsersApi.getUser(call.parameters["uid"])
            } catch (e: Exception) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.BadRequest)
                return@get
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        post("/api/createNewUser") {
            UsersApi.createUser(call.receive())
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteUser") {
            try {
                UsersApi.deleteUser(call.receive())
            } catch (e: TokenCheckException) {
                e.printStackTrace()
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }
    }
}

@KtorExperimentalAPI
val Application.envKind
    get() = environment.config.property("ktor.environment").getString()

@KtorExperimentalAPI
val Application.isDev
    get() = envKind == "dev"

@KtorExperimentalAPI
val Application.isProd
    get() = envKind == "prod"