package site.kirimin_chan.board.model

import org.joda.time.DateTime

data class Thread(
    val threadId: Int,
    val createdAt: String,
    val updatedAt: String,
    val createdUserId: Int
)