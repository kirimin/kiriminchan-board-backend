package site.kirimin_chan.board.api.request

data class CreateNewThreadRequest(
    val createdUserId: Int,
    val title: String,
    val text: String,
    val token: String
)