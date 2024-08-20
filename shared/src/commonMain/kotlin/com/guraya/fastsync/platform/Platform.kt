package com.guraya.fastsync.platform

interface Platform {
    val platform: PLATFORM
}

enum class PLATFORM{
    MOBILE,
    DESKTOP
}

expect fun getPlatform(): Platform