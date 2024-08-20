package com.guraya.fastsync

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.guraya.fastsync.data.Share
import io.github.vinceglb.filekit.core.FileKit
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


class MainActivity : ComponentActivity() {
    private val filePickerLauncher: ActivityResultLauncher<Intent> = onReadFiles()
    private val viewModel = AndroidViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileKit.init(this)
        setContent {
            App(viewModel = viewModel, onFabClick = { launchFilePicker() })
        }
    }

    private fun onReadFiles(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val intent = activityResult.data
                if (intent != null) {
                    // if intent.data is null meaning multiple files and use clipData
                    // if clipData is null, single file and use intent.data
                    val filesPathList = mutableListOf<String>()
                    if (intent.data != null) {
                        // single selection
                        println("intent.data.path ${intent.data?.path}")
                        filesPathList.add(intent.data?.path!!.substringAfter(":"))
                    } else if (intent.clipData != null) {
                        //multiple selections
                        for (i in 0..intent.clipData?.itemCount!!) {
                            val encodedPath = intent.clipData?.getItemAt(0)?.uri
                            val decodedPath = URLDecoder.decode(
                                encodedPath.toString(),
                                StandardCharsets.UTF_8.name()
                            )

                            filesPathList.add(decodedPath.substringAfter(":"))
                        }

                    }
                    filesPathList.forEachIndexed { i, path ->
                        println("file $path")
                        filesPathList[i] = "storage/self/primary/$path"
                        println(filesPathList)
                    }
                    viewModel.uploadShares(filesPathList.map {
                        Share(
                            File(it).name,
                            it.substringBeforeLast("/")
                        )
                    })
                }
            }

        }
    }

    private fun launchFilePicker() {
        val subPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        filePickerLauncher.launch(subPickerIntent)
    }
}

@Preview(showBackground = true)
@Composable
private fun FieldPreview() {
    ShareItem(title = "File.txt") {}
}

