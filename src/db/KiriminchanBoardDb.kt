package site.kirimin_chan.board.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import site.kirimin_chan.board.entities.*

object KiriminchanBoardDb {

    fun connect() {
        Database.connect(
            "jdbc:postgresql://localhost:5432/kiriminchan_board",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
    }

    fun initTables() {
        transaction {
            addLogger(StdOutSqlLogger)

//        SchemaUtils.drop(Users)
//        SchemaUtils.drop(Threads)
//        SchemaUtils.drop(Comments)
//        SchemaUtils.drop(Reactions)
//        SchemaUtils.drop(Stamps)
//        SchemaUtils.drop(CommentReactions)

            SchemaUtils.create(Users)
            SchemaUtils.create(Threads)
            SchemaUtils.create(Comments)
            SchemaUtils.create(Reactions)
            SchemaUtils.create(Stamps)
            SchemaUtils.create(CommentReactions)
        }
    }
}