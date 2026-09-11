package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import rebecca.app.shared.generated.resources.Res
import rebecca.app.shared.generated.resources.state_selected
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppListRow 变体（R2；对齐官方 item.tsx）。 */
enum class AppListRowVariant { Default, Outline, Muted }

/** AppListRow 尺寸（官方 size: default/sm）。 */
enum class AppListRowSize { Default, Sm }

/**
 * 列表组（官方 ItemGroup 的 role="list" 等价物）。CMP Role 枚举无 List（v0.3 §10.2）→ collectionInfo。
 */
@Composable
fun AppListGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.semantics { collectionInfo = androidx.compose.ui.semantics.CollectionInfo(-1, -1) },
        content = content,
    )
}

/**
 * 列表行（frontend-design-system v0.3 §6.5；规格页 02-applistrow，官方 item.tsx 的 slot 结构）。
 * onClick 非 null = 可点：clickable(role = Role.Button)（foundation clickable 默认 role=null，显式传，F15）
 * + minimumInteractiveComponentSize（F8 热区）。
 * selected 的 stateDescription 来自 catalog（F15，我们的增强，官方无）；仅 selected=true 时设置
 * （未选中/不可点行不播报噪音）。复合行 mergeDescendants = 一条朗读（§10.2）。
 */
@Composable
fun AppListRow(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppListRowVariant = AppListRowVariant.Default,
    size: AppListRowSize = AppListRowSize.Default,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    description: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val shape = RoundedCornerShape(AppTheme.radius.small)
    val padding: PaddingValues = when (size) {
        AppListRowSize.Default -> PaddingValues(horizontal = spacing.md, vertical = spacing.md)
        AppListRowSize.Sm -> PaddingValues(horizontal = spacing.md, vertical = spacing.controlPadding)
    }

    val background: Color = when (variant) {
        AppListRowVariant.Default -> Color.Transparent
        AppListRowVariant.Muted -> colors.muted
        AppListRowVariant.Outline -> colors.surface
    }
    val selectedDesc: String? = if (selected) stringResource(Res.string.state_selected) else null

    val rowModifier = modifier
        .fillMaxWidth()
        .background(background, shape)
        .then(
            if (variant == AppListRowVariant.Outline) Modifier.border(1.dp, colors.border, shape) else Modifier,
        )
        .then(
            if (onClick != null) {
                Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClick = onClick)
            } else Modifier,
        )
        .semantics(mergeDescendants = true) {
            if (selectedDesc != null) stateDescription = selectedDesc
        }
        .padding(padding)

    Column(modifier = rowModifier) {
        header?.invoke()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            leading?.invoke()
            Column(modifier = Modifier.weight(1f)) {
                title()
                description?.invoke()
            }
            trailing?.invoke()
        }
        footer?.invoke()
    }
}

/** 行媒体（官方 ItemMedia：图标容器）。 */
@Composable
fun AppListRowMedia(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) { icon?.invoke() }
}

/** 行分隔（官方 ItemSeparator；0.1.0 仅默认分隔线，AppSeparator 原子 0.2+）。 */
@Composable
fun AppListRowSeparator(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    Box(
        modifier.fillMaxWidth().padding(horizontal = AppTheme.spacing.md),
    ) {
        Box(
            Modifier.fillMaxWidth().padding(vertical = AppTheme.spacing.xxs).height(1.dp).background(colors.border),
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppListRowPreview() {
    AppTheme {
        AppListGroup {
            AppListRow(
                title = { androidx.compose.material3.Text("run") },
                description = { androidx.compose.material3.Text("move at a speed faster than a walk") },
                onClick = {},
            )
            AppListRow(title = { androidx.compose.material3.Text("port") }, selected = true, onClick = {})
        }
    }
}
