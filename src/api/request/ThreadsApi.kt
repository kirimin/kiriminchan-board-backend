package site.kirimin_chan.board.api.request

import db.entities.Comments
import db.entities.Threads
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import site.kirimin_chan.board.auth.FirebaseAuth
import site.kirimin_chan.board.exceptions.TokenCheckException

object ThreadsApi {

    fun getThreadsSumally() = transaction {
        Threads.getAllThread().map {
            it.comments = Comments.getByThreadId(it.threadId)
            it
        }.filter {
            it.comments.isNotEmpty()
        }
    }

    fun getThreadDetail(threadId: String?) = transaction {
        threadId?.toInt()?.let { threadId ->
            Threads.getByThreadId(threadId).let {
                it.comments = Comments.getByThreadId(it.threadId)
                it
            }
        } ?: throw IllegalArgumentException("param threadid must not empty.")
    }

    fun createNewThread(request: CreateNewThreadRequest) {
        if (!FirebaseAuth.checkToken(request.createdUserId, request.token)) {
            throw TokenCheckException("token is wrong: token:${request.token}")
        }
        transaction {
            val newThreadId = Threads.insert {
                it[createdUserId] = request.createdUserId
                it[title] = request.title
                it[createdAt] = DateTime()
                it[updatedAt] = DateTime()
            } get Threads.threadId
            Comments.insert {
                it[threadId] = newThreadId
                it[createdUserId] = request.createdUserId
                it[commentNumber] = 1
                it[text] = request.text
                it[Threads.createdAt] = DateTime()
                it[Threads.updatedAt] = DateTime()
            }
        }
    }

    fun deleteThread(request: DeleteThreadRequest) {
        if (!FirebaseAuth.checkToken(request.userId, request.token)) {
            throw TokenCheckException("token is wrong: token:${request.token}")
        }
        transaction {
            Threads.deleteWhere { Threads.threadId eq request.threadId }
            Comments.deleteWhere { Comments.threadId eq request.threadId }
        }
    }

    data class CreateNewThreadRequest(
        val createdUserId: Int,
        val title: String,
        val text: String,
        val token: String
    )

    data class DeleteThreadRequest(
        val threadId: Int,
        val userId: Int,
        val token: String
    )
}