package site.kirimin_chan.board.db

import db.entities.Threads
import db.entities.Users
import io.ktor.application.Application
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.entities.*
import site.kirimin_chan.board.isDev
import site.kirimin_chan.board.isProd
import java.lang.IllegalStateException

object KiriminchanBoardDb {

    fun connect(application: Application) {
        when {
            application.isDev -> {
                Database.connect(
                    "jdbc:postgresql://localhost:5432/kiriminchan_board",
                    driver = "org.postgresql.Driver",
                    user = "postgres",
                    password = "postgres"
                )
            }
            application.isProd -> {
                Database.connect(System.getenv("JDBC_DATABASE_URL"), driver = "org.postgresql.Driver")
            }
            else -> throw IllegalStateException()
        }
    }

    fun initTables() {
        transaction {
            addLogger(StdOutSqlLogger)

            dropTables()

            SchemaUtils.create(Users)
            SchemaUtils.create(Threads)
            SchemaUtils.create(Comments)
            SchemaUtils.create(Reactions)
            SchemaUtils.create(Stamps)
            SchemaUtils.create(CommentReactions)

            createAdminUserIfNeeded()
        }
    }

    private fun dropTables() {
        SchemaUtils.drop(Users)
        SchemaUtils.drop(Threads)
        SchemaUtils.drop(Comments)
        SchemaUtils.drop(Reactions)
        SchemaUtils.drop(Stamps)
        SchemaUtils.drop(CommentReactions)
    }

    private fun createAdminUserIfNeeded() {
        if (Users.select { Users.isAdmin eq '1' }.empty()) {
            Users.insert {
                it[userId] = Integer.MAX_VALUE
                it[screenName] = "きりみんちゃん@admin"
                it[iconUrl] = ""
                it[isDeleted] = '0'
                it[twitterId] = "kirimin_chan"
                it[isAdmin] = '1'
                it[firebaseUid] = ""
                it[createdAt] = DateTime()
                it[updatedAt] = DateTime()
            }
        }
    }
}