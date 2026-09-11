package top.guangyiliushan.rebecca.design.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeMode
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.design.tokens.AppColors

/**
 * 0.0.2 tokens 验证页（0.1.0 全量 showcase 的前身）。
 * 纯展示：状态提升，(settings, onModeChange)；只用 tokens 与 M3 原语。
 */
@Composable
fun TokensShowcase(
    settings: ThemeSettings,
    onModeChange: (ThemeMode) -> Unit,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    Column(
        modifier = Modifier
            .background(colors.background)
            .safeContentPadding()   // 顶部/底部安全区：避开状态栏/导航栏/刘海（0.0.3 起移交 AppScaffold）
            .verticalScroll(rememberScrollState())
            .padding(spacing.md)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        SectionTitle("Theme")
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            ThemeMode.entries.forEach { mode ->
                if (settings.mode == mode) {
                    Button(onClick = { onModeChange(mode) }) { Text(mode.name) }
                } else {
                    OutlinedButton(onClick = { onModeChange(mode) }) { Text(mode.name) }
                }
            }
        }
        Text(
            "family: ${settings.family.name}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onMuted,
        )

        SectionTitle("Color slots")
        ColorSlotRow(colors = settings.family.colors(dark = false), dark = false)
        ColorSlotRow(colors = settings.family.colors(dark = true), dark = true)

        SectionTitle("Spacing")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            listOf(
                "xs" to spacing.xs, "sm" to spacing.sm, "md" to spacing.md,
                "lg" to spacing.lg, "xl" to spacing.xl, "xxl" to spacing.xxl,
            ).forEach { (name, value) ->
                Box(Modifier.width(value).height(spacing.md).background(colors.primary))
                Text(name, style = MaterialTheme.typography.labelLarge, color = colors.onBackground)
            }
        }

        SectionTitle("Radius")
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            listOf(
                "extraSmall" to radius.extraSmall, "small" to radius.small, "medium" to radius.medium,
                "large" to radius.large, "extraLarge" to radius.extraLarge,
            )
                .forEach { (name, value) ->
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Box(
                            // 演示色块尺寸由间距令牌派生（xl×2），避免魔法 dp
                            Modifier
                                .size(spacing.xl * 2)
                                .clip(RoundedCornerShape(value))
                                .background(colors.accent)
                                .border(1.dp, colors.border, RoundedCornerShape(value)),
                        )
                        Text(name, style = MaterialTheme.typography.labelLarge, color = colors.onBackground)
                    }
                }
        }

        SectionTitle("Type")
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            listOf(
                "display" to MaterialTheme.typography.displayLarge,
                "headline" to MaterialTheme.typography.headlineMedium,
                "titleLg" to MaterialTheme.typography.titleLarge,
                "title" to MaterialTheme.typography.titleMedium,
                "titleSm" to MaterialTheme.typography.titleSmall,
                "bodyLg" to MaterialTheme.typography.bodyLarge,
                "body" to MaterialTheme.typography.bodyMedium,
                "bodySm" to MaterialTheme.typography.bodySmall,
                "label" to MaterialTheme.typography.labelLarge,
            ).forEach { (name, style) ->
                Text("$name — Rebecca tokens", style = style, color = colors.onBackground)
            }
        }

        SectionTitle("Content width")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            listOf(
                "reading" to AppTheme.contentWidth.reading,
                "default" to AppTheme.contentWidth.default,
                "wide" to AppTheme.contentWidth.wide,
            ).forEach { (name, width) ->
                Column {
                    Box(Modifier.width(width).height(spacing.xs).background(colors.ring))
                    Text(name, style = MaterialTheme.typography.labelLarge, color = colors.onBackground)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        color = AppTheme.colors.onBackground,
        modifier = Modifier.semantics { heading() },
    )
}

/** commonMain 无 Color.toHexString（该名撞 stdlib ByteArray 扩展）；手写 RGB→hex。 */
private fun colorHex(c: androidx.compose.ui.graphics.Color): String {
    fun byte(v: Float): Int = (v * 255).toInt().coerceIn(0, 255)
    fun hex(i: Int): String {
        val digits = "0123456789ABCDEF"
        return "${digits[i / 16]}${digits[i % 16]}"
    }
    return hex(byte(c.red)) + hex(byte(c.green)) + hex(byte(c.blue))
}

@Composable
private fun ColorSlotRow(colors: AppColors, dark: Boolean) {
    val spacing = AppTheme.spacing
    val slots = listOf(
        "background" to colors.background, "onBackground" to colors.onBackground,
        "surface" to colors.surface, "onSurface" to colors.onSurface,
        "muted" to colors.muted, "onMuted" to colors.onMuted,
        "primary" to colors.primary, "onPrimary" to colors.onPrimary,
        "accent" to colors.accent, "onAccent" to colors.onAccent,
        "destructive" to colors.destructive, "onDestructive" to colors.onDestructive,
        "success" to colors.success, "onSuccess" to colors.onSuccess,
        "warning" to colors.warning, "onWarning" to colors.onWarning,
        "border" to colors.border, "input" to colors.input, "ring" to colors.ring,
    )
    Text(
        if (dark) "dark" else "light",
        style = MaterialTheme.typography.titleSmall,
        color = AppTheme.colors.onBackground,
    )
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        slots.forEach { (name, color) ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Box(
                    Modifier
                        .size(spacing.lg)
                        .clip(RoundedCornerShape(AppTheme.radius.small))
                        .background(color)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(AppTheme.radius.small)),
                )
                Text(
                    "$name  #${colorHex(color)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.onBackground,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ShowcaseLightPreview() {
    AppTheme(ThemeSettings(mode = ThemeMode.Light)) {
        TokensShowcase(ThemeSettings(mode = ThemeMode.Light), onModeChange = {})
    }
}

@Preview
@Composable
private fun ShowcaseDarkPreview() {
    AppTheme(ThemeSettings(mode = ThemeMode.Dark)) {
        TokensShowcase(ThemeSettings(mode = ThemeMode.Dark), onModeChange = {})
    }
}
