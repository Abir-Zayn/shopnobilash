package com.shopnobilash.app.data.chat.model

data class Conversation(
    val propertyId: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val isOnline: Boolean,
)

data class ChatMessage(
    val isMe: Boolean,
    val text: String,
    val time: String,
)

val MOCK_CONVERSATIONS = listOf(
    Conversation("earth",   "Sure, the place is available from July 1st.", "2m",  2, true),
    Conversation("minimal", "I can send over a few more photos if you'd like.", "1h", 0, true),
    Conversation("lara",    "Thanks for your interest in the apartment!", "Tue", 0, false),
    Conversation("aspen",   "Your villa tour is confirmed for Saturday ✓", "Mon", 0, false),
)
