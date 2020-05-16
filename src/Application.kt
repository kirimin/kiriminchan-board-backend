package site.kirimin_chan.board

import db.entities.Threads
import db.entities.Users
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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import site.kirimin_chan.board.api.request.*
import site.kirimin_chan.board.auth.FirebaseAuth
import site.kirimin_chan.board.db.KiriminchanBoardDb
import site.kirimin_chan.board.entities.*
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

    KiriminchanBoardDb.connect()
    KiriminchanBoardDb.initTables()
    FirebaseAuth.initFirebase(this)

    routing {
        get("/api/getThreadsSumally") {
            val response = transaction {
                Threads.getAllThread().map {
                    it.comments = Comments.getByThreadId(it.threadId)
                    it
                }.filter {
                    it.comments.isNotEmpty()
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        get("/api/getThreadDetail/{threadId}") {
            val params = call.parameters
            val threadId =
                params["threadid"]?.toIntOrNull() ?: throw IllegalArgumentException("param threadid must not empty.")
            val response = transaction {
                Threads.getByThreadId(threadId).let {
                    it.comments = Comments.getByThreadId(it.threadId)
                    it
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        post("/api/createNewThread") {
            val request = call.receive<CreateNewThreadRequest>()
            if (!FirebaseAuth.checkToken(request.createdUserId, request.token)) {
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            transaction {
                val newThreadId = Threads.insert {
                    it[createdUserId] = request.createdUserId
                    it[title] = request.title
                    it[createdAt] = DateTime()
                    it[updatedAt] = DateTime()
                } get Threads.threadId
                Comments.insert {
                    it[threadId] = newThreadId
                    it[createdUserId] = request.createdUserId
                    it[commentNumber] = 1
                    it[text] = request.text
                    it[Threads.createdAt] = DateTime()
                    it[Threads.updatedAt] = DateTime()
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteThread") {
            val request = call.receive<DeleteThreadRequest>()
            if (!FirebaseAuth.checkToken(request.userId, request.token)) {
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            transaction {
                Threads.deleteWhere { Threads.threadId eq request.threadId }
                Comments.deleteWhere { Comments.threadId eq request.threadId }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/createNewComment") {
            val request = call.receive<CreateNewCommentRequest>()
            if (!FirebaseAuth.checkToken(request.createdUserId, request.token)) {
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            transaction {
                Comments.insert {
                    it[threadId] = request.threadId
                    it[createdUserId] = request.createdUserId
                    it[commentNumber] = getLastCommentNumber(request.threadId).commentNumber + 1
                    it[text] = request.text
                    it[Threads.createdAt] = DateTime()
                    it[Threads.updatedAt] = DateTime()
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteComment") {
            val request = call.receive<DeleteCommentRequest>()
            if (!FirebaseAuth.checkToken(request.userId, request.token)) {
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            transaction {
                Comments.deleteWhere { Comments.commentId eq request.commentId }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        get("/api/getUser/{uid}") {
            val params = call.parameters
            val uid =
                params["uid"] ?: throw IllegalArgumentException("param uid must not empty.")
            val response = transaction {
                Users.select { Users.firebaseUid eq uid }.map {
                    Users.getUserById(it[Users.userId])
                }.first()
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(response)
        }

        post("/api/createNewUser") {
            val request = call.receive<CreateNewUserRequest>()
            transaction {
                Users.insert {
                    it[screenName] = request.name
                    it[firebaseUid] = request.firebaseUid
                    it[iconUrl] = ""
                    it[isDeleted] = '0'
                    it[isAdmin] = '0'
                    it[twitterId] = ""
                    it[createdAt] = DateTime()
                    it[updatedAt] = DateTime()
                    it[token] = request.token
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/updateUserToken") {
            val request = call.receive<UpdateUserTokenRequest>()
            val uId = FirebaseAuth.getUidByToken(idToken = request.token)
            if (uId != request.uid) {
                call.response.status(HttpStatusCode.BadRequest)
                return@post
            }
            transaction {
                Users.update(where = { Users.firebaseUid eq request.uid }) {
                    it[token] = request.token
                }
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }

        post("/api/deleteUser") {
            val request = call.receive<DeleteUserRequest>()
            if (!FirebaseAuth.checkToken(request.userId, request.token)) {
                call.response.status(HttpStatusCode.Unauthorized)
                return@post
            }
            transaction {
                Users.update(where = { Users.userId eq request.userId }, body = {
                    it[isDeleted] = '1'
                })
            }
            call.response.status(HttpStatusCode.OK)
            call.respond(mapOf("status" to "OK"))
        }
    }
}

val DEF_FMT: DateTimeFormatter = DateTimeFormat.mediumDateTime()

@KtorExperimentalAPI
val Application.envKind get() = environment.config.property("ktor.environment").getString()
@KtorExperimentalAPI
val Application.isDev get() = envKind == "dev"
@KtorExperimentalAPI
val Application.isProd get() = envKind == "prod"