package site.kirimin_chan.board.api.request

import db.entities.Comments
import db.entities.Threads
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import site.kirimin_chan.board.auth.FirebaseAuth
import site.kirimin_chan.board.exceptions.TokenCheckException

object CommentsApi {
    fun createNewComment(request: CreateNewCommentRequest) {
        if (!FirebaseAuth.checkToken(request.createdUserId, request.token)) {
            throw TokenCheckException("token is wrong: token:${request.token}")
        }
        transaction {
            Comments.insert {
                it[threadId] = request.threadId
                it[createdUserId] = request.createdUserId
                it[commentNumber] = getLastCommentNumber(request.threadId).commentNumber + 1
                it[text] = request.text
                it[Threads.createdAt] = DateTime()
                it[Threads.updatedAt] = DateTime()
            }
            Threads.update(where = { Threads.threadId eq request.threadId }, body = {
                it[updatedAt] = DateTime()
            })
        }
    }

    fun deleteComment(request: DeleteCommentRequest) {
        if (!FirebaseAuth.checkToken(request.userId, request.token)) {
            throw TokenCheckException("token is wrong: token:${request.token}")
        }
        transaction {
            Comments.deleteWhere { Comments.commentId eq request.commentId }
        }
    }

    data class CreateNewCommentRequest(
        val threadId: Int,
        val createdUserId: Int,
        val text: String,
        val token: String
    )

    data class DeleteCommentRequest(
        val commentId: Int,
        val userId: Int,
        val token: String
    )
}