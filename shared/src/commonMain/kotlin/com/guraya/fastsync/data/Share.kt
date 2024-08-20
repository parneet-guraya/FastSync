package com.guraya.fastsync.data

import kotlinx.serialization.Serializable

@Serializable
data class Share(val name: String, val path: String, val id: Int? = null)