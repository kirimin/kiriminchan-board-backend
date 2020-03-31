package site.kirimin_chan.board

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import site.kirimin_chan.board.entities.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    println("start application module")
    install(ContentNegotiation) {
        gson()
    }
    Database.connect(
        "jdbc:postgresql://localhost:5432/kiriminchan_board",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)
        SchemaUtils.create(Thereads)
        SchemaUtils.create(Comments)
        SchemaUtils.create(Reactions)
        SchemaUtils.create(Stamps)
        SchemaUtils.create(CommentReactions)

        // insert new city. SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg')
//        val stPeteId = Cities.insert {
//            it[name] = "St. Petersburg"
//        } get Cities.id
        Thereads.insert {
            it[createdUserId] = 1
            it[title] = "test"
            it[createdAt] = DateTime()
            it[updatedAt] = DateTime()
        }

        Users.selectAll().forEach {
            println(it)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/thread/list") {
            val allThreads = transaction {
                Thereads.selectAll().map {
                    site.kirimin_chan.board.model.Thread(
                        threadId = it[Thereads.threadId],
                        createdUserId = it[Thereads.createdUserId],
                        createdAt = DEF_FMT.print(it[Thereads.createdAt]),
                        updatedAt = DEF_FMT.print(it[Thereads.updatedAt])
                    )
                }
            }
            call.respond(allThreads)
        }
    }
}

//
//fun main() {
//
//}

var DEF_FMT: DateTimeFormatter = DateTimeFormat.mediumDateTime()