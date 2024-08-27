package com.guraya.fastsync.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SharesTabRow(
    destination: List<ShareDestination>,
    onSelected: (ShareDestination) -> Unit,
    currentScreen: ShareDestination
) {

    TabRow(selectedTabIndex = destination.indexOf(currentScreen)) {
        destination.onEach { destination ->
            Tab(
                modifier = Modifier.height(56.dp),
                selected = currentScreen == destination,
                onClick = { onSelected(destination) },
                interactionSource = remember { MutableInteractionSource() }) {
                Text(destination.name)
            }
        }
    }

}

sealed interface ShareDestination {
    val name: String
    val route: String
}

data object Shares : ShareDestination {
    override val name: String
        get() = "Shares"
    override val route: String
        get() = "route_shares"
}

data object MyShares : ShareDestination {
    override val name: String
        get() = "My Shares"
    override val route: String
        get() = "route_myShares"
}

val mainScreenDestinations = listOf(
    Shares, MyShares
)
