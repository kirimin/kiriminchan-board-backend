package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Comments: Table() {
    val commentId: Column<Int> = integer("commentid").autoIncrement()
    val threadId: Column<Int> = integer("threadid")
    val stampId:  Column<Int> = integer("stampid")
    val isDeleted: Column<Char> = char("is_deleted")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(commentId)
}