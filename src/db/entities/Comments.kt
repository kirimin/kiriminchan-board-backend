package site.kirimin_chan.board.entities

import db.entities.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.model.Comment
import toFormattedDateTime

object Comments : Table() {
    val commentId: Column<Int> = integer("commentid").autoIncrement()
    val threadId: Column<Int> = integer("threadid")
    val createdUserId: Column<Int> = integer("createdUserid")
    val commentNumber: Column<Int> = integer("commentNumber")
    val text: Column<String> = text("title")
    val stampId: Column<Int?> = integer("stampid").nullable()
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(commentId)

    fun getByThreadId(threadId: Int) = transaction {
        Comments.select { Comments.threadId eq threadId }.map {
            toModel(it)
        }
    }

    fun getLastCommentNumber(threadId: Int) = transaction {
        toModel(Comments.select { (Comments.threadId eq threadId) }.orderBy(commentNumber, SortOrder.ASC).last())
    }

    private fun toModel(row: ResultRow) = Comment(
        commentId = row[commentId],
        threadId = row[threadId],
        createdUserId = row[createdUserId],
        createdUserName = Users.getUserById(row[createdUserId]).screenName,
        commentNumber = row[commentNumber],
        text = row[text],
        stampId = row[stampId],
        createdAt = toFormattedDateTime((row[createdAt])),
        updatedAt = toFormattedDateTime((row[updatedAt]))
    )
}