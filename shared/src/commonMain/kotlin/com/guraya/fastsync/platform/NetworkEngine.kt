package com.guraya.fastsync.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect fun httpClient(
    config: HttpClientConfig<*>.() -> Unit = {
        install(ContentNegotiation){
            json(json = Json { ignoreUnknownKeys = true})
        }
        install(WebSockets){
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
): HttpClient
