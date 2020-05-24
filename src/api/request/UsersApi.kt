package site.kirimin_chan.board.api.request

import db.entities.Users
import domain.model.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import site.kirimin_chan.board.auth.FirebaseAuth
import site.kirimin_chan.board.exceptions.TokenCheckException

object UsersApi {

    fun getUser(uId: String?): User {
        uId?.let {
            return transaction {
                Users.select { Users.firebaseUid eq uId }.map {
                    Users.getUserById(it[Users.userId])
                }.first()
            }
        } ?: throw IllegalArgumentException("param uId must not empty.")
    }

    fun createUser(request: CreateNewUserRequest) = transaction {
        Users.insert {
            it[screenName] = request.name
            it[iconUrl] = ""
            it[isDeleted] = '0'
            it[isAdmin] = '0'
            it[twitterId] = ""
            it[firebaseUid] = request.firebaseUid
            it[createdAt] = DateTime()
            it[updatedAt] = DateTime()
        }
    }

    fun deleteUser(request: DeleteUserRequest) {
        if (!FirebaseAuth.checkToken(request.userId, request.token)) {
            throw TokenCheckException("token is wrong: token:${request.token}")
        }
        transaction {
            Users.update(where = { Users.userId eq request.userId }, body = {
                it[isDeleted] = '1'
            })
        }
    }

    data class CreateNewUserRequest(
        val name: String,
        val firebaseUid: String
    )

    data class DeleteUserRequest(
        val userId: Int,
        val token: String
    )
}