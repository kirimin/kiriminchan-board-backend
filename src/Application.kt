package site.kirimin_chan.board

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.kirimin_chan.board.entities.*

//fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
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

        Users.selectAll().forEach {
            println(it)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/kiriminchan_board",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )
}