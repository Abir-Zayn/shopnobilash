package com.shopnobilash.app.ui.feature.chat

import androidx.lifecycle.ViewModel
import com.shopnobilash.app.data.model.ChatMessage
import com.shopnobilash.app.data.model.MOCK_CONVERSATIONS
import com.shopnobilash.app.data.model.propertyById
import com.shopnobilash.app.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(
    private val repository: PropertyRepository,
    private val propertyId: String? = null,
) : ViewModel() {

    val conversations = MOCK_CONVERSATIONS

    private val _messages = MutableStateFlow(
        if (propertyId != null) {
            val pr = propertyById(propertyId)
            listOf(
                ChatMessage(false, "Hi! Thanks for reaching out about ${pr.title}.", "09:41"),
                ChatMessage(true,  "Hi! Is it still available for July?", "09:42"),
                ChatMessage(false, "Sure, the place is available from July 1st.", "09:42"),
                ChatMessage(true,  "Great. Could I schedule a tour this weekend?", "09:43"),
                ChatMessage(false, "Absolutely — Saturday at 11am works well for me.", "09:44"),
            )
        } else emptyList()
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun setInputText(text: String) { _inputText.value = text }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return
        _messages.value = _messages.value + ChatMessage(true, text, "now")
        _inputText.value = ""
    }
}
