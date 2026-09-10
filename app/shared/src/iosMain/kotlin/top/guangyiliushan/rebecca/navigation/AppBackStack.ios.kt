package top.guangyiliushan.rebecca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberAppBackStack(): MutableList<Route> =
    remember { mutableStateListOf(Route.Study) }
