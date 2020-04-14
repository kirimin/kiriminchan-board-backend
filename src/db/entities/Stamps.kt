package site.kirimin_chan.board.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Stamps: Table() {
    val stampId: Column<Int> = integer("stampid")
    val imageUrl: Column<String> = varchar("image_url", 80)
    override val primaryKey = PrimaryKey(stampId)
}