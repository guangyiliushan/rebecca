package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings

/**
 * 身份条（ui-design-survey 05 §Shared identity and status bar）：非卡片、非全局 chrome——
 * 由路由决定是否经 AppScaffold.topBar 安装（Study/Lists/Dictionary/Explore 装；Settings/Quiz 等不装）。
 * 只暴露身份与语言对轻状态；streak 等奖励指标不出现（归 LearningOverviewCard，避免重复）。
 * min-height 布局非硬高（200% 字体安全）；名字视觉截断、语义给全名；
 * 语言对视觉短标签 + 语义完整短语（"Learning English; native language Chinese"）。
 * 语言对点击留 0.1.4（语言选择 sheet）；onLanguageClick 为 null 时不可点、不播报可点语义。
 */
@Composable
fun AppIdentityBar(
    name: String,
    languagePairLabel: String,
    languagePairSemantics: String,
    modifier: Modifier = Modifier,
    nameSemantics: String? = null,
    onLanguageClick: (() -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // 头像占位（0.1.4 账号占位接真头像；匿名显示 "Local learner"）
        Box(
            modifier = Modifier
                .size(AppTheme.iconSize.Lg)
                .background(colors.accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null, // 装饰；身份由名字文本承担
                tint = colors.onAccent,
                modifier = Modifier.size(AppTheme.iconSize.Md),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (nameSemantics != null) {
                    Modifier.semantics { contentDescription = nameSemantics }
                } else Modifier,
            )
        }
        // 语言对：视觉短标签，语义完整短语；可点语义仅在 onLanguageClick 非 null 时存在。
        // 顺序：semantics（同节点合并）→ minimumInteractiveComponentSize（F8 48dp 热区，外层）→ clickable
        Text(
            text = languagePairLabel,
            style = MaterialTheme.typography.labelLarge,
            color = colors.primary,
            modifier = Modifier
                .semantics { contentDescription = languagePairSemantics }
                .then(
                    if (onLanguageClick != null) {
                        Modifier.minimumInteractiveComponentSize().clickable(role = Role.Button, onClick = onLanguageClick)
                    } else Modifier,
                ),
        )
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun AppIdentityBarPreview() {
    AppTheme(ThemeSettings()) {
        AppIdentityBar(
            name = "Local learner",
            languagePairLabel = "EN · 中文",
            languagePairSemantics = "Learning English; native language Chinese",
        )
    }
}
