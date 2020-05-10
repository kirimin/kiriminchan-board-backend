package site.kirimin_chan.board.model

data class Comment(
    val commentId: Int,
    val threadId: Int,
    val createdUserId: Int,
    val createdUserName: String,
    val commentNumber: Int,
    val text: String,
    val stampId: Int?,
    val createdAt: String,
    val updatedAt: String
)