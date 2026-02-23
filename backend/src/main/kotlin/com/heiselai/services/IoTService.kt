package com.heiselai.services

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

class IoTService(private val config: AppConfig) {
    private val logger = LoggerFactory.getLogger(IoTService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val deviceMappings = mapOf(
        "luz" to "light",
        "luces" to "light",
        "light" to "light",
        "lights" to "light",
        "tomacorriente" to "switch",
        "tomacorrientes" to "switch",
        " outlet" to "switch",
        "puerta" to "lock",
        "puertas" to "lock",
        "door" to "lock",
        "doors" to "lock",
        "alarma" to "alarm",
        "alarmas" to "alarm",
        "alarm" to "alarm",
        "termostato" to "climate",
        "thermostat" to "climate",
        "cámara" to "camera",
        "camaras" to "camera",
        "camera" to "camera",
        "cerradura" to "lock"
    )

    private val actionMappings = mapOf(
        "encender" to "turn_on",
        "apagar" to "turn_off",
        "activar" to "turn_on",
        "desactivar" to "turn_off",
        "on" to "turn_on",
        "off" to "turn_off",
        "abrir" to "unlock",
        "cerrar" to "lock",
        "activar" to "arm",
        "desactivar" to "disarm"
    )

    suspend fun controlDevice(request: IoTRequest): Result<IoTResponse> {
        return try {
            if (config.homeAssistantUrl.isEmpty() || config.homeAssistantToken.isEmpty()) {
                return Result.success(simulateDeviceControl(request))
            }

            val entityId = getEntityId(request.device)
            val haAction = actionMappings[request.action.lowercase()] ?: request.action

            val serviceData = buildJsonObject {
                put("entity_id", entityId)
                request.value?.let { put("value", it.toString()) }
            }

            val service = "${getDomain(request.device)}.$haAction"

            val response: HttpResponse = client.post("${config.homeAssistantUrl}/api/services/${service}") {
                header("Authorization", "Bearer ${config.homeAssistantToken}")
                setBody(serviceData.toString())
                contentType(io.ktor.http.ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                Result.success(IoTResponse(
                    success = true,
                    device = request.device,
                    action = request.action,
                    message = "Dispositivo controlado exitosamente"
                ))
            } else {
                Result.failure(Exception("Error controlling device: ${response.status}"))
            }
        } catch (e: Exception) {
            logger.error("Error controlling IoT device: ${e.message}")
            Result.success(simulateDeviceControl(request))
        }
    }

    private fun simulateDeviceControl(request: IoTResponse): IoTResponse {
        val action = request.action.lowercase()
        val isOn = action == "encender" || action == "activar" || action == "on" || action == "arm"
        
        return IoTResponse(
            success = true,
            device = request.device,
            action = request.action,
            result = mapOf("state" to if (isOn) "on" else "off"),
            message = "Simulación: ${request.device} ${if (isOn) "encendido" else "apagado"}. " +
                    "Configura HA_URL y HA_TOKEN para control real."
        )
    }

    private fun getEntityId(device: String): String {
        val domain = getDomain(device)
        val cleanName = device.lowercase()
            .replace("la ", "")
            .replace("el ", "")
            .replace(" ", "_")
        return "$domain.$cleanName"
    }

    private fun getDomain(device: String): String {
        val cleanDevice = device.lowercase()
        return deviceMappings.entries.find { (key, _) -> 
            cleanDevice.contains(key) 
        }?.value ?: "switch"
    }

    suspend fun getDevices(): Result<List<String>> {
        return try {
            if (config.homeAssistantUrl.isEmpty()) {
                return Result.success(listOf(
                    "light.sala",
                    "light.dormitorio",
                    "switch.cocina",
                    "lock.puerta_principal",
                    "alarm.casa"
                ))
            }

            val response: HttpResponse = client.get("${config.homeAssistantUrl}/api/states") {
                header("Authorization", "Bearer ${config.homeAssistantToken}")
            }

            val responseBody = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val states = json.parseToJsonElement(responseBody).jsonArray

            val devices = states.mapNotNull { state ->
                state.jsonObject["entity_id"]?.jsonPrimitive?.content
            }

            Result.success(devices)
        } catch (e: Exception) {
            logger.error("Error getting devices: ${e.message}")
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
