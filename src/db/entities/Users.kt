package db.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.DEF_FMT
import model.User
import java.lang.NullPointerException

object Users : Table() {
    val userId: Column<Int> = integer("userid").autoIncrement()
    val screenName: Column<String> = varchar("screen_name", 80)
    val iconUrl: Column<String> = varchar("icon_url", 80)
    val isDeleted: Column<Char> = char("is_deleted")
    val isAdmin: Column<Char> = char("is_admin")
    val twitterId: Column<String> = varchar("twitter_id", 80)
    val firebaseUid: Column<String> = varchar("firebase_uid", 80)
    val createdAt: Column<DateTime> = datetime("created_at")
    val updatedAt: Column<DateTime> = datetime("updated_at")
    override val primaryKey = PrimaryKey(userId)

    fun getUserById(id: Int) = transaction {
        return@transaction Users.select { userId eq id }.map {
            User(
                userId = it[userId],
                screenName = it[screenName],
                iconUrl = it[iconUrl],
                isDeleted = it[isDeleted],
                isAdmin = it[isAdmin],
                twitterId = it[twitterId],
                uid = it[firebaseUid],
                createdAt = DEF_FMT.print(it[createdAt]),
                updatedAt = DEF_FMT.print(it[updatedAt])
            )
        }.firstOrNull() ?: throw NullPointerException("UserNotFound userId:$id")
    }
}