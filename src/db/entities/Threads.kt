package db.entities

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.model.Thread
import toFormattedDateTime

object Threads: Table() {
    val threadId: Column<Int> = integer("threadid").autoIncrement()
    val createdUserId: Column<Int> = integer("created_userid")
    val title: Column<String> = text("title")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(threadId)

    fun getAllThread() = transaction {
        Threads.selectAll().orderBy(updatedAt to SortOrder.DESC).map {
            toModel(it)
        }
    }

    fun getByThreadId(id: Int) = transaction {
        Threads.select { (threadId eq id) }.map {
            toModel(it)
        }.first()
    }

    private fun toModel(row: ResultRow) = Thread(
        threadId = row[threadId],
        title = row[title],
        createdUserId = row[createdUserId],
        createdUserName = Users.getUserById(row[createdUserId]).screenName,
        createdAt = toFormattedDateTime((row[createdAt])),
        updatedAt = toFormattedDateTime((row[updatedAt])),
        comments = emptyList()
    )
}