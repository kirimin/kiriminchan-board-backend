package db.entities

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.DEF_FMT
import site.kirimin_chan.board.model.Thread

object Threads: Table() {
    val threadId: Column<Int> = integer("threadid").autoIncrement()
    val createdUserId: Column<Int> = integer("created_userid")
    val title: Column<String> = text("title")
    val isDeleted: Column<Char> = char("is_deleted")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(threadId)

    fun getAllThread() = transaction {
        Threads.select { isDeleted eq '0' }.orderBy(threadId to SortOrder.DESC).map {
            toModel(it)
        }
    }

    fun getByThreadId(id: Int) = transaction {
        Threads.select { (isDeleted eq '0') and (threadId eq id) }.map {
            toModel(it)
        }.first()
    }

    private fun toModel(row: ResultRow) = Thread(
        threadId = row[threadId],
        title = row[title],
        createdUserId = row[createdUserId],
        createdUserName = Users.getUserById(row[createdUserId]).screenName,
        createdAt = DEF_FMT.print(row[createdAt]),
        updatedAt = DEF_FMT.print(row[updatedAt]),
        comments = emptyList()
    )
}