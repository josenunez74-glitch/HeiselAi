package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import com.heiselai.services.SearchService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.searchRoutes(config: AppConfig) {
    val searchService = SearchService(config)
    
    route("/api/search") {
        post {
            val request = call.receive<SearchRequest>()
            val result = withContext(Dispatchers.IO) {
                searchService.search(request.query, request.maxResults)
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
        
        get("/web") {
            val query = call.parameters["q"] ?: ""
            val maxResults = call.parameters["max"]?.toIntOrNull() ?: 5
            
            val result = withContext(Dispatchers.IO) {
                searchService.search(query, maxResults)
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
