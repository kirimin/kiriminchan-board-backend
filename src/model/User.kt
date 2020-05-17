package model

data class User(
    val userId: Int,
    val screenName: String,
    val iconUrl: String,
    val isDeleted: Char,
    val isAdmin: Char,
    val uid: String,
    val twitterId: String,
    val createdAt: String,
    val updatedAt: String
)