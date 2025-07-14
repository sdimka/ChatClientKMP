package dev.goood.chat_client.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.goood.chat_client.model.AttachedFiles
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.SystemMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQueries = database.appDatabaseQueries

    fun updateMessages(chatID: Int, messages: List<Message>) {
        dbQueries.transaction {
            messages.forEach { message ->
                dbQueries.insertOrReplaceMessage(
                    id = message.id.toLong(),
                    chatID = chatID.toLong(),
                    content = message.content,
                    initiator = message.initiator.toLong(),
                    role = message.role,
                    systemMessage = message.systemMessage!!.title,
                    files = message.files?.map { it.name }.toString(),
                    updatedAt = message.updatedAt,
                )
            }
        }
    }

    fun getMessages(chatID: Int): Flow<List<Message>> {
        return dbQueries.selectMessages(chatID.toLong()) { id, chatID, content, initiator, role, systemMessage, files, updatedAt ->
            Message(
                id = id.toInt(),
                content = content,
                initiator = initiator.toInt(),
                role = role,
                systemMessage = SystemMessage(
                    title = systemMessage,
                    id = 0,
                    content = ""
                ),
                files = files.split(",").map { AttachedFiles(it) },
                updatedAt = updatedAt
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    fun deleteMessage(id: Int) {
        dbQueries.deleteMessage(id.toLong())
    }
}