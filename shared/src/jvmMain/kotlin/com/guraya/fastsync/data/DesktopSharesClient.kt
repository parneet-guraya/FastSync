package com.guraya.fastsync.data

import com.guraya.fastsync.model.Response
import com.guraya.fastsync.platform.httpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.deserialize
import io.ktor.websocket.FrameType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DesktopSharesClient : SharesClient() {

    suspend fun connectToWebSocket(onRequestToTransfer: suspend (listOfShares: List<Share>, savePath: String) -> Unit) {
        // keep the connection to websocket active
        try {
            val client = httpClient()
            client.webSocket(host = host, port = port, path = "/wsDesktop") {

                for (frame in incoming) {
                    if (frame.frameType == FrameType.TEXT || frame.frameType == FrameType.BINARY) {
                        println("Frame received ${frame.frameType}")
                        val desktopSharesIdsList =
                            converter?.deserialize<Pair<List<Int>, String>?>(frame)
                        println("Data received $desktopSharesIdsList")
                        if (desktopSharesIdsList != null) {
                            val sharesToTransferResponse =
                                getSharesWithId(desktopSharesIdsList.first)
                            when (sharesToTransferResponse) {
                                is Response.Success -> onRequestToTransfer(
                                    sharesToTransferResponse.data,
                                    desktopSharesIdsList.second
                                )

                                is Response.Error -> println("Error occured while getting shares with Ids ${sharesToTransferResponse.throwable.message}")

                            }

                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error while receiving websocket data ${e.message}")
        }
        println("Desktop websocket connection closed")
    }

    override suspend fun getShares(client: HttpClient): Response<List<Share>> {
        return getMobileShares(client)
    }

    override suspend fun getMyShares(client: HttpClient): Response<List<Share>> {
        return getSelfShares(client)
    }

    private suspend fun getMobileShares(client: HttpClient): Response<List<Share>> {
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

    private suspend fun getSelfShares(client: HttpClient): Response<List<Share>> {
        return try {
            val response =
                client.get(urlString = "${hostWithPort}/desktop_shares").body<List<Share>>()
            println(response as List<Share>)
            Response.Success(response)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }

    override suspend fun createShares(shares: List<Share>, client: HttpClient): Response<Unit?> {
        return try {
            client.post(urlString = "${hostWithPort}/desktop_shares") {
                contentType(ContentType.Application.Json)
                setBody(shares)
            }
            Response.Success(null)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }

    suspend fun getSharesWithId(listOfIds: List<Int>): Response<List<Share>> {
        return try {
            val response =
                httpClient().get(urlString = "${hostWithPort}/desktop_shares") {
                    val encodedData = Json.encodeToString(listOfIds)
                    parameter("getWithIds", encodedData)
                }.body<List<Share>>()
            println(response as List<Share>)
            Response.Success(response)
        } catch (e: Exception) {
            println("Error $e")
            Response.Error(throwable = e)
        }
    }
}