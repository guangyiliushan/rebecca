package top.guangyiliushan.rebecca.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.nav_dictionary
import rebecca.app.shared.generated.resources.nav_explore
import rebecca.app.shared.generated.resources.nav_lists
import rebecca.app.shared.generated.resources.nav_settings
import rebecca.app.shared.generated.resources.nav_study

data class AppNavItem(
    val route: Route,
    val labelRes: StringResource,
    val icon: ImageVector,
)

val topLevelNavItems: List<AppNavItem> = listOf(
    AppNavItem(Route.Study, Res.string.nav_study, Icons.Filled.Home),
    AppNavItem(Route.Lists, Res.string.nav_lists, Icons.AutoMirrored.Filled.List),
    AppNavItem(Route.Dictionary, Res.string.nav_dictionary, Icons.Filled.Search),
    AppNavItem(Route.Explore, Res.string.nav_explore, Icons.Filled.Star),
    AppNavItem(Route.Settings, Res.string.nav_settings, Icons.Filled.Settings),
)
