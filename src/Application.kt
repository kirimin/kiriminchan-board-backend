package site.kirimin_chan.board

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import site.kirimin_chan.board.api.request.CreateThreadRequest
import site.kirimin_chan.board.db.KiriminchanBoardDb
import site.kirimin_chan.board.entities.*
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    println("start application module")
    install(ContentNegotiation) {
        gson()
    }
    install(CORS) {
        method(HttpMethod.Options)
        header(HttpHeaders.XForwardedProto)
        anyHost()
        // host("my-host:80")
        // host("my-host", subDomains = listOf("www"))
        // host("my-host", schemes = listOf("http", "https"))
        allowCredentials = true
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = Duration.ofDays(1).seconds
    }

    KiriminchanBoardDb.connect()
    KiriminchanBoardDb.initTables()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/thread/list") {
            call.respond(Threads.getAllThread())
        }
        post("/thread/create") {
            val request = call.receive<CreateThreadRequest>()
            println("request:$request")
            transaction {
                val newThreadId = Threads.insert {
                    it[createdUserId] = request.createdUserId
                    it[title] = request.text
                    it[Comments.isDeleted] = '0'
                    it[createdAt] = DateTime()
                    it[updatedAt] = DateTime()
                } get Threads.threadId
                Comments.insert {
                    it[threadId] = newThreadId
                    it[createdUserId] = request.createdUserId
                    it[text] = request.text
                    it[isDeleted] = '0'
                    it[Threads.createdAt] = DateTime()
                    it[Threads.updatedAt] = DateTime()
                }
            }
            call.respond(mapOf("status" to "OK"))
        }
        get("/comment/list") {
            val params = call.request.queryParameters
            val threadId = params["threadid"]?.toIntOrNull()
            if (threadId == null) {
                call.respond(mapOf("status" to "IllegalArguments. param threadid must not empty."))
                return@get
            }
            call.respond(Comments.getByThreadId(threadId))
        }
    }
}

var DEF_FMT: DateTimeFormatter = DateTimeFormat.mediumDateTime()