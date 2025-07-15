package dev.goood.chat_client.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.goood.chat_client.model.AttachedFiles
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.SystemMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQueries = database.appDatabaseQueries

    @OptIn(ExperimentalTime::class)
    fun updateMessages(chatID: Int, messages: List<Message>) {

        val messagesToDelete = mutableListOf<Message>()
        val messagesToUpsert = mutableListOf<Message>()

        for (message in messages) {
            if (message.deletedAt != null) {
                messagesToDelete.add(message)
            } else {
                messagesToUpsert.add(message)
            }
        }

        dbQueries.transaction {

            messagesToDelete.forEach { messageToDelete ->
                deleteMessage(messageToDelete.id) // Assuming this is synchronous or handled correctly within the transaction
            }

            messagesToUpsert.forEach { message ->

                val files: String? = message.files?.takeIf { it.isNotEmpty() }?.joinToString(",") { it.name }

                dbQueries.insertOrReplaceMessage(
                    id = message.id.toLong(),
                    chatID = chatID.toLong(),
                    content = message.content,
                    initiator = message.initiator.toLong(),
                    role = message.role,
                    systemMessage = message.systemMessage?.title,
                    files = files,
                    updatedAt = Instant.parse(message.updatedAt).toEpochMilliseconds(),
                    )
            }
        }
    }

    fun getLastUpdateTime(chatID: Int): Long? {
        return dbQueries.getLastUpdateTime(chatID.toLong()).executeAsOne().highest_value
    }

    @OptIn(ExperimentalTime::class)
    fun getMessages(chatID: Int): Flow<List<Message>> {
        return dbQueries.selectMessages(chatID.toLong()) { id, chatID, content, initiator, role, systemMessage, fNames, updatedAtMillis ->

            val sm = systemMessage?.let{
                SystemMessage(
                    title = it,
                    id = 0,
                    content = ""
                )
            }

            val files = fNames?.takeIf { it.isNotBlank() }
                ?.split(",")
                ?.map { AttachedFiles(it) }

            Message(
                id = id.toInt(),
                content = content,
                initiator = initiator.toInt(),
                role = role,
                systemMessage = sm,
                files = files,
                updatedAt = Instant.fromEpochMilliseconds(updatedAtMillis).toString()
            )

        }.asFlow().mapToList(Dispatchers.IO)
    }

    fun deleteMessage(id: Int) {
        dbQueries.deleteMessage(id.toLong())
    }
}