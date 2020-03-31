package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object CommentReactions: Table() {
    val commentId: Column<Int> = integer("commentid")
    val reactionId: Column<Int> = Reactions.integer("reactionid")
    val userId: Column<Int> = Users.integer("userid")
    override val primaryKey = PrimaryKey(commentId, reactionId, userId)
}