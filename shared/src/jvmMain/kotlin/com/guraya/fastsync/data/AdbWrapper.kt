package com.guraya.fastsync.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AdbWrapper {

    suspend fun executeAdbCommand(vararg commands: String) {
        withContext(Dispatchers.IO) {
            val list = mutableListOf<String>("adb")
            commands.onEach {
                list.add(it)
            }
            println(list)
            val processBuilder = ProcessBuilder(list)
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                println("Command completed successfully.")
            } else {
                println("Command failed with exit code $exitCode.")
            }
        }
    }

    suspend fun listDevices() {
        executeAdbCommand("devices", "")
    }

    suspend fun pushData(localPath: String, remoteDestPath: String) {
        executeAdbCommand("push", localPath, remoteDestPath)
    }

    suspend fun pullData(remotePath: String, localDestPath: String) {
        executeAdbCommand(
            "pull", remotePath, localDestPath
        )
    }

}