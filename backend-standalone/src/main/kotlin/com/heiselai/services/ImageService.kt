package com.heiselai.services

import com.heiselai.config.AppConfig
import com.heiselai.models.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.util.Base64
import kotlin.random.Random

class ImageService(private val config: AppConfig) {
    private val logger = LoggerFactory.getLogger(ImageService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun generateImage(request: ImageRequest): Result<ImageResponse> {
        return try {
            val sdRequest = buildJsonObject {
                put("prompt", request.prompt)
                put("negative_prompt", request.negativePrompt)
                put("width", request.width)
                put("height", request.height)
                put("steps", request.steps)
                put("cfg_scale", request.cfgScale)
                put("seed", Random.nextLong())
            }

            val response: HttpResponse = client.post("${config.stableDiffusionUrl}/sdapi/v1/txt2img") {
                setBody(sdRequest.toString())
                contentType(io.ktor.http.ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val responseJson = json.parseToJsonElement(responseBody).jsonObject
            
            val base64Image = responseJson["images"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.content
                ?: throw Exception("No image generated")

            Result.success(ImageResponse(
                imageBase64 = base64Image,
                seed = Random.nextLong(),
                model = request.model
            ))
        } catch (e: Exception) {
            logger.error("Error generating image: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun generateImageWithOllama(prompt: String): Result<ImageResponse> {
        return try {
            val ollamaRequest = buildJsonObject {
                put("model", config.imageModel)
                put("prompt", prompt)
                put("stream", false)
            }

            val response: HttpResponse = client.post("${config.ollamaUrl}/api/generate") {
                setBody(ollamaRequest.toString())
                contentType(io.ktor.http.ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val responseJson = json.parseToJsonElement(responseBody).jsonObject
            
            val base64Image = responseJson["response"]?.jsonPrimitive?.content
                ?: throw Exception("No image generated")

            Result.success(ImageResponse(
                imageBase64 = base64Image,
                seed = Random.nextLong(),
                model = config.imageModel
            ))
        } catch (e: Exception) {
            logger.error("Error generating image with Ollama: ${e.message}")
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
