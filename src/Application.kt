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
    Database.connect(
        "jdbc:postgresql://localhost:5432/kiriminchan_board",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(Users)
        SchemaUtils.drop(Threads)
        SchemaUtils.drop(Comments)
        SchemaUtils.drop(Reactions)
        SchemaUtils.drop(Stamps)
        SchemaUtils.drop(CommentReactions)

        SchemaUtils.create(Users)
        SchemaUtils.create(Threads)
        SchemaUtils.create(Comments)
        SchemaUtils.create(Reactions)
        SchemaUtils.create(Stamps)
        SchemaUtils.create(CommentReactions)

        // insert new city. SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg')
//        val stPeteId = Cities.insert {
//            it[name] = "St. Petersburg"
//        } get Cities.id
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/thread/list") {
            val allThreads = transaction {
                Threads.selectAll().filter { it[Threads.isDeleted] == '0' }.map {
                    site.kirimin_chan.board.model.Thread(
                        threadId = it[Threads.threadId],
                        title = it[Threads.title],
                        createdUserId = it[Threads.createdUserId],
                        createdAt = DEF_FMT.print(it[Threads.createdAt]),
                        updatedAt = DEF_FMT.print(it[Threads.updatedAt])
                    )
                }
            }
            call.respond(allThreads)
        }
        post("/thread/create") {
            val request = call.receive<CreateThreadRequest>()
            println("request:$request")
            transaction {
                val result = Threads.insert {
                    it[createdUserId] = request.createdUserId
                    it[title] = request.text
                    it[Comments.isDeleted] = '0'
                    it[createdAt] = DateTime()
                    it[updatedAt] = DateTime()
                }
                val newThreadId = result.resultedValues?.get(0)?.get(Threads.threadId) ?: 0
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
    }
}

var DEF_FMT: DateTimeFormatter = DateTimeFormat.mediumDateTime()