package com.guraya.fastsync.data

import com.guraya.fastsync.model.Response
import com.guraya.fastsync.platform.httpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AndroidSharesClient() : SharesClient() {

    suspend fun requestToPushShares(share: Share, savePath: String){
        val client = httpClient()
        client.webSocket(host = host, port = port, path = "/wsMobile"){
            // right now share request can be triggered through button so happen one share at a time
            // in future should handle list of shares
            val data = Pair(listOf(share.id),savePath)
            sendSerialized(data)
        }
        println("Android session closed")
    }

    override suspend fun getShares(client: HttpClient): Response<List<Share>> {
        return getDesktopShares(client)
    }

    override suspend fun getMyShares(client: HttpClient): Response<List<Share>> {
        return getSelfShares(client)
    }

    private suspend fun getDesktopShares(client: HttpClient): Response<List<Share>> {
        return try {
            val response = client.get(urlString = "${hostWithPort}/desktop_shares").body<List<Share>>()
            println(response as List<Share>)
            Response.Success(response)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }
    private suspend fun getSelfShares(client: HttpClient): Response<List<Share>> {
        return try {
            val response =
                client.get(urlString = "${hostWithPort}/mobile_shares").body<List<Share>>()
            println(response as List<Share>)
            Response.Success(response)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }

    override suspend fun createShares(shares: List<Share>, client: HttpClient): Response<Unit?> {
        return try {
            client.post(urlString = "${hostWithPort}/mobile_shares") {
                contentType(ContentType.Application.Json)
                setBody(shares)
            }
            Response.Success(null)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }

    override suspend fun deleteShares(listOfIds: List<Int>, client: HttpClient): Response<Unit?> {
        return try {
            val response =
                httpClient().delete(urlString = "${hostWithPort}/mobile_shares") {
                    val encodedData = Json.encodeToString(listOfIds)
                    parameter("id", encodedData)
                }
            println("Delete share response $response")
            Response.Success(null)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }
}