package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Thereads: Table() {
    val threadId: Column<Int> = integer("threadid")
    val createdDate: Column<DateTime> = datetime("created_date")
    val updatedDate: Column<DateTime> = datetime("updated_date")
    val createdUserId: Column<Int> = integer("created_userid")
    override val primaryKey = PrimaryKey(threadId)
}