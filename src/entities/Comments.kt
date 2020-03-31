package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Comments: Table() {
    val commentId: Column<Int> = integer("commentid")
    val threadId: Column<Int> = Thereads.integer("threadid")
    val createdUserId: Column<Int> = Thereads.integer("created_userid")
    val stampId:  Column<Int> = integer("stampid")
    val isDeleted: Column<Char> = Users.char("is_deleted")
    override val primaryKey = PrimaryKey(commentId)
}