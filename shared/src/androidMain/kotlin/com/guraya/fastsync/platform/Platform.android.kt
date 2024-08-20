package com.guraya.fastsync.platform


actual fun getPlatform(): Platform {
    return object: Platform{
        override val platform: PLATFORM
            get() = PLATFORM.MOBILE
    }
}