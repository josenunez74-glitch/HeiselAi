package com.heiselai

import com.heiselai.config.AppConfig
import com.heiselai.routes.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.plugins.callloging.*
import io.ktor.serialization.gson.*
import org.slf4j.LoggerFactory

fun main() {
    val config = AppConfig.load()
    
    embeddedServer(Netty, port = config.port) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        
        install(CORS) {
            anyHost()
            allowMethod(io.ktor.http.HttpMethod.Get)
            allowMethod(io.ktor.http.HttpMethod.Post)
            allowMethod(io.ktor.http.HttpMethod.Put)
            allowMethod(io.ktor.http.HttpMethod.Delete)
        }
        
        install(CallLogging) {
            logger = LoggerFactory.getLogger("ktor.application")
        }
        
        install(WebSockets)
        
        routing {
            chatRoutes(config)
            imageRoutes(config)
            searchRoutes(config)
            iotRoutes(config)
            phoneRoutes(config)
            healthRoute()
        }
    }.start(wait = true)
}
