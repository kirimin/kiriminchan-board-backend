package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Thereads: Table() {
    val threadId: Column<Int> = integer("threadid").autoIncrement()
    val createdUserId: Column<Int> = integer("created_userid")
    val title: Column<String> = text("title")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(threadId)
}