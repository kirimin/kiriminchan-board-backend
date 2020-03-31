package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId: Column<Int> = integer("userid")
    val screenName: Column<String> = varchar("screen_name", 80)
    val iconUrl: Column<String> = varchar("icon_url", 80)
    val isDeleted: Column<Char> = char("is_deleted")
    val twitterId: Column<String> = varchar("twitter_id", 80)
    override val primaryKey = PrimaryKey(userId)
}