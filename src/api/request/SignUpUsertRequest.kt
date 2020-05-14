package site.kirimin_chan.board.api.request

data class CreateNewUserRequest(
    val name: String,
    val firebaseUid: String,
    val token: String
)