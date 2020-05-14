package site.kirimin_chan.board.api.request

data class DeleteThreadRequest(
    val threadId: Int,
    val userId: Int,
    val token: String
)