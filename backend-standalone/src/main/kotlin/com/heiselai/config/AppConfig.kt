package com.heiselai.config

data class AppConfig(
    val port: Int = 8080,
    val ollamaUrl: String = "http://localhost:11434",
    val stableDiffusionUrl: String = "http://localhost:7860",
    val searchApiUrl: String = "https://api.tavily.com",
    val searchApiKey: String = "",
    val homeAssistantUrl: String = "",
    val homeAssistantToken: String = "",
    val defaultModel: String = "llama3",
    val imageModel: String = "llava",
    val contextLength: Int = 4096
) {
    companion object {
        fun load(): AppConfig {
            return AppConfig(
                port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
                ollamaUrl = System.getenv("OLLAMA_URL") ?: "http://localhost:11434",
                stableDiffusionUrl = System.getenv("SD_URL") ?: "http://localhost:7860",
                searchApiKey = System.getenv("SEARCH_API_KEY") ?: "",
                homeAssistantUrl = System.getenv("HA_URL") ?: "",
                homeAssistantToken = System.getenv("HA_TOKEN") ?: "",
                defaultModel = System.getenv("DEFAULT_MODEL") ?: "llama3",
                imageModel = System.getenv("IMAGE_MODEL") ?: "llava"
            )
        }
    }
}
