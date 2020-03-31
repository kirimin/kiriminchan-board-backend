package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Reactions : Table() {
    val reactionId: Column<Int> = integer("reactionid")
    val isEmoji: Column<Char> = char("is_emoji")
    val emojiCode: Column<String> = varchar("emoji_code", 80)
    val isImage: Column<Char> = char("is_image")
    val imageUrl: Column<String> = varchar("image_url", 80)
    override val primaryKey = PrimaryKey(reactionId)
}