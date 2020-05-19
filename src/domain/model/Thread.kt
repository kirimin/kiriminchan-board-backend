package site.kirimin_chan.board.model

data class Thread(
    val threadId: Int,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val createdUserId: Int,
    val createdUserName: String,
    var comments : List<Comment>
)