package com.example.heiselai

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay

class AiRepositoryImpl : AiRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    override suspend fun getAiResponse(prompt: String): String {
        // Simulate a network call
        delay(1000)
        return "This is a simulated response to: $prompt"
    }
}
