package top.guangyiliushan.rebecca.navigation

import androidx.compose.runtime.Composable

/** 平台差异：web 端接浏览器历史（navigation3-browser），其余端进程内 SnapshotStateList（F-L2）。 */
@Composable
expect fun rememberAppBackStack(): MutableList<Route>
