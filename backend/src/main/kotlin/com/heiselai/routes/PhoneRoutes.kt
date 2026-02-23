package com.heiselai.routes

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.phoneRoutes(config: AppConfig) {
    route("/api/phone") {
        post("/command") {
            val command = call.receive<PhoneCommand>()
            
            val response = handlePhoneCommand(command)
            call.respond(response)
        }
        
        get("/status") {
            call.respond(PhoneResponse(
                success = true,
                action = "status",
                result = mapOf(
                    "battery" to "85%",
                    "wifi" to "connected",
                    "location" to "enabled",
                    "bluetooth" to "enabled"
                ),
                message = "Estado del teléfono"
            ))
        }
    }
}

private fun handlePhoneCommand(command: PhoneCommand): PhoneResponse {
    return when (command.action.lowercase()) {
        "call" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("initiated" to true, "number" to (command.params["number"] ?: "")),
            message = "Llamada iniciada"
        )
        
        "sms", "message" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("sent" to true),
            message = "Mensaje enviado"
        )
        
        "open_app" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("app" to (command.params["package"] ?: "")),
            message = "App abierta"
        )
        
        "set_alarm" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("alarm_set" to true),
            message = "Alarma configurada"
        )
        
        "take_photo" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("photo_taken" to true),
            message = "Foto tomada"
        )
        
        "get_location" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("latitude" to 18.4861, "longitude" to -69.9312),
            message = "Ubicación obtenida"
        )
        
        "send_whatsapp" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("sent" to true),
            message = "Mensaje de WhatsApp enviado"
        )
        
        "post_social" -> PhoneResponse(
            success = true,
            action = command.action,
            result = mapOf("posted" to true),
            message = "Publicación enviada a red social"
        )
        
        else -> PhoneResponse(
            success = false,
            action = command.action,
            message = "Comando no reconocido: ${command.action}"
        )
    }
}
