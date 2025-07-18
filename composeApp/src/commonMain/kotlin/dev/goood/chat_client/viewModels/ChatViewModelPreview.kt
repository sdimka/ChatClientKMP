package dev.goood.chat_client.viewModels


import dev.goood.chat_client.model.MFile
import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageList
import dev.goood.chat_client.model.SystemMessage
import dev.goood.chat_client.model.SystemMessageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModelPreview() : ChatViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    override val state: StateFlow<State> = _state
    override val newReply: StateFlow<String> = MutableStateFlow("")

    private val _inputValue = MutableStateFlow("")
    override val inputValue: StateFlow<String> = _inputValue.asStateFlow()
    override val isPreviousMessagesEnabled: StateFlow<Boolean> = MutableStateFlow(true)

    val list = listOf(
        Message(
            id = 1,
            content = "### What's included \uD83D\uDE80\n" +
                    "# This is H1 \n" +
                    "## This is H2 \n" +
                    "### This is H3 \n" +
                    "#### This is H4 \n" +
                    "Base text \n" +
                    "**Marked text**" +
                    "\n" +
                    "- Super simple setup\n" +
                    "- Cross-platform ready\n" +
                    "- Lightweight",
            initiator = 0,
            role = "A",
            systemMessage = SystemMessage(
                id = 1,
                title = "Sys message",
                content = ""
            ),
            updatedAt = "",
        ),
        Message(
            id = 2,
            content = "Some reply content",
            initiator = 1,
            role = "A",
            updatedAt = "",
        )
    )

    override val systemMessages: StateFlow<SystemMessageList> = MutableStateFlow(
        listOf(
            SystemMessage(1, "Some sys mess", "Some conetnt")
        ))
    override val selectedSysMessage: StateFlow<SystemMessage?> = MutableStateFlow(
        SystemMessage(1, "Some sys mess", "Some conetnt")
    )

    override val filesList: StateFlow<List<MFile>> = MutableStateFlow(
        listOf(
            MFile(
                id = "1",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "2",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview1.pdf",
                purpose = "assistants"
            ),
            MFile(
                id = "3",
                obj = "Obj",
                bytes = 123456,
                createdAt = 1613677385,
                filename = "salesOverview2.pdf",
                purpose = "assistants"
            ),
        )
    )

    override fun selectSysMessage(sysMessage: SystemMessage?) {

    }

    override fun onPreviousMessagesEnabledChanged(checked: Boolean) {

    }

    override fun onSelectedMessagesListUpdate(messageID: Int) {

    }

    override fun resetChatSpecificStates() {

    }

    override fun updateFileList(file: MFile, operation: (List<MFile>, MFile) -> List<MFile>) {

    }


    private val _messages = MutableStateFlow(list)
    override val messages: StateFlow<MessageList> = _messages

    override fun getMessages(chatId: Int) {}

    override fun deleteMessage(message: Message) {

    }

    override fun sendMessage(messageText: String) {}

    override fun updateInputValue(newValue: String) {}

    override fun clearInputValue() {}
}