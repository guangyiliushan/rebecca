package top.guangyiliushan.rebecca.design.showcase

/**
 * showcase 注册表（frontend-design-system v0.3 §6.7/§9.2，决策 D7）：
 * `design/components/` 目录下的组件必须在此登记，漏注册/僵尸条目即门禁失败（ShowcaseRegistryTest）。
 * COMPONENTS.md 由本注册表生成（单一真源）。
 *
 * @param component 组件名，与 `design/components/<component>.kt` 文件名一一对应
 * @param variants  公开变体枚举值（R2）
 * @param a11y      语义承诺（F15 声明）：role / stateDescription / 触控
 */
data class ShowcaseEntry(
    val component: String,
    val variants: List<String>,
    val a11y: List<String>,
)

val showcaseEntries: List<ShowcaseEntry> = listOf(
    ShowcaseEntry(
        component = "AppButton",
        variants = listOf("Primary", "Secondary", "Ghost", "Outline", "Destructive", "Link"),
        a11y = listOf("role=Button", "loading: spinner contentDescription(Loading) + disabled", "touch>=48dp"),
    ),
    ShowcaseEntry(
        component = "AppIconButton",
        variants = listOf("Sm", "Md", "Lg"),
        a11y = listOf("role=Button", "contentDescription required → semantics label", "touch>=48dp"),
    ),
    ShowcaseEntry(
        component = "AppSpinner",
        variants = listOf("Xs", "Sm", "Md", "Lg"),
        a11y = listOf("liveRegion=Polite + contentDescription（CMP Role 无 Status）"),
    ),
    ShowcaseEntry(
        component = "AppCard",
        variants = listOf("Elevated", "Outlined", "Filled"),
        a11y = listOf("clickable: role=Button（显式补，M3 可点 Card 默认无 role）", "touch>=48dp（M3 内建）"),
    ),
    ShowcaseEntry(
        component = "AppBadge",
        variants = listOf("Default", "Secondary", "Destructive", "Outline", "Ghost", "Link"),
        a11y = listOf("non-interactive（纯展示）", "已知差异：Link 与 Ghost 视觉同源"),
    ),
    ShowcaseEntry(
        component = "AppChip",
        variants = listOf("Filter", "Choice", "Assist"),
        a11y = listOf("role=Checkbox(M3 SelectableChip 语义)", "stateDescription(selected，来自 catalog)", "touch>=48dp"),
    ),
    ShowcaseEntry(
        component = "AppChipGroup",
        variants = listOf("Single/Separated", "Single/Connected", "Multiple/Separated"),
        a11y = listOf(
            "Single: selectableGroup()（§10.2 选择组）",
            "已知差异：roving focus 未实现（focusGroup() 不存在于 CMP 1.12.0，AndroidX 独有）",
            "已知差异：Connected 仅零间距连排，shadcn 首/尾圆角收拢未实现",
        ),
    ),
    ShowcaseEntry(
        component = "AppInput",
        variants = listOf("default", "error", "disabled"),
        a11y = listOf("EditableText", "error() 文案来自 AppFieldState.errors 首条（catalog）", "focus ring=ring 槽"),
    ),
    ShowcaseEntry(
        component = "AppField",
        variants = listOf("Vertical", "Horizontal"),
        a11y = listOf("label/description slot（样式由调用方）", "errors liveRegion=Assertive + 去重", "两种 orientation 均渲染 errors"),
    ),
    ShowcaseEntry(
        component = "AppListRow",
        variants = listOf("Default", "Outline", "Muted"),
        a11y = listOf("clickable: role=Button（显式）", "selected 时才播报 stateDescription", "mergeDescendants", "touch>=48dp"),
    ),
    ShowcaseEntry(
        component = "AppProgress",
        variants = listOf("Linear", "Circular", "determinate", "indeterminate"),
        a11y = listOf("progressBarRangeInfo（确定态）", "label→stateDescription（读屏专用，无视觉文本）"),
    ),
    // ---- overlays（design/overlays/）----
    ShowcaseEntry(
        component = "AppDialog",
        variants = listOf("Xs", "Md"),
        a11y = listOf("paneTitle（BasicAlertDialog 内建）", "Esc → onDismiss（onPreviewKeyEvent 桥接）", "focusable 初始焦点"),
    ),
    ShowcaseEntry(
        component = "AppAlertDialog",
        variants = listOf("Xs", "Md"),
        a11y = listOf("alertdialog pattern", "dismissOnClickOutside=false 固定", "必有 Action/Cancel"),
    ),
    ShowcaseEntry(
        component = "AppSheet",
        variants = listOf("bottom only"),
        a11y = listOf("ModalBottomSheet 内建 Scrim（更正 SH-2 误判）", "dragHandle 开关", "F17：无把手时调用方须放关闭按钮"),
    ),
    // ---- scaffold（design/scaffold/）----
    ShowcaseEntry(
        component = "AppTopBar",
        variants = listOf("Small", "Medium", "Large"),
        a11y = listOf("AppBarRow DSL 自动溢出", "overflowIndicator 自传（catalog 文案，规避 F6 平台泄漏）"),
    ),
)
