package site.kirimin_chan.board.api.request

data class CreateNewCommentRequest(
    val threadId: Int,
    val createdUserId: Int,
    val text: String
)