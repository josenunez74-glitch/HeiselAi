package com.example.heiselai

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heiselai.Message
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatEvent {
    data class Navigate(val route: String) : ChatEvent()
    data class StartActivity(val intent: Intent) : ChatEvent()
    object RequestPermissions : ChatEvent()
}

class ChatViewModel(private val repository: AiRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _events = MutableSharedFlow<ChatEvent>()
    val events = _events.asSharedFlow()

    val inputText = MutableStateFlow("")

    fun onInputTextChanged(text: String) {
        inputText.value = text
    }

    fun sendMessage(hasCallPhonePermission: Boolean) {
        val text = inputText.value
        if (text.isBlank()) return

        val userMessage = Message(text, true)
        _messages.value = _messages.value + userMessage
        inputText.value = ""

        viewModelScope.launch {
            when {
                text.equals("health", ignoreCase = true) -> {
                    _events.emit(ChatEvent.Navigate("health"))
                    _messages.value = _messages.value + Message("Navigating to health screen.", false)
                }
                text.equals("agenda", ignoreCase = true) -> {
                    _events.emit(ChatEvent.Navigate("agenda"))
                    _messages.value = _messages.value + Message("Navigating to agenda screen.", false)
                }
                text.startsWith("call", ignoreCase = true) -> {
                    if (hasCallPhonePermission) {
                        val phoneNumber = text.substringAfter("call ")
                        val intent = Intent(Intent.ACTION_CALL, "tel:$phoneNumber".toUri())
                        _events.emit(ChatEvent.StartActivity(intent))
                        _messages.value = _messages.value + Message("Calling $phoneNumber", false)
                    } else {
                        _events.emit(ChatEvent.RequestPermissions)
                        _messages.value = _messages.value + Message("Permission to call is required.", false)
                    }
                }
                text.startsWith("search for", ignoreCase = true) -> {
                    val query = text.substringAfter("search for ")
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.google.com/search?q=$query".toUri())
                    _events.emit(ChatEvent.StartActivity(intent))
                    _messages.value = _messages.value + Message("Searching for: $query", false)
                }
                text.startsWith("search on youtube for", ignoreCase = true) -> {
                    val query = text.substringAfter("search on youtube for ")
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.youtube.com/results?search_query=$query".toUri())
                    _events.emit(ChatEvent.StartActivity(intent))
                    _messages.value = _messages.value + Message("Searching YouTube for: $query", false)
                }
                else -> {
                    val aiResponse = repository.getAiResponse(text)
                    _messages.value = _messages.value + Message(aiResponse, false)
                }
            }
        }
    }
}