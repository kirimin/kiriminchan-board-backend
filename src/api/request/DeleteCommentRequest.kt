package site.kirimin_chan.board.api.request

data class DeleteCommentRequest(
    val commentId: Int,
    val userId: Int,
    val token: String
)