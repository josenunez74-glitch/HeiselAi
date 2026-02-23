package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import com.heiselai.services.IoTService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.iotRoutes(config: AppConfig) {
    val iotService = IoTService(config)
    
    route("/api/iot") {
        post("/control") {
            val request = call.receive<IoTRequest>()
            val result = withContext(Dispatchers.IO) {
                iotService.controlDevice(request)
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
        
        get("/devices") {
            val result = withContext(Dispatchers.IO) {
                iotService.getDevices()
            }
            
            result.fold(
                onSuccess = { devices ->
                    call.respond(mapOf("devices" to devices))
                },
                onFailure = { error ->
                    call.respond(500, mapOf("error" to (error.message ?: "Unknown error")))
                }
            )
        }
    }
}
