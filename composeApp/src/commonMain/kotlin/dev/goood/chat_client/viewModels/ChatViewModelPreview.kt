package dev.goood.chat_client.viewModels


import dev.goood.chat_client.model.Message
import dev.goood.chat_client.model.MessageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModelPreview: ChatViewModel() {

    private val _state = MutableStateFlow<State>(State.NewReply)
    override val state: StateFlow<State> = _state
    override val newReply: StateFlow<String> = MutableStateFlow("")

    val list = listOf(
        Message(
            id = 1,
            content = "### What's included \uD83D\uDE80\n" +
                    "# This is H1" +
                    "\n" +
                    "- Super simple setup\n" +
                    "- Cross-platform ready\n" +
                    "- Lightweight",
            initiator = 0,
            role = "A"
        ),
        Message(
            id = 2,
            content = "Some reply content",
            initiator = 1,
            role = "A"
        )
    )

    private val _messages = MutableStateFlow(list)
    override val messages: StateFlow<MessageList> = _messages

    override fun getMessages(chatId: Int) {}

    override fun sendMessage(messageText: String) {}
}