## 描述

<!-- 一句话：改了什么、为什么。关联 issue 编号。 -->

## 检查清单

### CI 自动（无需勾选，漏了直接红）

- F1/F2/F6 门禁（`scripts/lint/f_gates.py`）
- F16 对比度（`scripts/a11y/contrast.py`）
- showcase 门禁（ShowcaseRegistryTest：漏注册/僵尸条目/多预览注解）
- 编译 + 全量测试（:core:allTests + :app:shared:jvmTest）

### 人工项（按改动范围勾选）

- [ ] **F3** 视觉组件只在 `design/`；feature 内无私造样式组件
- [ ] **F4** 变体枚举参数化，未复制组件改样式
- [ ] **F5** 组件签名遵守 R1-R8（状态提升、slot、modifier/style 透传）
- [ ] **F7** 无 `Left/Right` 系 API 与物理方向假设（RTL）
- [ ] **F8** 可点元素触控热区 ≥48dp；图标按钮 contentDescription 来自 catalog
- [ ] **F9** 屏幕不读窗口尺寸；断点逻辑只在 AppScaffold
- [ ] **F12** 跨屏参数走路由序列化，无全局单例传参
- [ ] **F13** 组件无业务/数据获取/导航；ViewModel 无 Composable
- [ ] **F14** 预留功能按 capabilities 渲染，无 `if(false)` 散落
- [ ] **F15** 交互元素有 role + 可读标签；状态控件带 stateDescription
- [ ] **F17** 手势有等效替代（按钮/菜单/键盘）；动效遵守减弱动效

### showcase 目视（组件类改动必做）

- [ ] 变体齐全、light/dark 正常
- [ ] fontScale 2.0 无截断

### 新组件（缺一不算完成，§6.7）

- [ ] 单文件于 `design/{components,overlays,scaffold}`，R1-R8 合规
- [ ] `@PreviewLightDark` + `@PreviewFontScale`
- [ ] 语义断言测试（commonTest）
- [ ] 触控热区 ≥48dp 断言
- [ ] 已登记 `ShowcaseRegistry.kt`
