package com.shopnobilash.app.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopnobilash.app.data.chat.model.ChatMessage
import com.shopnobilash.app.data.chat.model.MOCK_CONVERSATIONS
import com.shopnobilash.app.data.property.model.Property
import com.shopnobilash.app.domain.property.usecase.GetPropertyByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getPropertyByIdUseCase: GetPropertyByIdUseCase,
    private val propertyId: String? = null,
) : ViewModel() {

    val conversations = MOCK_CONVERSATIONS

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    init {
        if (propertyId != null) {
            viewModelScope.launch {
                getPropertyByIdUseCase(propertyId).onSuccess { prop ->
                    _property.value = prop
                    val title = prop.title
                    _messages.value = listOf(
                        ChatMessage(false, "Hi! Thanks for reaching out about $title.", "09:41"),
                        ChatMessage(true,  "Hi! Is it still available for July?", "09:42"),
                        ChatMessage(false, "Sure, the place is available from July 1st.", "09:42"),
                        ChatMessage(true,  "Great. Could I schedule a tour this weekend?", "09:43"),
                        ChatMessage(false, "Absolutely — Saturday at 11am works well for me.", "09:44"),
                    )
                }
            }
        }
    }

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
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
