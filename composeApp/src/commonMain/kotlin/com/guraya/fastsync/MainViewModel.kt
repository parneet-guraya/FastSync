package com.guraya.fastsync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guraya.fastsync.data.Share
import com.guraya.fastsync.data.SharesClient
import com.guraya.fastsync.model.Response
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MainViewModel(
    mainScreenState: MainScreenState,
    val sharesClient: SharesClient
) : ViewModel() {

    var _screenState = MutableStateFlow(mainScreenState)
    val screenState = _screenState.asStateFlow()

    var savePath: String? = null


    fun getShares() {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(loading = true, screenData = _screenState.value.screenData.copy(isUploadingShares = false, isUploadSuccess = false))
            val response = sharesClient.getShares()
            _screenState.value =_screenState.value.copy(loading = false, screenData = _screenState.value.screenData.copy(isUploadingShares = false, isUploadSuccess = false))
            when (response) {
                is Response.Success -> _screenState.value =
                    _screenState.value.copy(
                        loading = false,
                        screenData = _screenState.value.screenData.copy(sharesList = response.data, isUploadingShares = false, isUploadSuccess = false),
                        errorMessage = null
                    )

                is Response.Error -> _screenState.value =
                    _screenState.value.copy(
                        loading = false,
                        screenData = _screenState.value.screenData.copy(sharesList = null, isUploadingShares = false, isUploadSuccess = false),
                        errorMessage = response.throwable.message
                    )
            }

        }
    }

      fun getMyShares() {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(loading = true, screenData = _screenState.value.screenData.copy(isUploadingShares = false, isUploadSuccess = false))
            val response = sharesClient.getMyShares()
            _screenState.value =_screenState.value.copy(loading = false, screenData = _screenState.value.screenData.copy(isUploadingShares = false, isUploadSuccess = false))
            when (response) {
                is Response.Success -> _screenState.value =
                    _screenState.value.copy(
                        loading = false,
                        screenData = _screenState.value.screenData.copy(selfSharesList = response.data, isUploadingShares = false, isUploadSuccess = false),
                        errorMessage = null
                    )

                is Response.Error -> _screenState.value =
                    _screenState.value.copy(
                        loading = false,
                        screenData = _screenState.value.screenData.copy(sharesList = null, isUploadingShares = false, isUploadSuccess = false),
                        errorMessage = response.throwable.message
                    )
            }

        }
    }



    open fun transfer(share: Share) {}

    open fun chooseDirectory() {
        viewModelScope.launch {
            savePath = FileKit.pickDirectory()?.path
        }
    }

    open fun addShares() {
        viewModelScope.launch {
            // wrapper around platform files
            try {
                val platformFiles = FileKit.pickFile(
                    mode = PickerMode.Multiple(),
                    title = "Pick File"
                )
                if (platformFiles != null) {
                    uploadShares(platformFiles.map {
                        Share(it.name, it.path?.substringBeforeLast("/")!!)
                    })

                }
            } catch (e: Exception) {
                _screenState.value =
                    _screenState.value.copy(errorMessage = "Error Picking files ${e.message}")
            }
        }

    }

    fun uploadShares(shares: List<Share>) {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(
                screenData = _screenState.value.screenData.copy(isUploadingShares = true)
            )

            try {
                sharesClient.createShares(shares.map {
                    Share(name = it.name, path = it.path!!)
                })
                _screenState.value = _screenState.value.copy(
                    screenData = _screenState.value.screenData.copy(
                        isUploadingShares = false,
                        isUploadSuccess = true
                    )
                )
            } catch (e: Exception) {
                _screenState.value = _screenState.value.copy(
                    screenData = _screenState.value.screenData.copy(
                        isUploadingShares = false,
                        isUploadSuccess = false
                    ),
                    errorMessage = "Uploading Error: ${e.message}"
                )
            }
        }
    }

fun deleteShares(listOfShareIds: List<Int>) {
        viewModelScope.launch {
//            _screenState.value = _screenState.value.copy(
//                screenData = _screenState.value.screenData.copy(isUploadingShares = true)
//            )

            try {
                sharesClient.deleteShares(listOfShareIds)
//                _screenState.value = _screenState.value.copy(
//                    screenData = _screenState.value.screenData.copy(
//                        isUploadingShares = false,
//                        isUploadSuccess = true
//                    )
//                )
            } catch (e: Exception) {
//                _screenState.value = _screenState.value.copy(
//                    screenData = _screenState.value.screenData.copy(
//                        isUploadingShares = false,
//                        isUploadSuccess = false
//                    ),
//                    errorMessage = "Uploading Error: ${e.message}"
//                )
            }
        }
    }

    open fun updateLocalHost(host: String, port: String) {
        sharesClient.host = host
        sharesClient.port = port.toInt()
    }

}