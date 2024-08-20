package com.guraya.fastsync.model

sealed class Response<T>() {
    data class Success<T>(val data: T) : Response<T>()
    data class Error<T>(val data: T? = null, val throwable: Throwable) : Response<T>()
}