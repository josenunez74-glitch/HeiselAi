package com.example.heiselai

interface AiRepository {
    suspend fun getAiResponse(prompt: String): String
}
