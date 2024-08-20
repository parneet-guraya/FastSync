package com.guraya.fastsync

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.FastSyncTheme
import com.guraya.fastsync.data.Share
import fastsync.composeapp.generated.resources.Res
import fastsync.composeapp.generated.resources.description_24
import fastsync.composeapp.generated.resources.download_24
import fastsync.composeapp.generated.resources.folder_24
import fastsync.composeapp.generated.resources.refresh_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(viewModel: MainViewModel, onFabClick: () -> Unit = { viewModel.addShares() }) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle(lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current)
    val snackbarHostState = remember { SnackbarHostState() }
    FastSyncTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Scaffold(snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
                topBar = { TopAppBar(title = { Text("Fast Sync") }) },
                content = { padding ->
                    println(padding)
                    MainScreen(
                        modifier = Modifier.padding(padding).padding(start = 16.dp, end = 16.dp),
                        screenState = screenState,
                        snackbarHostState = snackbarHostState,
                        onGetShares = { viewModel.getShares() },
                        onTransfer = { viewModel.transfer(share = it) },
                        onHostUpdate = { host, port -> viewModel.updateLocalHost(host, port) },
                        onChooseDirectory = { viewModel.chooseDirectory() }

                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = onFabClick,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    screenState: MainScreenState,
    snackbarHostState: SnackbarHostState,
    onGetShares: () -> Unit,
    onTransfer: (share: Share) -> Unit,
    onHostUpdate: (host: String, port: String) -> Unit,
    onChooseDirectory: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        AddHostField(modifier = Modifier) { host, port ->
            onHostUpdate(host, port)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = "Shares",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onChooseDirectory) {
                Icon(painter = painterResource(Res.drawable.folder_24), null)
            }
            IconButton(onClick = onGetShares) {
                Icon(painter = painterResource(Res.drawable.refresh_24), null)
            }
        }

        screenState.screenData.let { mainScreenData ->
            if (screenState.loading) {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize())
            } else {
                mainScreenData.sharesList?.let {
                    // bottom padding shouldn't be hardcoded
                    LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                        items(it) {
                            ShareItem(title = it.name) {
                                onTransfer(it)
                            }
                        }
                    }
                }
            }

            if (mainScreenData.isUploadingShares) {
                // showing loading dialog
                LoadingDialog()
            }

            if (mainScreenData.isUploadSuccess) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Upload Success",
                        withDismissAction = true
                    )
                }
            }
        }

        screenState.errorMessage?.let {
            Text(text = it)
        }
    }
}

@Composable
fun ShareItem(modifier: Modifier = Modifier, title: String, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().wrapContentSize(),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painter = painterResource(Res.drawable.description_24), null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(2.dp))
            IconButton(onClick = onClick) {
                Icon(painter = painterResource(Res.drawable.download_24), null)
            }
        }
    }
}


@Composable
fun AddHostField(
    modifier: Modifier = Modifier,
    onUpdateHost: (host: String, port: String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var hostFieldValue by rememberSaveable {
        mutableStateOf("")
    }
    var portFieldValue by rememberSaveable {
        mutableStateOf("8080")
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(modifier = Modifier.weight(.4f),
            value = hostFieldValue,
            placeholder = { Text(text = "Local Host") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            onValueChange = {
                hostFieldValue = it
            })
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            modifier = Modifier.weight(.3f),
            value = portFieldValue,
            placeholder = { Text(text = "Port") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            onValueChange = {
                portFieldValue = it
            })
        Spacer(modifier = Modifier.width(8.dp))
        Button(modifier = Modifier.weight(.3f), onClick = {
            onUpdateHost(
                hostFieldValue,
                portFieldValue
            )
        }) {
            Text("Update")
        }
    }
}

@Composable
fun LoadingDialog() =
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularProgressIndicator()
                Text("Uploading...")
            }
        }
    }