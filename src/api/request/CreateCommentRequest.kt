package site.kirimin_chan.board.api.request

data class CreateCommentRequest(
    val threadId: Int,
    val createdUserId: Int,
    val text: String
)