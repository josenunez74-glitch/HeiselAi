package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import com.heiselai.services.ImageService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Base64

fun Route.imageRoutes(config: AppConfig) {
    val imageService = ImageService(config)
    
    route("/api/image") {
        post("/generate") {
            val request = call.receive<ImageRequest>()
            val result = withContext(Dispatchers.IO) {
                imageService.generateImage(request)
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
        
        post("/generate/ollama") {
            val params = call.receiveParameters()
            val prompt = params["prompt"] ?: ""
            
            val result = withContext(Dispatchers.IO) {
                imageService.generateImageWithOllama(prompt)
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
}
