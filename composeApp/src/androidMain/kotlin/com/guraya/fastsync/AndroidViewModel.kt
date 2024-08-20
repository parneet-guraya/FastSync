package com.guraya.fastsync

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.guraya.fastsync.data.AndroidSharesClient
import com.guraya.fastsync.data.Share
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch

class AndroidViewModel(private val androidSharesClient: AndroidSharesClient = AndroidSharesClient()) :
    MainViewModel(
        mainScreenState = MainScreenState(),
        sharesClient = androidSharesClient
    ) {

    override fun transfer(share: Share) {
        // send the request to desktop client to push shares
        viewModelScope.launch { androidSharesClient.requestToPushShares(share,savePath?:"storage/self/primary/Download") }
    }

    fun getFilePath(applicationContext: Context, uri: Uri): String? {
        val cursor = applicationContext.contentResolver.query(
            uri,
            null,
            null,
            null,
            null,
            null
        )
        var filePath: String? = null
        cursor.use { cur ->
            if (cur != null) {
                val filePathIndex =
                    cur.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                if (cur.moveToFirst()) {
                    filePath = cur.getString(filePathIndex)
                }
            }
        }
        return filePath
    }

    override fun chooseDirectory() {
        viewModelScope.launch {
            val platformDir = FileKit.pickDirectory("Choose Save Folder")
            if (platformDir != null) {
                val path = platformDir.path?.substringAfter(":")
                savePath = "storage/self/primary/$path"
                println(savePath)
            }
        }
    }
}