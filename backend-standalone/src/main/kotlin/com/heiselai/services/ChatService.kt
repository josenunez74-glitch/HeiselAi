package com.heiselai.services

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import com.heiselai.utils.SystemPrompts
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

class ChatService(private val config: AppConfig) {
    private val logger = LoggerFactory.getLogger(ChatService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun chat(request: ChatRequest): Result<ChatResponse> {
        return try {
            val messages = buildMessages(request)
            
            val ollamaRequest = buildJsonObject {
                put("model", request.model.ifEmpty { config.defaultModel })
                put("messages", JsonArray(messages.map { msg ->
                    buildJsonObject {
                        put("role", msg["role"]!!)
                        put("content", msg["content"]!!)
                    }
                }))
                put("stream", request.stream)
            }

            val response: HttpResponse = client.post("${config.ollamaUrl}/api/chat") {
                setBody(ollamaRequest.toString())
                contentType(io.ktor.http.ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            logger.info("Ollama response: $responseBody")
            
            val json = Json { ignoreUnknownKeys = true }
            val responseJson = json.parseToJsonElement(responseBody).jsonObject
            
            val messageObj = responseJson["message"]?.jsonObject
            val content = messageObj?.get("content")?.jsonPrimitive?.content ?: ""
            val role = messageObj?.get("role")?.jsonPrimitive?.content ?: "assistant"
            
            Result.success(ChatResponse(
                message = ChatMessage(role = role, content = content),
                model = request.model.ifEmpty { config.defaultModel }
            ))
        } catch (e: Exception) {
            logger.error("Error in chat: ${e.message}")
            Result.failure(e)
        }
    }

    private fun buildMessages(request: ChatRequest): List<Map<String, String>> {
        val messages = mutableListOf<Map<String, String>>()
        
        val systemPrompt = request.systemPrompt ?: run {
            val lastUserMessage = request.messages.lastOrNull { it.role == "user" }?.content ?: ""
            SystemPrompts.detectSpecialty(lastUserMessage)?.let { specialty ->
                SystemPrompts.getSpecialtyPrompt(specialty)
            } ?: SystemPrompts.DEFAULT
        }
        
        messages.add(mapOf("role" to "system", "content" to systemPrompt))
        
        request.messages.forEach { msg ->
            messages.add(mapOf("role" to msg.role, "content" to msg.content))
        }
        
        return messages
    }

    suspend fun getAvailableModels(): Result<List<String>> {
        return try {
            val response: HttpResponse = client.get("${config.ollamaUrl}/api/tags")
            val body = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val responseJson = json.parseToJsonElement(body).jsonObject
            
            val models = responseJson["models"]?.jsonArray?.map { modelObj ->
                modelObj.jsonObject["name"]?.jsonPrimitive?.content ?: ""
            }?.filter { it.isNotEmpty() } ?: emptyList()
            
            Result.success(models)
        } catch (e: Exception) {
            logger.error("Error getting models: ${e.message}")
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
