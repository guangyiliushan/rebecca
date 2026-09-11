# COMPONENTS.md — 组件目录（由 ShowcaseRegistry.kt 生成，勿手改）

> 单一真源：`design/showcase/ShowcaseRegistry.kt`。本文件由
> `scripts/lint/gen_components_md.py` 生成；AI/新人入口：先读本文件再写组件。

## 组件清单

### AppButton

- **变体**: Primary, Secondary, Ghost, Outline, Destructive, Link
- **语义承诺（F15）**: role=Button; loading: spinner contentDescription(Loading) + disabled; touch>=48dp

### AppIconButton

- **变体**: Sm, Md, Lg
- **语义承诺（F15）**: role=Button; contentDescription required → semantics label; touch>=48dp

### AppSpinner

- **变体**: Xs, Sm, Md, Lg
- **语义承诺（F15）**: liveRegion=Polite + contentDescription（CMP Role 无 Status）

### AppCard

- **变体**: Elevated, Outlined, Filled
- **语义承诺（F15）**: clickable: role=Button（显式补，M3 可点 Card 默认无 role）; touch>=48dp（M3 内建）

### AppBadge

- **变体**: Default, Secondary, Destructive, Outline, Ghost, Link
- **语义承诺（F15）**: non-interactive（纯展示）; 已知差异：Link 与 Ghost 视觉同源

### AppChip

- **变体**: Filter, Choice, Assist
- **语义承诺（F15）**: role=Checkbox(M3 SelectableChip 语义); stateDescription(selected，来自 catalog); touch>=48dp

### AppInput

- **变体**: default, error, disabled
- **语义承诺（F15）**: EditableText; error() 文案来自 AppFieldState.errors 首条（catalog）; focus ring=ring 槽

### AppField

- **变体**: Vertical, Horizontal
- **语义承诺（F15）**: label/description slot（样式由调用方）; errors liveRegion=Assertive + 去重; 两种 orientation 均渲染 errors

### AppListRow

- **变体**: Default, Outline, Muted
- **语义承诺（F15）**: clickable: role=Button（显式）; selected 时才播报 stateDescription; mergeDescendants; touch>=48dp

### AppProgress

- **变体**: Linear, Circular, determinate, indeterminate
- **语义承诺（F15）**: progressBarRangeInfo（确定态）; label→stateDescription（读屏专用，无视觉文本）

### AppDialog

- **变体**: Xs, Md
- **语义承诺（F15）**: paneTitle（BasicAlertDialog 内建）; Esc → onDismiss（onPreviewKeyEvent 桥接）; focusable 初始焦点

### AppAlertDialog

- **变体**: Xs, Md
- **语义承诺（F15）**: alertdialog pattern; dismissOnClickOutside=false 固定; 必有 Action/Cancel

### AppSheet

- **变体**: bottom only
- **语义承诺（F15）**: ModalBottomSheet 内建 Scrim（更正 SH-2 误判）; dragHandle 开关; F17：无把手时调用方须放关闭按钮

### AppTopBar

- **变体**: Small, Medium, Large
- **语义承诺（F15）**: AppBarRow DSL 自动溢出; overflowIndicator 自传（catalog 文案，规避 F6 平台泄漏）
