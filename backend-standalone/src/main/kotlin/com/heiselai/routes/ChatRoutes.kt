package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import com.heiselai.services.ChatService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.chatRoutes(config: AppConfig) {
    val chatService = ChatService(config)
    
    route("/api/chat") {
        post {
            val request = call.receive<ChatRequest>()
            val result = withContext(Dispatchers.IO) {
                chatService.chat(request)
            }
            
            result.fold(
                onSuccess = { response ->
                    call.respond(response)
                },
                onFailure = { error ->
                    call.respond(500, mapOf("error" to (error.message ?: "Unknown error")))
                }
            )
        }
    }
    
    route("/api/models") {
        get {
            val result = withContext(Dispatchers.IO) {
                chatService.getAvailableModels()
            }
            
            result.fold(
                onSuccess = { models ->
                    call.respond(mapOf("models" to models))
                },
                onFailure = { error ->
                    call.respond(500, mapOf("error" to (error.message ?: "Unknown error")))
                }
            )
        }
    }
}
