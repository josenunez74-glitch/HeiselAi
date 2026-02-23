package com.heiselai.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatRequest(
    val messages: List<ChatMessage>,
    val model: String = "llama3",
    val stream: Boolean = false,
    val systemPrompt: String? = null
)

@Serializable
data class ChatResponse(
    val message: ChatMessage,
    val model: String,
    val done: Boolean = true
)

@Serializable
data class ImageRequest(
    val prompt: String,
    val negativePrompt: String = "",
    val width: Int = 512,
    val height: Int = 512,
    val steps: Int = 20,
    val cfgScale: Float = 7f,
    val model: String = "stable-diffusion"
)

@Serializable
data class ImageResponse(
    val imageBase64: String,
    val seed: Long,
    val model: String
)

@Serializable
data class SearchRequest(
    val query: String,
    val maxResults: Int = 5
)

@Serializable
data class SearchResult(
    val title: String,
    val url: String,
    val content: String,
    val score: Double
)

@Serializable
data class SearchResponse(
    val results: List<SearchResult>,
    val query: String
)

@Serializable
data class IoTRequest(
    val device: String,
    val action: String,
    val value: Any? = null
)

@Serializable
data class IoTResponse(
    val success: Boolean,
    val device: String,
    val action: String,
    val result: Any? = null,
    val message: String = ""
)

@Serializable
data class PhoneCommand(
    val action: String,
    val params: Map<String, String> = emptyMap()
)

@Serializable
data class PhoneResponse(
    val success: Boolean,
    val action: String,
    val result: Any? = null,
    val message: String = ""
)

@Serializable
data class HealthResponse(
    val status: String,
    val services: Map<String, String>
)
