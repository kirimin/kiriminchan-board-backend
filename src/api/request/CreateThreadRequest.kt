package site.kirimin_chan.board.api.request

data class CreateThreadRequest(
    val createdUserId: Int,
    val title: String,
    val text: String
)