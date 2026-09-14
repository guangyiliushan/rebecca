package top.guangyiliushan.rebecca.feature.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import top.guangyiliushan.rebecca.design.components.AppBadge
import top.guangyiliushan.rebecca.design.components.AppButton
import top.guangyiliushan.rebecca.design.components.AppButtonVariant
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.components.AppCardContent
import top.guangyiliushan.rebecca.design.components.AppCardHeader
import top.guangyiliushan.rebecca.design.components.AppChip
import top.guangyiliushan.rebecca.design.components.AppChipGroup
import top.guangyiliushan.rebecca.design.components.AppChipGroupMode
import top.guangyiliushan.rebecca.design.components.AppChipGroupSpacing
import top.guangyiliushan.rebecca.design.components.AppField
import top.guangyiliushan.rebecca.design.components.AppIconButton
import top.guangyiliushan.rebecca.design.components.AppInput
import top.guangyiliushan.rebecca.design.components.AppListGroup
import top.guangyiliushan.rebecca.design.components.AppListRow
import top.guangyiliushan.rebecca.design.components.AppProgress
import top.guangyiliushan.rebecca.design.components.AppSpinner
import top.guangyiliushan.rebecca.design.overlays.AppAlertDialog
import top.guangyiliushan.rebecca.design.overlays.AppDialog
import top.guangyiliushan.rebecca.design.overlays.AppDialogBody
import top.guangyiliushan.rebecca.design.overlays.AppDialogFooter
import top.guangyiliushan.rebecca.design.overlays.AppDialogHeader
import top.guangyiliushan.rebecca.design.overlays.AppSheet
import top.guangyiliushan.rebecca.design.overlays.AppSheetFooter
import top.guangyiliushan.rebecca.design.overlays.AppSheetHeader
import top.guangyiliushan.rebecca.design.showcase.TokensShowcase
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeMode
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/** 组件陈列区小节标题。 */
@Composable
private fun Section(title: String) {
    Text(
        text = title,
        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
        color = AppTheme.colors.onBackground,
        modifier = Modifier,
    )
}

/**
 * Showcase debug route 入口（0.1.0 升级）：tokens 陈列 + 14 组件陈列。
 * 运行时切换 light/dark（TokensShowcase 内）与弹层演示开关。
 */
@Composable
fun ShowcaseScreen(settings: ThemeSettings, onModeChange: (ThemeMode) -> Unit) {
    var dialogOpen by remember { mutableStateOf(false) }
    var alertOpen by remember { mutableStateOf(false) }
    var sheetOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        TokensShowcase(settings = settings, onModeChange = onModeChange)

        Section("Components")

        Section("AppButton")
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppButton(text = "Primary", onClick = {})
            AppButton(text = "Ghost", variant = AppButtonVariant.Ghost, onClick = {})
            AppButton(text = "Outline", variant = AppButtonVariant.Outline, onClick = {})
            AppButton(text = "Loading", onClick = {}, loading = true)
        }

        Section("AppIconButton / AppSpinner / AppBadge")
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppIconButton(onClick = {}, icon = { Text("✕") }, contentDescription = "Close")
            AppSpinner(contentDescription = "Loading")
            AppBadge { Text("New") }
        }

        Section("AppChip")
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppChip(label = { Text("Verb") }, selected = true, onClick = {})
            AppChip(label = { Text("Noun") }, selected = false, onClick = {})
        }

        Section("AppChipGroup")
        AppChipGroup(mode = AppChipGroupMode.Single, spacing = AppChipGroupSpacing.Separated) {
            AppChip(label = { Text("Verb") }, selected = true, onClick = {})
            AppChip(label = { Text("Noun") }, selected = false, onClick = {})
        }
        AppChipGroup(mode = AppChipGroupMode.Single, spacing = AppChipGroupSpacing.Connected) {
            AppChip(label = { Text("Prev") }, selected = false, onClick = {})
            AppChip(label = { Text("Next") }, selected = true, onClick = {})
        }
        AppChipGroup(mode = AppChipGroupMode.Multiple, spacing = AppChipGroupSpacing.Separated) {
            AppChip(label = { Text("Alpha") }, selected = true, onClick = {})
            AppChip(label = { Text("Beta") }, selected = true, onClick = {})
        }

        Section("AppCard")
        AppCard {
            AppCardHeader(title = { Text("Title") }, description = { Text("Description") })
            AppCardContent { Text("Content") }
        }

        Section("AppField + AppInput")
        AppField(label = { Text("Name") }, errors = listOf("Required")) {
            AppInput(value = "demo", onValueChange = {})
        }

        Section("AppListRow")
        AppListGroup {
            AppListRow(title = { Text("run") }, description = { Text("move fast") }, onClick = {})
            AppListRow(title = { Text("port") }, selected = true, onClick = {})
        }

        Section("AppProgress")
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppProgress(progress = 0.5f, label = "50%")
            AppProgress(progress = null)
        }

        Section("Overlays")
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppButton(text = "Dialog", onClick = { dialogOpen = true })
            AppButton(text = "AlertDialog", onClick = { alertOpen = true })
            AppButton(text = "Sheet", onClick = { sheetOpen = true })
        }
    }

    AppDialog(open = dialogOpen, onDismiss = { dialogOpen = false }) {
        AppDialogHeader(title = { Text("Dialog") })
        AppDialogBody { Text("Body") }
        AppDialogFooter(
            confirm = { AppButton(text = "OK", onClick = { dialogOpen = false }) },
            dismiss = { AppButton(text = "Cancel", variant = AppButtonVariant.Ghost, onClick = { dialogOpen = false }) },
        )
    }
    AppAlertDialog(
        open = alertOpen,
        onDismiss = { alertOpen = false },
        title = { Text("Delete?") },
        text = { Text("This cannot be undone.") },
        confirmLabel = "Delete",
        onConfirm = { alertOpen = false },
        dismissLabel = "Cancel",
    )
    AppSheet(open = sheetOpen, onDismiss = { sheetOpen = false }) {
        AppSheetHeader(title = { Text("Actions") })
        AppSheetFooter {
            AppButton(text = "Done", onClick = { sheetOpen = false })
        }
    }
}
