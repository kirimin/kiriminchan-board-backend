package db.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object CommentReactions: Table() {
    val commentId: Column<Int> = integer("commentid")
    val reactionId: Column<Int> = integer("reactionid")
    val userId: Column<Int> = integer("userid")
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(commentId, reactionId, userId)
}