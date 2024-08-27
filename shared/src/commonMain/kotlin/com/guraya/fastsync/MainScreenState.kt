package com.guraya.fastsync

import com.guraya.fastsync.data.Share

data class MainScreenState(
    val screenData: MainScreenData = MainScreenData(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
)

data class MainScreenData(
    val sharesList: List<Share>? = null,
    val selfSharesList: List<Share>? = null,
    val isUploadingShares: Boolean = false,
    val isUploadSuccess: Boolean = false
)