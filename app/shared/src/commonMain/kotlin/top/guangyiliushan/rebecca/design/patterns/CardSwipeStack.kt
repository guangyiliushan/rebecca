package top.guangyiliushan.rebecca.design.patterns

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import top.guangyiliushan.rebecca.design.components.AppCard
import top.guangyiliushan.rebecca.design.theme.AppTheme
import top.guangyiliushan.rebecca.design.theme.ThemeSettings
import top.guangyiliushan.rebecca.design.tokens.AppMotion

/** 拖拽锚点：左=不认识、中=原位、右=认识。 */
enum class SwipeAnchor { Left, Center, Right }

/**
 * 卡片滑动手势层（0.1.2 §卡片浏览；官方 `AnchoredDraggable` 原语，R5 行为收敛在此组件）。
 * 右滑 = 认识（true）、左滑 = 不认识（false）、松手回中位；阈值与速度由官方 flingBehavior 决定。
 * `state` 可注入（测试用 snapTo 驱动落点判定，不必真实拖拽注入）。
 * ⚠ 手势**必须**有常驻等效按钮（§10.5/§10.7）——由调用方提供，本组件只出手势与位移；
 * 翻面状态与键盘接入同样由调用方承担（见法典 §6.6 行）。
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardSwipeStack(
    onDecision: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    state: AnchoredDraggableState<SwipeAnchor> = remember { AnchoredDraggableState(SwipeAnchor.Center) },
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val thresholdPx = with(density) { AppMotion.swipeDecisionThreshold.toPx() }

    LaunchedEffect(thresholdPx) {
        state.updateAnchors(
            DraggableAnchors {
                SwipeAnchor.Left at -thresholdPx
                SwipeAnchor.Center at 0f
                SwipeAnchor.Right at thresholdPx
            },
        )
    }

    // 落点结算：滑到左/右锚点即产出判定，并回中位等待下一张
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }.collect { settled ->
            when (settled) {
                SwipeAnchor.Left -> {
                    onDecision(false)
                    state.snapTo(SwipeAnchor.Center)
                }
                SwipeAnchor.Right -> {
                    onDecision(true)
                    state.snapTo(SwipeAnchor.Center)
                }
                SwipeAnchor.Center -> Unit
            }
        }
    }

    val offsetX = state.offset.takeIf { !it.isNaN() } ?: 0f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(offsetX.roundToInt(), 0)
                }
            }
            .alpha(1f - (kotlin.math.abs(offsetX) / (thresholdPx * 4f)).coerceIn(0f, 0.3f))
            .anchoredDraggable(
                state = state,
                reverseDirection = false,
                orientation = Orientation.Horizontal,
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        content()
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun CardSwipeStackPreview() {
    AppTheme(ThemeSettings()) {
        CardSwipeStack(onDecision = {}) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Text("headword")
            }
        }
    }
}
