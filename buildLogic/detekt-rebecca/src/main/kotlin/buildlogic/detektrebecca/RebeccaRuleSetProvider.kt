package buildlogic.detektrebecca

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

/**
 * detekt 2.0 规则集：工厂形态（RuleSetProvider.instance() 返回规则工厂，非实例）。
 * ⚠ 休眠状态（Plan B，grill Q2）：2.0.0-alpha.6 上 F6 规则无法可靠加载（2026-09-11 实测：
 * F1/F2 生效、F6 从未实例化），F 约束门禁现由 scripts/lint/f_gates.py 承担。
 * detekt 2.0 转正后迁移：改回此处装配 + 移除 f_gates.py。规则本体（RebeccaColorLiteral 等）保留不变。
 */
class RebeccaRuleSetProvider : RuleSetProvider {
    override val ruleSetId: RuleSetId = RuleSetId("rebecca")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::RebeccaColorLiteral,
            ::RebeccaRawDimension,
            ::RebeccaHardcodedText,
        ),
    )
}
