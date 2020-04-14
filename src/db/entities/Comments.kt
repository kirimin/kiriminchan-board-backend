package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.DEF_FMT
import site.kirimin_chan.board.model.Comment

object Comments : Table() {
    val commentId: Column<Int> = integer("commentid").autoIncrement()
    val threadId: Column<Int> = integer("threadid")
    val createdUserId: Column<Int> = integer("createdUserid")
    val text: Column<String> = text("title")
    val stampId: Column<Int?> = integer("stampid").nullable()
    val isDeleted: Column<Char> = char("is_deleted")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(commentId)

    fun getByThreadId(threadId: Int) = transaction {
        Comments.select { Comments.threadId eq threadId }.map {
            toModel(it)
        }
    }

    private fun toModel(row: ResultRow) = Comment(
        commentId = row[commentId],
        threadId = row[threadId],
        createdUserId = row[createdUserId],
        isDeleted = row[isDeleted],
        text = row[text],
        stampId = row[stampId],
        createdAt = DEF_FMT.print(row[createdAt]),
        updatedAt = DEF_FMT.print(row[updatedAt])
    )
}