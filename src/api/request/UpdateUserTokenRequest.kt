package site.kirimin_chan.board.api.request

data class UpdateUserTokenRequest(
    val uid: String,
    val token: String
)