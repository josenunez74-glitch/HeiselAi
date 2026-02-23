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

class SearchService(private val config: AppConfig) {
    private val logger = LoggerFactory.getLogger(SearchService::class.java)
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun search(query: String, maxResults: Int = 5): Result<SearchResponse> {
        return try {
            if (config.searchApiKey.isEmpty()) {
                return Result.success(fallbackSearch(query, maxResults))
            }

            val searchRequest = buildJsonObject {
                put("api_key", config.searchApiKey)
                put("query", query)
                put("max_results", maxResults)
                put("include_answer", true)
                put("include_raw_content", false)
            }

            val response: HttpResponse = client.post("${config.searchApiUrl}/search") {
                setBody(searchRequest.toString())
                contentType(io.ktor.http.ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val responseJson = json.parseToJsonElement(responseBody).jsonObject
            
            val results = responseJson["results"]?.jsonArray?.mapNotNull { result ->
                try {
                    val obj = result.jsonObject
                    SearchResult(
                        title = obj["title"]?.jsonPrimitive?.content ?: "",
                        url = obj["url"]?.jsonPrimitive?.content ?: "",
                        content = obj["content"]?.jsonPrimitive?.content ?: "",
                        score = obj["score"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            Result.success(SearchResponse(
                results = results,
                query = query
            ))
        } catch (e: Exception) {
            logger.error("Error searching: ${e.message}")
            Result.success(fallbackSearch(query, maxResults))
        }
    }

    private fun fallbackSearch(query: String, maxResults: Int): SearchResponse {
        val mockResults = listOf(
            SearchResult(
                title = "Búsqueda: $query",
                url = "https://www.google.com/search?q=${query.replace(" ", "+")}",
                content = "Resultados de búsqueda para: $query. Configura SEARCH_API_KEY para obtener resultados reales.",
                score = 1.0
            )
        )
        return SearchResponse(results = mockResults, query = query)
    }

    fun close() {
        client.close()
    }
}
