package site.kirimin_chan.board.db

import db.entities.Threads
import db.entities.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.entities.*

object KiriminchanBoardDb {

    fun connect() {
        Database.connect(System.getenv("JDBC_DATABASE_URL"), driver = "org.postgresql.Driver")
    }

    fun initTables() {
        transaction {
            addLogger(StdOutSqlLogger)

//            dropTables()

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
                it[token] = ""
            }
        }
    }
}