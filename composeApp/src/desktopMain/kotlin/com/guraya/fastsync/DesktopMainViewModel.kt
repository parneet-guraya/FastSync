package com.guraya.fastsync

import androidx.lifecycle.viewModelScope
import com.guraya.fastsync.data.AdbWrapper
import com.guraya.fastsync.data.DesktopSharesClient
import com.guraya.fastsync.data.Share
import kotlinx.coroutines.launch

class DesktopMainViewModel(private val desktopSharesClient: DesktopSharesClient = DesktopSharesClient()) :
    MainViewModel(
        mainScreenState = MainScreenState(),
        sharesClient = desktopSharesClient
    ) {

    override fun transfer(share: Share) {
        viewModelScope.launch {
            try {
                val userName = System.getProperty("user.name")
                AdbWrapper.pullData(
                    remotePath = "${share.path}/${share.name}",
                    savePath ?: "/home/$userName/Downloads"
                )
                println("data pulling finished")
            } catch (e: Exception) {
                println("Error while pulling $e")
            }
        }
    }

    private suspend fun pushData(share: Share, mobileSavePath: String) {
        try {
            AdbWrapper.pushData(
                localPath = "${share.path}/${share.name}",
                //TODO: use default path for android here
                remoteDestPath = mobileSavePath ?: "storage/self/primary/Download"
            )
            println("data pushing finished")
        } catch (e: Exception) {
            println("Error while pushing $e")
        }
    }

    override fun updateLocalHost(host: String, port: String) {
        super.updateLocalHost(host, port)
        viewModelScope.launch {
            desktopSharesClient.connectToWebSocket { listOfShares, savePath ->
                listOfShares.onEach { share ->
                    pushData(share, savePath)
                }
            }
        }
    }
}