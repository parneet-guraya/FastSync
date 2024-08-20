package com.guraya.fastsync.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(OkHttp.create(), config)
}
