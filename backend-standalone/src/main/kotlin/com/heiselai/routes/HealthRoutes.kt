package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

fun Route.healthRoute() {
    route("/api") {
        get("/health") {
            val services = mapOf(
                "status" to "ok",
                "timestamp" to System.currentTimeMillis().toString()
            )
            
            call.respond(HealthResponse(
                status = "ok",
                services = services
            ))
        }
    }
}
