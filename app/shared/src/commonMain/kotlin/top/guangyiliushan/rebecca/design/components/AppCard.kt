package top.guangyiliushan.rebecca.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.theme.AppTheme

/** AppCard 变体（R2）。Filled 官方无对应物（v0.3 §6.5 注明），= muted 底自定。 */
enum class AppCardVariant { Elevated, Outlined, Filled }

/**
 * 卡片（frontend-design-system v0.3 §6.5；规格页 02-appcard）。onClick 非 null = 卡片按钮。
 * ⚠ M3 可点 Card 重载经 Surface(onClick) 且 foundation clickable 默认 role=null（已核源码）——
 * 故此处显式补 `role = Role.Button`（F15）。
 * 官方 CardHeader 的 container query（按自身宽度切换双列）v1 显式放弃：有 action 就右侧排（固定布局）。
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: AppCardVariant = AppCardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    // 官方 card: flex-col gap-6 py-6；CardHeader/Content/Footer 各 px-6
    val paddedContent: @Composable ColumnScope.() -> Unit = {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
            content()
        }
    }
    val cardModifier = if (onClick != null) {
        modifier.semantics { role = Role.Button }
    } else modifier

    if (onClick != null) {
        when (variant) {
            AppCardVariant.Elevated -> ElevatedCard(
                onClick = onClick,
                modifier = cardModifier,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface,
                ),
                content = paddedContent,
            )
            AppCardVariant.Outlined -> OutlinedCard(
                onClick = onClick,
                modifier = cardModifier,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface,
                ),
                content = paddedContent,
            )
            AppCardVariant.Filled -> Card(
                onClick = onClick,
                modifier = cardModifier,
                colors = CardDefaults.cardColors(
                    containerColor = colors.muted,
                    contentColor = colors.onMuted,
                ),
                content = paddedContent,
            )
        }
    } else {
        when (variant) {
            AppCardVariant.Elevated -> ElevatedCard(
                modifier = cardModifier,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface,
                ),
                content = paddedContent,
            )
            AppCardVariant.Outlined -> OutlinedCard(
                modifier = cardModifier,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface,
                ),
                content = paddedContent,
            )
            AppCardVariant.Filled -> Card(
                modifier = cardModifier,
                colors = CardDefaults.cardColors(
                    containerColor = colors.muted,
                    contentColor = colors.onMuted,
                ),
                content = paddedContent,
            )
        }
    }
}

/** 卡片头：标题 + 可选描述 + 可选右侧 action（官方 CardAction 的 Row/Column 等价实现，非 CSS grid）。 */
@Composable
fun AppCardHeader(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    Row(
        modifier = modifier.padding(horizontal = spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            title()
            description?.invoke()
        }
        action?.invoke()
    }
}

/** 卡片正文区（官方 CardContent 的 px-6）。 */
@Composable
fun AppCardContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.padding(horizontal = AppTheme.spacing.lg), content = content)
}

/** 卡片脚（官方 CardFooter）。 */
@Composable
fun AppCardFooter(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(modifier.padding(horizontal = AppTheme.spacing.lg), content = { content() })
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppCardPreview() {
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            AppCard {
                AppCardHeader(title = { Text("Title") }, description = { Text("Description") })
                AppCardContent { Text("Content") }
                AppCardFooter { Text("Footer") }
            }
            AppCard(variant = AppCardVariant.Outlined) { AppCardContent { Text("Outlined") } }
            AppCard(variant = AppCardVariant.Filled) { AppCardContent { Text("Filled") } }
        }
    }
}
