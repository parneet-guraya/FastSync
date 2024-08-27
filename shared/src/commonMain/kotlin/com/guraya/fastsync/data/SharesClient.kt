package com.guraya.fastsync.data

import com.guraya.fastsync.model.Response
import com.guraya.fastsync.platform.httpClient
import io.ktor.client.HttpClient


open class SharesClient() {
    var host: String? = null
    var port: Int? = null
    val hostWithPort: String?
        get() = "http://$host:$port"

    open suspend fun getShares(client: HttpClient = httpClient()): Response<List<Share>> {
        return Response.Success(emptyList())
    }


    open suspend fun getMyShares(client: HttpClient = httpClient()): Response<List<Share>> {
        return Response.Success(emptyList())
    }


    open suspend fun createShares(
        shares: List<Share>,
        client: HttpClient = httpClient()
    ): Response<Unit?> {
        return Response.Success(null)
    }
}